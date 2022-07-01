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

import io.github.cjstehno.trample.stomp.BaseFrame;
import io.github.cjstehno.trample.stomp.FrameType;
import io.github.cjstehno.trample.stomp.FrameType.Type;
import lombok.val;

/**
 * TODO: document
 */
enum ParserMode {

    CLIENT, SERVER, ALL;

    boolean allowsFrame(final BaseFrame frame) {
        val frameClass = frame.getClass();
        return switch (this) {
            case CLIENT -> extractFrameType(frameClass) == Type.CLIENT;
            case SERVER -> extractFrameType(frameClass) == Type.SERVER;
            default -> true;
        };
    }

    private static Type extractFrameType(final Class<?> frameClass) {
        return frameClass.getAnnotation(FrameType.class).value();
    }
}
