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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
        try (val reader = new BufferedReader(new StringReader(source))) {
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
        val lineReader = new LineReader(reader);
        var line = lineReader.readLine();

        log.info("Line: {}", line);

        while (line != null) {
            val trimmed = line.trim();

            if (!trimmed.isEmpty()) {
                val frame = switch (trimmed) {
                    case StompConnectedFrame.COMMAND -> parseFrame(new StompConnectedFrame(), lineReader);
                    default -> throw new UnsupportedOperationException("Unsupported command: " + line);
                };

                if (mode.allowsFrame(frame)) {
                    log.info("Finished frame: {}", frame);
                    collector.accept(frame);
                } else if (ignoreIllegalFrame) {
                    // FIXME: ignore it
                    log.debug("Parsed illegal frame ({}) for mode ({}) - ignoring it.", frame, mode);
                } else {
                    log.debug("Parsed illegal frame ({}) for mode ({}) - throwing exception.", frame, mode);
                    // TODO: custom exception - IllegalFrameException
                    throw new IOException("Illegal frame (" + frame.getCommand() + ") for mode (" + mode + ").");
                }
            }

            line = lineReader.readLine();
        }
    }

    private StompFrame parseFrame(final StompFrame frame, final LineReader lineReader) throws IOException {
        log.info("Parsing-Frame: {}", frame);

        // headers
        log.info("... parsing headers...");
        var line = lineReader.readLine();

        while (!line.isBlank()) {
            val headerName = line.substring(0, line.indexOf(":"));
            val headerValue = line.substring(line.indexOf(":") + 1);
            frame.setHeader(headerName, headerValue);

            line = lineReader.readLine();
        }

        // body
        log.info("... parsing body....");
        var buffer = new StringBuilder();

        var ch = lineReader.read();
        while (ch != '\0') {
            if (ch != '\r') {
                buffer.append(ch);
            }
            ch = lineReader.read();
        }

        frame.setBody(buffer.toString());

        return frame;
    }

    private static class LineReader {
        private final BufferedReader reader;

        private LineReader(final Reader reader) {
            this.reader = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
        }

        String readLine() throws IOException {
            return reader.readLine();
        }

        char read() throws IOException {
            return (char) reader.read();
        }
    }
}
