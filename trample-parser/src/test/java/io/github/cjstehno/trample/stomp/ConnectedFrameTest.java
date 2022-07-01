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

import lombok.val;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.github.cjstehno.trample.stomp.StompHeaders.VERSION;
import static io.github.cjstehno.trample.testing.Checks.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ConnectedFrameTest {

    private static ConnectedFrame connectedFrame() {
        return new ConnectedFrame("1.2");
    }

    @Test void properties() {
        val frame = connectedFrame();

        assertEquals("CONNECTED", frame.getCommand());

        assertEquals(1, frame.getHeaders().size());
        assertEquals("1.2", frame.getHeaders().get(VERSION));
        assertEquals("1.2", frame.getHeader(VERSION));
        assertEquals("1.2", frame.getVersion());

        assertEquals("", frame.getBody());
    }

    @Test void settingVersion() {
        val frame = new ConnectedFrame();
        assertNull(frame.getVersion());

        frame.setVersion("1.1");
        assertEquals("1.1", frame.getVersion());

        // should ignore setting once set
        frame.setVersion("1.0");
        assertEquals("1.1", frame.getVersion());
    }

    @Test void equalsAndHash() {
        checkEqualsAndHashCode(connectedFrame(), connectedFrame());
    }

    @Test void string() {
        checkToString(
            "ConnectedFrame(super=BaseFrame(command=CONNECTED, headers={version=1.2}, body=))",
            connectedFrame()
        );
    }

    @Test void readWrite() throws IOException {
        checkFrameWriteRead(
            connectedFrame(),
            """
                CONNECTED
                version:1.2
                            
                \0"""
        );
    }

    @Test void read() throws IOException {
        checkFrameRead(
            new ConnectedFrame("1.2"),
            """
                CONNECTED
                version:1.2
                            
                \0"""
        );

        checkFrameRead(
            new ConnectedFrame("1.2"),
            """
                version:1.2
                            
                \0"""
        );
    }
}