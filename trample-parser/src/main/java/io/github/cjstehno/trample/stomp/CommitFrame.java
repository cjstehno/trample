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

import static io.github.cjstehno.trample.stomp.StompHeaders.TRANSACTION;

@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
public class CommitFrame extends BaseFrame implements BaseFrame.ClientFrame {

    public static final String COMMAND = "COMMIT";

    public CommitFrame() {
        super(COMMAND);
    }

    public CommitFrame(final String transaction) {
        this();
        setTransaction(transaction);
    }

    public void setTransaction(final String transaction) {
        setHeader(TRANSACTION, transaction);
    }

    public String getTransaction() {
        return getHeader(TRANSACTION);
    }
}
