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

import lombok.EqualsAndHashCode;
import lombok.ToString;

import static io.github.cjstehno.trample.stomp.FrameType.Type.CLIENT;
import static io.github.cjstehno.trample.stomp.StompHeaders.ACCEPT_VERSION;
import static io.github.cjstehno.trample.stomp.StompHeaders.HOST;

@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true) @FrameType(CLIENT)
public final class StompFrame extends BaseFrame {

    public static final String COMMAND = "STOMP";

    public StompFrame() {
        super(COMMAND);
    }

    public StompFrame(final String host, final String acceptVersion) {
        this();
        setHost(host);
        setAcceptVersion(acceptVersion);
    }

    public void setHost(final String value) {
        setHeader(HOST, value);
    }

    public String getHost() {
        return getHeader(HOST);
    }

    public void setAcceptVersion(final String value) {
        setHeader(ACCEPT_VERSION, value);
    }

    public String getAcceptVersion() {
        return getHeader(ACCEPT_VERSION);
    }
}
