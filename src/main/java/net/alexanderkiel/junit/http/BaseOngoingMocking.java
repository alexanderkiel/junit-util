/******************************************************************************
 * Copyright 2010 Alexander Kiel                                              *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package net.alexanderkiel.junit.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.logging.Level.FINER;

/**
 * @author Alexander Kiel
 */
abstract class BaseOngoingMocking implements OngoingMocking, HttpHandler {

    static final int BUFFER_SIZE = 1024;

    private static final Logger LOGGER = Logger.getLogger(BaseOngoingMocking.class.getName());
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final byte[] buffer;
    private Response response;
    private BasicAuthToken basicAuthToken;

    BaseOngoingMocking() {
        buffer = new byte[BUFFER_SIZE];
    }

    public OngoingMocking willRespond(Response response) {
        this.response = response;
        return this;
    }

    public OngoingMocking withBasicAuth(String username, String password) {
        basicAuthToken = new BasicAuthToken(username, password);
        return this;
    }

    void setResponseHeaders(Headers headers) {
        headers.putAll(response.getHeaders());
        if (response.hasBody()) {
            headers.set("Content-Type", response.getContentType());
        }
    }

    void sendResponseHeaders(HttpExchange httpExchange) throws IOException {
        if (response.hasBody()) {
            httpExchange.sendResponseHeaders(response.getStatusCode(), response.getBodyLength());
        } else {
            httpExchange.sendResponseHeaders(response.getStatusCode(), -1);
        }
    }

    void sendResponseBody(OutputStream responseBodyOutputStream) throws IOException {
        if (response.hasBody()) {
            copyBody(responseBodyOutputStream);
        } else {
            responseBodyOutputStream.close();
        }
    }

    private void copyBody(OutputStream responseBodyOutputStream) throws IOException {
        BufferedInputStream inputStream = new BufferedInputStream(response.getBodyInputStream());
        try {
            int length = 0;
            while (length >= 0) {
                length = inputStream.read(buffer);
                responseBodyOutputStream.write(buffer, 0, length);
            }
            responseBodyOutputStream.close();
        } finally {
            inputStream.close();
        }
    }

    protected void checkAuthorisation(HttpExchange httpExchange) {
        if (basicAuthToken != null) {
            Headers requestHeaders = httpExchange.getRequestHeaders();
            if (requestHeaders.containsKey(AUTHORIZATION_HEADER)) {
                String authHeader = requestHeaders.getFirst(AUTHORIZATION_HEADER);
                if (!authHeader.equals(basicAuthToken.getAuthHeaderValue())) {
                    response = new EmptyResponse(403);
                }
            } else {
                response = new EmptyResponse(401);
            }
        }
    }

    protected static void logRequest(HttpExchange httpExchange) {
        if (LOGGER.isLoggable(FINER)) {
            String contentType = httpExchange.getRequestHeaders().getFirst("Content-Type");
            LOGGER.finer(format("Handle request: %s %s, Content-Type: %s", httpExchange.getRequestMethod(),
                    httpExchange.getRequestURI(), contentType == null ? "N/A" : contentType));
        }
    }

    @Override
    public String toString() {
        return "response = " + response;
    }
}
