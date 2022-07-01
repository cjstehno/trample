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
package io.github.cjstehno.trample.parser;

import io.github.cjstehno.trample.stomp.ConnectedFrame;
import io.github.cjstehno.trample.parser.StompParser;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StompParserTest {

    // FIXME: test server, client and all modes
    // FIXME: test ignore/throw illegal
    // FIXME: test all message types

    // FIXME: need to support serializing (writing out) frames to string

    @Test void parsingSingleMessage() throws IOException {
        val parser = StompParser.forServerFrames();

        val source = """
            CONNECTED
            version:1.2
                        
            \0
            """;

        val frames = parser.parse(source);

        assertEquals(1, frames.size());
        assertEquals(new ConnectedFrame("1.2"), frames.get(0));
    }

    // FIXME: move this to frame test
    @Test void writingConnected() throws IOException {
        val frame = new ConnectedFrame("1.2");
        val string = frame.write();

        assertEquals(
            """
                CONNECTED
                version:1.2
                            
                \0""",
            string
        );
    }

}