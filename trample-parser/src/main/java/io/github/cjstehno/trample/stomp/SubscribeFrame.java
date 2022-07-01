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

import static io.github.cjstehno.trample.stomp.StompHeaders.DESTINATION;
import static io.github.cjstehno.trample.stomp.StompHeaders.ID;

@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
public class SubscribeFrame extends BaseFrame implements BaseFrame.ClientFrame {

    public static final String COMMAND = "SUBSCRIBE";

    public SubscribeFrame() {
        super(COMMAND);
    }

    public SubscribeFrame(final String destination, final String id) {
        this();
        setDestination(destination);
        setId(id);
    }

    public void setDestination(final String destination) {
        setHeader(DESTINATION, destination);
    }

    public String getDestination() {
        return getHeader(DESTINATION);
    }

    public void setId(final String id) {
        setHeader(ID, id);
    }

    public String getId() {
        return getHeader(ID);
    }
}
