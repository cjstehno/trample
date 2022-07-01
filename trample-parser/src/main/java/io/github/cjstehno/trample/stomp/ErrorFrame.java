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
import lombok.val;

import static io.github.cjstehno.trample.stomp.FrameType.Type.SERVER;
import static io.github.cjstehno.trample.stomp.StompHeaders.CONTENT_LENGTH;
import static io.github.cjstehno.trample.stomp.StompHeaders.CONTENT_TYPE;
import static java.lang.Integer.parseInt;

@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true) @FrameType(SERVER)
public final class ErrorFrame extends BaseFrame {

    public static final String COMMAND = "ERROR";
    public static final String MESSAGE_HEADER = "message";

    public ErrorFrame() {
        super(COMMAND);
    }

    public void setMessage(final String message) {
        setHeader(MESSAGE_HEADER, message);
    }

    public String getMessage() {
        return getHeader(MESSAGE_HEADER);
    }

    public void setContentType(final String value) {
        setHeader(CONTENT_TYPE, value);
    }

    public String getContentType() {
        return getHeader(CONTENT_TYPE);
    }

    public void setContentLength(final int length) {
        setHeader(CONTENT_LENGTH, String.valueOf(length));
    }

    public Integer getContentLength() {
        val length = getHeader(CONTENT_LENGTH);
        return length != null ? parseInt(length) : null;
    }

    @Override public String getBody() {
        return super.getBody();
    }

    @Override public void setBody(final String body) {
        super.setBody(body);
    }
}
