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
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.Executors;

import static net.alexanderkiel.junit.http.HttpMock.Method.GET;

/**
 * @author Alexander Kiel
 * @version $Id$
 */
class HttpMockCore {

	private static final int THREAD_POOL_SIZE = 10;

	private final HttpServer httpServer;
	private final Headers commonHeaders;

	HttpMockCore(@NotNull HttpServer httpServer) {
		this.httpServer = httpServer;
		commonHeaders = new Headers();
	}

	void init() {
		httpServer.setExecutor(Executors.newFixedThreadPool(THREAD_POOL_SIZE));
		httpServer.createContext("/", new CatchAllHandler());
	}

	void start(String context) {
		httpServer.start();
	}

	void stop() {
		httpServer.stop(0);
	}

	void setCommonHeader(String name, String value) {
		commonHeaders.set(name, value);
	}

	OngoingMocking given(@NotNull HttpMock.Method method, @NotNull String path) {
		return new ReadonlyOngoingMocking(httpServer, commonHeaders, method, path);
	}

	OngoingMocking given(@NotNull HttpMock.Method method, @NotNull String path, @NotNull String payload) {
		return new WritableOngoingMocking(httpServer, commonHeaders, method, path, payload);
	}

	@Override
	public String toString() {
		return "HttpMockCore[httpServer.address = " + httpServer.getAddress() + "]";
	}

	private class CatchAllHandler implements HttpHandler {

		public void handle(HttpExchange httpExchange) throws IOException {
			if (isGetRequest(httpExchange)) {
				setCommonHeaders(httpExchange.getResponseHeaders());
				httpExchange.sendResponseHeaders(404, -1);
				httpExchange.close();
			}
		}

		private boolean isGetRequest(HttpExchange httpExchange) {
			return GET.name().equals(httpExchange.getRequestMethod());
		}

		private void setCommonHeaders(Headers responseHeaders) {
			responseHeaders.putAll(commonHeaders);
		}
	}
}
