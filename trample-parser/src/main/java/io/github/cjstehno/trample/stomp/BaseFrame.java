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

import io.github.cjstehno.trample.parser.StompParser;
import lombok.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE) @ToString @EqualsAndHashCode
public abstract sealed class BaseFrame permits AbortFrame, AckFrame, BeginFrame, CommitFrame, ConnectFrame, ConnectedFrame, DisconnectFrame, ErrorFrame, MessageFrame, NackFrame, ReceiptFrame, SendFrame, StompFrame, SubscribeFrame, UnsubscribeFrame {

    @Getter private final String command;
    @Getter private final Map<String, String> headers = new LinkedHashMap<>();
    @Getter(PACKAGE) @Setter(PACKAGE) private String body = "";

    // NOTE: only first value of duplicate header is stored
    public void setHeader(final String name, final String value) {
        if (!headers.containsKey(name)) {
            headers.put(name, value);
        }
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    // NOTE: the command line is optional (may have been parsed off already)
    public BaseFrame readFrom(final Reader reader) throws IOException {
        val lineReader = StompParser.ensureBuffered(reader);

        var line = lineReader.readLine();

        // command (may not be present)
        if (line.trim().equals(command)) {
            // skip and continue
            line = lineReader.readLine();
        }

        // headers
        while (!line.isBlank()) {
            val headerName = line.substring(0, line.indexOf(":"));
            val headerValue = line.substring(line.indexOf(":") + 1);
            setHeader(headerName, headerValue);

            line = lineReader.readLine();
        }

        // body
        var buffer = new StringBuilder();

        var ch = lineReader.read();
        while (ch != '\0') {
            if (ch != '\r') {
                buffer.append(ch);
            }
            ch = lineReader.read();
        }

        setBody(buffer.toString());

        return this;
    }

    public void writeTo(final Writer writer) throws IOException {
        // command
        writer.write(command);
        writer.write('\n');

        // headers
        for (val header : headers.entrySet()) {
            writer.write(header.getKey());
            writer.write(':');
            writer.write(header.getValue());
            writer.write('\n');
        }
        writer.write('\n');

        // body
        writer.write(body);

        writer.write('\0');
    }

    public String write() throws IOException {
        try (val writer = new StringWriter()) {
            writeTo(writer);
            return writer.toString();
        }
    }
}
