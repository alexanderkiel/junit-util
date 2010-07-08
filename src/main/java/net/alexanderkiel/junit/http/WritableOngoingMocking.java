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
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

/**
 * @author Alexander Kiel
 * @version $Id$
 */
class WritableOngoingMocking extends BaseOngoingMocking {

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private final String payload;

	WritableOngoingMocking(@NotNull HttpServer httpServer, @NotNull Map<String, List<String>> commonHeaders,
	                       @NotNull HttpMock.Method method, @NotNull String path, @NotNull String payload) {
		super(path, commonHeaders, method, httpServer);
		this.payload = payload;
	}

	public void willRespond(@NotNull Response response) {
		httpServer.createContext(path, new MyHttpHandler(response));
	}

	private class MyHttpHandler extends BaseHttpHandler {

		private final char[] charBuffer;

		private MyHttpHandler(@NotNull Response response) {
			super(response);
			charBuffer = new char[BUFFER_SIZE];
		}

		@Override
		void handleRequest(HttpExchange httpExchange) throws IOException {
			verifyPayload(httpExchange.getRequestBody());
			setResponseHeaders(httpExchange.getResponseHeaders());
			sendResponseHeaders(httpExchange);
			sendResponseBody(httpExchange.getResponseBody());
		}

		private void verifyPayload(InputStream requestBodyInputStream) throws IOException {
			assertEquals(format("request %s payload", path), payload, readBody(requestBodyInputStream));
		}

		private String readBody(InputStream requestBodyInputStream) throws IOException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(requestBodyInputStream, UTF_8));
			try {
				StringBuilder sb = new StringBuilder();
				int length = 0;
				while (length >= 0) {
					length = reader.read(charBuffer);
					sb.append(charBuffer, 0, length);
				}
				return sb.toString();
			} finally {
				reader.close();
			}
		}
	}
}
