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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author Alexander Kiel
 */
@RunWith(MockitoJUnitRunner.class)
public class CatchAllHandlerTest {

    private CatchAllHandler handler;

    @Mock
    private HttpExchange httpExchange;

    @Before
    public void setUp() throws Exception {
        handler = new CatchAllHandler();
    }

    @Test
    public void testHandleGetRequest() throws Exception {
        given(httpExchange.getRequestMethod()).willReturn("GET");

        handler.handle(httpExchange);

        verify(httpExchange).sendResponseHeaders(404, -1);
        verify(httpExchange).close();
    }

    @Test
    public void testHandlePostRequest() throws Exception {
        given(httpExchange.getRequestMethod()).willReturn("POST");

        handler.handle(httpExchange);
    }
}
