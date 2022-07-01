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

import static io.github.cjstehno.trample.stomp.StompHeaders.RECEIPT_ID;

@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
public class ReceiptFrame extends BaseFrame implements BaseFrame.ServerFrame {

    public static final String COMMAND = "RECEIPT";


    public ReceiptFrame() {
        super(COMMAND);
    }

    public ReceiptFrame(final String receiptId) {
        this();
        setReceiptId(receiptId);
    }

    public void setReceiptId(final String value) {
        setHeader(RECEIPT_ID, value);
    }

    public String getReceiptId() {
        return getHeader(RECEIPT_ID);
    }
}
