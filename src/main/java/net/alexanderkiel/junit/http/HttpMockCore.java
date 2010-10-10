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

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static java.util.Collections.singletonList;
import static java.util.logging.Level.FINE;

/**
 * @author Alexander Kiel
 */
class HttpMockCore {

    private static final int THREAD_POOL_SIZE = 10;
    private static final Logger LOGGER = Logger.getLogger(HttpMockCore.class.getName());

    private final HttpServer httpServer;
    private final String contextPath;

    private DefaultHandler defaultHandler;
    private CommonHeaderFilter commonHeaderFilter;
    private final Map<Key, OngoingMocking> mockings;

    HttpMockCore(@NotNull HttpServer httpServer, @NotNull String contextPath) {
        this.httpServer = httpServer;
        this.contextPath = contextPath;
        mockings = new HashMap<Key, OngoingMocking>();
    }

    void init() {
        httpServer.setExecutor(Executors.newFixedThreadPool(THREAD_POOL_SIZE));

        defaultHandler = new DefaultHandler(URI.create(contextPath));
        commonHeaderFilter = new CommonHeaderFilter();
        httpServer.createContext(contextPath, defaultHandler).getFilters().add(commonHeaderFilter);
        httpServer.createContext("/", new CatchAllHandler()).getFilters().add(commonHeaderFilter);
    }

    void start() {
        if (LOGGER.isLoggable(FINE)) {
            LOGGER.fine("Start HTTP Server on: " + httpServer.getAddress() + contextPath);
        }
        httpServer.start();
    }

    void stop() {
        if (LOGGER.isLoggable(FINE)) {
            LOGGER.fine("Stop HTTP Server.");
        }
        httpServer.stop(0);
    }

    void setCommonHeader(@NotNull String name, @NotNull String value) {
        commonHeaderFilter.setHeader(name, value);
    }

    OngoingMocking given(@NotNull HttpMock.Method method, @NotNull String path) {
        ReadonlyOngoingMocking mocking = new ReadonlyOngoingMocking(method, path);
        defaultHandler.registerSubHandler(method, path, mocking);
        mockings.put(new Key(method, path), mocking);
        return mocking;
    }

    OngoingMocking given(@NotNull HttpMock.Method method, @NotNull String path, @NotNull String payload) {
        WritableOngoingMocking mocking = new WritableOngoingMocking(payload);
        defaultHandler.registerSubHandler(method, path, mocking);
        mockings.put(new Key(method, path), mocking);
        return mocking;
    }

    /**
     * Verifies all requests.
     *
     * @throws AssertionError if one of the request were invalid.
     */
    void verify() {
        for (OngoingMocking mocking : mockings.values()) {
            mocking.verify();
        }
    }

    @Override
    public String toString() {
        return "HttpMockCore[httpServer.address = " + httpServer.getAddress() + ", contextPath = '" + contextPath + "']";
    }

    private static class CommonHeaderFilter extends Filter {

        private final Map<String, List<String>> commonHeaders;

        private CommonHeaderFilter() {
            commonHeaders = new Headers();
        }

        void setHeader(String name, String value) {
            commonHeaders.put(name, singletonList(value));
        }

        @Override
        public void doFilter(HttpExchange httpExchange, Chain chain) throws IOException {
            httpExchange.getResponseHeaders().putAll(commonHeaders);
            chain.doFilter(httpExchange);
        }

        @Override
        public String description() {
            return "Sets common response headers.";
        }

        @Override
        public String toString() {
            return "CommonHeaderFilter[commonHeaders = " + commonHeaders + "]";
        }
    }
}
