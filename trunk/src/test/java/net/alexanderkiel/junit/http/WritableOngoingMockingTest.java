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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static net.alexanderkiel.junit.http.HttpMock.Method.POST;
import static net.alexanderkiel.junit.http.WritableOngoingMocking.extractContentType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;

/**
 * @author <a href="mailto:alexander.kiel@life.uni-leipzig.de">Alexander Kiel</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class WritableOngoingMockingTest {

    private WritableOngoingMocking mocking;

    @Mock
    private HttpExchange httpExchange;

    @Before
    public void setUp() {
        mocking = new WritableOngoingMocking(POST, "/foo", "text/plain", "foo");
    }

    @Test
    public void testExtractContentTypeWithoutHeader() {
        given(httpExchange.getRequestHeaders()).willReturn(new Headers());

        String contentType = extractContentType(httpExchange);

        assertNull("content type is null", contentType);
    }

    @Test
    public void testExtractContentTypeWithEmptyHeader() throws Exception {
        Headers headers = new Headers();
        headers.add("Content-Type", "");
        given(httpExchange.getRequestHeaders()).willReturn(headers);

        String contentType = extractContentType(httpExchange);

        assertEquals("content type", "", contentType);
    }

    @Test
    public void testExtractContentTypeWithoutParameters() throws Exception {
        Headers headers = new Headers();
        headers.add("Content-Type", "text/plain");
        given(httpExchange.getRequestHeaders()).willReturn(headers);

        String contentType = extractContentType(httpExchange);

        assertEquals("content type", "text/plain", contentType);
    }

    @Test
    public void testExtractContentTypeWithOneParameter() throws Exception {
        Headers headers = new Headers();
        headers.add("Content-Type", "application/atom+xml;type=feed");
        given(httpExchange.getRequestHeaders()).willReturn(headers);

        String contentType = extractContentType(httpExchange);

        assertEquals("content type", "application/atom+xml", contentType);
    }

    @Test
    public void testExtractContentTypeWithTwoParameters() throws Exception {
        Headers headers = new Headers();
        headers.add("Content-Type", "application/atom+xml;type=feed;charset=utf-8");
        given(httpExchange.getRequestHeaders()).willReturn(headers);

        String contentType = extractContentType(httpExchange);

        assertEquals("content type", "application/atom+xml", contentType);
    }

    @Test
    public void testVerifyWithoutRequestCalled() throws Exception {
        try {
            mocking.verify();
            fail();
        } catch (AssertionError e) {
            assertEquals("assertion message", "request POST /foo called", e.getMessage());
        }
    }
}
