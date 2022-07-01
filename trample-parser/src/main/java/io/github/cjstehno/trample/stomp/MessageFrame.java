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

import static io.github.cjstehno.trample.stomp.StompHeaders.*;
import static java.lang.Integer.parseInt;

@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
public class MessageFrame extends BaseFrame implements BaseFrame.ServerFrame {

    public static final String COMMAND = "MESSAGE";
    public static final String MESSAGE_ID_HEADER = "message-id";
    public static final String ACK_HEADER = "ack";

    public MessageFrame() {
        super(COMMAND);
    }

    public MessageFrame(final String destination, final String messageId) {
        this();
        setDestination(destination);
        setMessageId(messageId);
    }

    public void setMessageId(final String id) {
        setHeader(MESSAGE_ID_HEADER, id);
    }

    public String getMessageId() {
        return getHeader(MESSAGE_ID_HEADER);
    }

    public void setDestination(final String destination) {
        setHeader(DESTINATION, destination);
    }

    public String getDestination() {
        return getHeader(DESTINATION);
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

    public void setAck(final String ack) {
        setHeader(ACK_HEADER, ack);
    }

    public String getAck() {
        return getHeader(ACK_HEADER);
    }

    @Override public String getBody() {
        return super.getBody();
    }

    @Override public void setBody(final String body) {
        super.setBody(body);
    }
}
