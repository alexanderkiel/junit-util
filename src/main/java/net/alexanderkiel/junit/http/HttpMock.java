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

import java.io.IOException;

/**
 * @author Alexander Kiel
 * @version $Id$
 */
public class HttpMock {

	private static final HttpMockCore HTTP_MOCK_CORE = createCore();

	private static HttpMockCore createCore() {
		try {
			return new HttpMockCoreFactory("localhost", 8181).create();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public enum Method {
		GET, PUT, POST, DELETE, OPTIONS
	}

	private HttpMock() {
	}

	public static void start() {
		HTTP_MOCK_CORE.init();
		HTTP_MOCK_CORE.setCommonHeader("Access-Control-Allow-Origin", "*");
		HTTP_MOCK_CORE.setCommonHeader("Access-Control-Allow-Methods", "GET, HEAD, PUT, DELETE, POST, OPTIONS");
		HTTP_MOCK_CORE.setCommonHeader("Access-Control-Allow-Headers", "Content-Type, Accept, Accept-Charset");
		HTTP_MOCK_CORE.setCommonHeader("Access-Control-Max-Age", "1728000");
		HTTP_MOCK_CORE.start("");
	}

	public static void stop() {
		HTTP_MOCK_CORE.stop();
	}

	public static OngoingMocking given(Method method, String path) {
		return HTTP_MOCK_CORE.given(method, path);
	}

	public static OngoingMocking given(Method method, String path, String payload) {
		return HTTP_MOCK_CORE.given(method, path, payload);
	}
}
