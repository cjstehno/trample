/**
 * Copyright (C) 2022 Christopher J. Stehno
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.cjstehno.trample.client;

import io.github.cjstehno.trample.parser.StompParser;
import io.github.cjstehno.trample.stomp.BaseFrame;
import io.github.cjstehno.trample.stomp.ConnectFrame;
import io.github.cjstehno.trample.stomp.ConnectedFrame;
import io.github.cjstehno.trample.stomp.FrameType;
import io.github.cjstehno.trample.stomp.FrameType.Type;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static io.github.cjstehno.trample.stomp.FrameType.Type.CLIENT;
import static java.nio.charset.StandardCharsets.US_ASCII;

// FIXME: this is the low-level client that just directly send/recive frames
@RequiredArgsConstructor @Slf4j
public class StompClient {

    // FIXME: SSL support

    private final String host;
    private final int port;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Map<Class<?>, List<Consumer<BaseFrame>>> listeners = new HashMap<>();
    private Socket socket;
    private Writer output;

    public void connect() throws IOException {
        socket = new Socket(host, port);
        output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));

        // listen for connected
        on(ConnectedFrame.class, frame -> {
            log.info("CONNECTED: {}", frame);
        });

        val waiter = new CountDownLatch(1);

        // start receiver
        executor.submit(() -> {
            val parser = StompParser.forServerFrames();

            try (val reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII))) {
                waiter.countDown();

                parser.read(reader, this::notifyListeners);

            } catch (Exception e) {
                log.error("Error: {}", e.getMessage(), e);
            }
        });

        // wait for stuff to be started
        try {
            waiter.await();
        } catch (InterruptedException e) {
            log.error("Exception: {}", e.getMessage(), e);
        }

        // send the connect frame
        // TODO: make these configurable
        val connectFrame = new ConnectFrame("locahost", "1.0,1.1,1.2");
        send(connectFrame);

        log.info("Initialized.");
    }

    private void notifyListeners(final BaseFrame frame) {
        if (listeners.containsKey(frame.getClass())) {
            listeners.get(frame.getClass()).forEach(handler -> handler.accept(frame));
        }
    }

    public void send(final BaseFrame frame) throws IOException {
        verifyFrameType(frame, CLIENT);

        log.info("Sending outbound frame: {}", frame);
        frame.writeTo(output);
        output.flush();
    }

    public void on(final Class<? extends BaseFrame> frameType, final Consumer<BaseFrame> handler) {
        listeners.computeIfAbsent(frameType, key -> new LinkedList<>()).add(handler);
    }

    public void disconnect() throws IOException {
        // FIXME: send disconnect message (and await ACK)

        if (socket != null) {
            socket.close();
        }
        log.info("Disconnected.");
    }

    private static void verifyFrameType(final BaseFrame frame, final Type expectedType) throws IOException {
        if (frame.getClass().getAnnotation(FrameType.class).value() != expectedType) {
            throw new IOException("Wrong frame type: " + frame);
        }
    }
}