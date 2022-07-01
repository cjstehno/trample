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

public interface StompHeaders {

    String VERSION = "version";
    String ACCEPT_VERSION = "accept-version";
    String HOST = "host";
    String RECEIPT_ID = "receipt-id";
    String RECEIPT = "receipt";
    String DESTINATION = "destination";
    String ID = "id";
    String TRANSACTION = "transaction";
    String CONTENT_TYPE = "content-type";
    String CONTENT_LENGTH = "content-length";
    String LOGIN = "login";
    String PASSCODE = "passcode";
    String HEART_BEAT = "heart-beat";
    String SESSION = "session";
    String SERVER = "server";
}
