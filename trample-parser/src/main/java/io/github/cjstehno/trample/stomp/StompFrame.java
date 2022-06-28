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

import lombok.*;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE) @ToString @EqualsAndHashCode
abstract class StompFrame {

    // FIXME: test each frame type reading and writing

    @Getter private final String command;
    @Getter private final Map<String, String> headers = new LinkedHashMap<>();
    @Getter(PACKAGE) @Setter(PACKAGE) private String body = "";

    public void setHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public static interface StompClientFrame {
        // FIXME: userful?
    }

    public static interface StompServerFrame {
        // FIXME: userful?
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
