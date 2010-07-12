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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;
import java.net.URISyntaxException;

import static net.alexanderkiel.junit.http.HttpMock.Method.GET;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author Alexander Kiel
 * @version $Id$
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultHandlerTest {

	private static final URI BASE_URI = URI.create("http://host.org:8080/foo");

	private DefaultHandler handler;

	@Mock
	private HttpHandler subHandler;

	@Mock
	private HttpExchange httpExchange;

	@Before
	public void setUp() throws Exception {
		handler = new DefaultHandler(BASE_URI);
	}

	@Test
	public void testHandleMatchingMethodAndPath() throws Exception {
		handler.registerSubHandler(GET, "bar", subHandler);
		givenRequestMethod("GET");
		givenRequestPath("/bar");

		handler.handle(httpExchange);

		verify(subHandler).handle(httpExchange);
	}

	@Test
	public void testHandleMatchingMethod() throws Exception {
		handler.registerSubHandler(GET, "bar", subHandler);
		givenRequestMethod("GET");
		givenRequestPath("/foo");

		handler.handle(httpExchange);

		verify(httpExchange).sendResponseHeaders(404, -1);
		verify(httpExchange).close();
	}

	@Test
	public void testHandleMatchingPath() throws Exception {
		handler.registerSubHandler(GET, "bar", subHandler);
		givenRequestMethod("POST");
		givenRequestPath("/bar");

		handler.handle(httpExchange);

		verify(httpExchange).sendResponseHeaders(404, -1);
		verify(httpExchange).close();
	}

	@Test
	public void testHandleNothingMatches() throws Exception {
		handler.registerSubHandler(GET, "bar", subHandler);
		givenRequestMethod("POST");
		givenRequestPath("/foo");

		handler.handle(httpExchange);

		verify(httpExchange).sendResponseHeaders(404, -1);
		verify(httpExchange).close();
	}

	private void givenRequestMethod(String method) {
		given(httpExchange.getRequestMethod()).willReturn(method);
	}

	private void givenRequestPath(String path) throws URISyntaxException {
		given(httpExchange.getRequestURI()).willReturn(new URI(BASE_URI.toString() + path));
	}
}
