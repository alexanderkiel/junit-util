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
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * @author Alexander Kiel
 */
class DefaultHandler implements HttpHandler {

    private static final Logger LOGGER = Logger.getLogger(DefaultHandler.class.getName());
    private static final int NOT_FOUND = 404;
    private static final int METHOD_NOT_ALLOWED = 405;

    private final URI baseUri;
    private final Map<String, Map<HttpMock.Method, HttpHandler>> handlerMap;

    DefaultHandler(URI baseUri) {
        this.baseUri = baseUri;
        handlerMap = new HashMap<String, Map<HttpMock.Method, HttpHandler>>();
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        String path = baseUri.relativize(httpExchange.getRequestURI()).getPath();

        if (handlerMap.containsKey(path)) {
            Map<HttpMock.Method, HttpHandler> map = handlerMap.get(path);
            HttpMock.Method method = HttpMock.Method.valueOf(httpExchange.getRequestMethod());
            if (map.containsKey(method)) {
                map.get(method).handle(httpExchange);
            } else {
                LOGGER.severe(format("Method %s on resource '%s' not allowed.", method, path));
                httpExchange.sendResponseHeaders(METHOD_NOT_ALLOWED, -1);
                httpExchange.close();
            }
        } else {
            LOGGER.severe(format("Resource '%s' not found.", path));
            httpExchange.sendResponseHeaders(NOT_FOUND, -1);
            httpExchange.close();
        }
    }

    void registerSubHandler(HttpMock.Method method, String path, HttpHandler handler) {
        if (handlerMap.containsKey(path)) {
            handlerMap.get(path).put(method, handler);
        } else {
            HashMap<HttpMock.Method, HttpHandler> map = new HashMap<HttpMock.Method, HttpHandler>();
            map.put(method, handler);
            handlerMap.put(path, map);
        }
    }

    @Override
    public String toString() {
        return "DefaultHandler[baseUri = " + baseUri + ", handlerMap = " + handlerMap + "]";
    }
}
