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

        /*
       FIXME: impl
       - client
           SEND
       - server
           MESSAGE
           ERROR
    */

    private static final Map<String, Supplier<StompFrame>> FRAMES = new HashMap<>() {{
        put(StompConnectedFrame.COMMAND, StompConnectedFrame::new);
        put(StompConnectFrame.COMMAND, StompConnectFrame::new);
        put(StompStompFrame.COMMAND, StompStompFrame::new);
        put(StompReceiptFrame.COMMAND, StompReceiptFrame::new);
        put(StompDisconnectFrame.COMMAND, StompDisconnectFrame::new);
        put(StompSubscribeFrame.COMMAND, StompSubscribeFrame::new);
        put(StompUnsubscribeFrame.COMMAND, StompUnsubscribeFrame::new);
        put(StompAckFrame.COMMAND, StompAckFrame::new);
        put(StompNackFrame.COMMAND, StompNackFrame::new);
        put(StompBeginFrame.COMMAND, StompBeginFrame::new);
        put(StompCommitFrame.COMMAND, StompCommitFrame::new);
        put(StompAbortFrame.COMMAND, StompAbortFrame::new);
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
    public List<StompFrame> parse(final String source) throws IOException {
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
    public List<StompFrame> parse(final Reader reader) throws IOException {
        val frames = new ArrayList<StompFrame>();
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
    public void read(final Reader reader, final Consumer<StompFrame> collector) throws IOException {
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

        private static final int READ_LIMIT = 15;
        private final BufferedReader reader;

        @Override public boolean hasNext() {
            return peekLine() != null;
        }

        @Override public String next() {
            return peekLine();
        }

        private String peekLine() {
            try {
                reader.mark(READ_LIMIT);
                val line = reader.readLine();
                reader.reset();
                return line;

            } catch (IOException ioe) {
                // FIXME: other?
                throw new RuntimeException(ioe);
            }
        }
    }
}
