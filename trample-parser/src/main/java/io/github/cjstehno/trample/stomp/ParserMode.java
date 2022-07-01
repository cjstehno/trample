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

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

/**
 * TODO: document
 */
@RequiredArgsConstructor(access = PRIVATE)
enum ParserMode {

    CLIENT(BaseFrame.ClientFrame.class),

    SERVER(BaseFrame.ServerFrame.class),

    ALL(BaseFrame.class);

    @Getter private final Class<?> frameClass;

    boolean allowsFrame(final BaseFrame frame) {
        return frameClass.isAssignableFrom(frame.getClass());
    }
}
