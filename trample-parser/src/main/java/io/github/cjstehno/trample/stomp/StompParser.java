/**
 * Copyright (C) 2022 Christopher J. Stehno
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.cjstehno.trample.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.github.cjstehno.trample.stomp.ParserMode.*;
import static lombok.AccessLevel.PRIVATE;

/**
 * TODO: document
 * ... is reusable and thread-safe
 *
 * @apiNote <a href="https://stomp.github.io/">Stomp Specification</a>
 */
@RequiredArgsConstructor(access = PRIVATE) @Slf4j
public class StompParser {

    // FIXME: sensible logging

    private static final Map<String, Supplier<BaseFrame>> FRAMES = new HashMap<>() {{
        put(ConnectedFrame.COMMAND, ConnectedFrame::new);
        put(ConnectFrame.COMMAND, ConnectFrame::new);
        put(StompFrame.COMMAND, StompFrame::new);
        put(ReceiptFrame.COMMAND, ReceiptFrame::new);
        put(DisconnectFrame.COMMAND, DisconnectFrame::new);
        put(SubscribeFrame.COMMAND, SubscribeFrame::new);
        put(UnsubscribeFrame.COMMAND, UnsubscribeFrame::new);
        put(AckFrame.COMMAND, AckFrame::new);
        put(NackFrame.COMMAND, NackFrame::new);
        put(BeginFrame.COMMAND, BeginFrame::new);
        put(CommitFrame.COMMAND, CommitFrame::new);
        put(AbortFrame.COMMAND, AbortFrame::new);
        put(SendFrame.COMMAND, SendFrame::new);
        put(MessageFrame.COMMAND, MessageFrame::new);
        put(ErrorFrame.COMMAND, ErrorFrame::new);
    }};
    private final ParserMode mode;
    private boolean ignoreIllegalFrame = false;

    /**
     * Creates a STOMP parser instance for extracting the Client frames - those frames SENT by the client. If you are
     * writing a server, you will want to parse these frames.
     */
    public static StompParser forClientFrames() {
        return new StompParser(CLIENT);
    }

    /**
     * Creates a STOMP parser instance for extracting the Server frames - those frame SENT by the server. If you are
     * writing a client, you will want to parse these frames.
     */
    public static StompParser forServerFrames() {
        return new StompParser(SERVER);
    }

    /**
     * Creates a STOMP parser instance for extracting the both the Client and Server frames.
     */
    public static StompParser forAllFrames() {
        return new StompParser(ALL);
    }

    /**
     * If a parsed frame not allowed by the parsing mode (Client, Server, or All), it will throw an exception by
     * default. Setting this property to <code>true</code> will cause the illegal frame to be ignored.
     *
     * @param ignore whether to ignore a frame that is not allowed by the mode.
     * @return a reference to this parser.
     */
    public StompParser ignoreIllegalFrame(final boolean ignore) {
        ignoreIllegalFrame = ignore;
        return this;
    }

    /**
     * Parses the frames for the configured mode contained in the <code>source</code> string, returning a list of the
     * extracted frames.
     *
     * @param source the frame source string
     * @return a list of parsed frames
     * @throws IOException if there is a problem parsing the source
     */
    public List<BaseFrame> parse(final String source) throws IOException {
        try (val reader = new BufferedReader(new StringReader(source), Math.min(source.length(), 8192))) {
            return parse(reader);
        }
    }

    /**
     * Parses the frames for the configured mode provided by the <code>Reader</code>, returning a list of the
     * extracted frames.
     *
     * @param reader the Reader containing the frame information
     * @return a list of parsed frames
     * @throws IOException if there is a problem parsing the frames in the reader
     */
    public List<BaseFrame> parse(final Reader reader) throws IOException {
        val frames = new ArrayList<BaseFrame>();
        read(reader, frames::add);
        return frames;
    }

    /**
     * Reads and extracts the frames from the <code>Reader</code> and publishes them to the provided
     * <code>Consumer</code> as they are parsed.
     *
     * @param reader    the Reader containing the frame information
     * @param collector the consumer of the extracted frames
     * @throws IOException if there is a problem parsing the frames in the reader
     */
    public void read(final Reader reader, final Consumer<BaseFrame> collector) throws IOException {
        val lineReader = ensureBuffered(reader);
        val lineIterator = new LineIterator(lineReader);

        while (lineIterator.hasNext()) {
            val line = lineIterator.next();
            val command = line.trim();

            if (!command.isEmpty()) {
                val frame = FRAMES.getOrDefault(
                        command,
                        () -> {
                            throw new RuntimeException("Unexpected frame command: " + command);
                        }
                    )
                    .get()
                    .readFrom(lineReader);

                if (mode.allowsFrame(frame)) {
                    log.info("Parsed frame: {}", frame);
                    collector.accept(frame);

                } else if (ignoreIllegalFrame) {
                    // we're just going to ignore it
                    log.info("Parsed illegal frame ({}) for mode ({}) - ignoring it.", frame, mode);

                } else {
                    log.debug("Parsed illegal frame ({}) for mode ({}) - throwing exception.", frame, mode);
                    // TODO: custom exception - IllegalFrameException
                    throw new IOException("Illegal frame (" + frame.getCommand() + ") for mode (" + mode + ").");
                }
            }
        }
    }

    // FIXME: move to util
    public static BufferedReader ensureBuffered(final Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    @RequiredArgsConstructor(access = PRIVATE)
    private static class LineIterator implements Iterator<String> {

        private final BufferedReader reader;
        private String line;

        @Override public boolean hasNext() {
            try {
                line = reader.readLine();
                return line != null;
            } catch (IOException ioe) {
                // FIXME: better?
                throw new RuntimeException(ioe);
            }
        }

        @Override public String next() {
            return line;
        }
    }
}
