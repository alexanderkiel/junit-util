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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="alexander.kiel@life.uni-leipzig.de">Alexander Kiel</a>
 * @version $Id$
 */
abstract class BaseOngoingMocking implements OngoingMocking {

	protected final HttpServer httpServer;
	protected final Map<String, List<String>> commonHeaders;
	protected final String path;
	protected final HttpMock.Method method;

	BaseOngoingMocking(@NotNull String path, @NotNull Map<String, List<String>> commonHeaders,
	                   @NotNull HttpMock.Method method, @NotNull HttpServer httpServer) {
		this.path = path;
		this.commonHeaders = new HashMap<String, List<String>>(commonHeaders);
		this.method = method;
		this.httpServer = httpServer;
	}

	abstract class BaseHttpHandler implements HttpHandler {

		static final int BUFFER_SIZE = 1024;

		final Response response;
		final byte[] buffer;

		BaseHttpHandler(@NotNull Response response) {
			this.response = response;
			buffer = new byte[BUFFER_SIZE];
		}

		public final void handle(HttpExchange httpExchange) throws IOException {
			setCommonHeaders(httpExchange.getResponseHeaders());

			if (!doesRequestMethodMatch(httpExchange)) {
				httpExchange.sendResponseHeaders(405, -1);
			} else if (!doesPathMatch(httpExchange)) {
				httpExchange.sendResponseHeaders(404, -1);
			} else {
				handleRequest(httpExchange);
			}
			httpExchange.close();
		}

		protected boolean doesRequestMethodMatch(HttpExchange httpExchange) {
			return httpExchange.getRequestMethod().equals(method.toString());
		}

		private boolean doesPathMatch(HttpExchange httpExchange) {
			return httpExchange.getRequestURI().toString().endsWith(path);
		}

		abstract void handleRequest(HttpExchange httpExchange) throws IOException;

		protected void setCommonHeaders(Headers responseHeaders) {
			responseHeaders.putAll(commonHeaders);
		}

		protected void setResponseHeaders(Headers headers) {
			if (response.getBodyLength() >= 0) {
				headers.set("Content-Type", response.getContentType());
			}
		}

		protected void sendResponseHeaders(HttpExchange httpExchange) throws IOException {
			httpExchange.sendResponseHeaders(response.getStatusCode(), response.getBodyLength());
		}

		protected void sendResponseBody(OutputStream responseBodyOutputStream) throws IOException {
			if (response.getBodyLength() >= 0) {
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
	}
}
