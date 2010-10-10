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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Kiel
 */
class DefaultHandler implements HttpHandler {

    private static final int NOT_FOUND = 404;

    private final URI baseUri;
    private final Map<Key, HttpHandler> handlerMap;

    DefaultHandler(URI baseUri) {
        this.baseUri = baseUri;
        handlerMap = new HashMap<Key, HttpHandler>();
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        Key key = buildKey(httpExchange);
        if (handlerMap.containsKey(key)) {
            handlerMap.get(key).handle(httpExchange);
        } else {
            httpExchange.sendResponseHeaders(NOT_FOUND, -1);
            httpExchange.close();
        }
    }

    private Key buildKey(HttpExchange httpExchange) {
        HttpMock.Method method = HttpMock.Method.valueOf(httpExchange.getRequestMethod());
        String path = baseUri.relativize(httpExchange.getRequestURI()).getPath();
        return new Key(method, path);
    }

    void registerSubHandler(HttpMock.Method method, String path, HttpHandler handler) {
        Key key = new Key(method, path);
        handlerMap.put(key, handler);
    }

    @Override
    public String toString() {
        return "DefaultHandler[baseUri = " + baseUri + ", handlerMap = " + handlerMap + "]";
    }
}
