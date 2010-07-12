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
 * @version $Id$
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

	private static class Key {

		private final HttpMock.Method method;
		private final String path;

		private Key(HttpMock.Method method, String path) {
			this.method = method;
			this.path = path;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof Key)) return false;

			Key key = (Key) obj;

			return method == key.method && path.equals(key.path);
		}

		@Override
		public int hashCode() {
			int result = method.hashCode();
			result = 31 * result + path.hashCode();
			return result;
		}

		@Override
		public String toString() {
			return "Key[method = " + method + ", path = '" + path + "']";
		}
	}

	@Override
	public String toString() {
		return "DefaultHandler[baseUri = " + baseUri + ", handlerMap = " + handlerMap + "]";
	}
}
