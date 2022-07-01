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
package io.github.cjstehno.trample.testing;

import io.github.cjstehno.trample.stomp.BaseFrame;
import io.github.cjstehno.trample.stomp.ConnectedFrame;
import lombok.val;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface Checks {

    static void checkEqualsAndHashCode(final Object instanceA, final Object instanceB) {
        assertEquals(instanceA, instanceA);
        assertEquals(instanceB, instanceB);
        assertEquals(instanceA, instanceB);
        assertEquals(instanceB, instanceA);
        assertEquals(instanceA.hashCode(), instanceA.hashCode());
        assertEquals(instanceB.hashCode(), instanceB.hashCode());
        assertEquals(instanceA.hashCode(), instanceB.hashCode());
        assertEquals(instanceB.hashCode(), instanceA.hashCode());
    }

    static void checkToString(final String expected, final Object obj) {
        assertEquals(expected, obj.toString());
    }

    static void checkFrameWriteRead(final BaseFrame frame, final String message) throws IOException {
        // write the frame
        val written = frame.write();

        // compare to string
        assertEquals(message, written);

        // read frame
        val other = new ConnectedFrame();
        other.readFrom(new StringReader(written));

        // compare to original
        assertEquals(frame, other);
    }

    static void checkFrameRead(final BaseFrame expectedFrame, final String message) throws IOException {
        val frame = new ConnectedFrame();
        frame.readFrom(new StringReader(message));
        assertEquals(expectedFrame, frame);
    }
}

