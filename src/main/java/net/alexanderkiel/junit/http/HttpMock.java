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
 */
public class HttpMock {

    private static HttpMockCore httpMockCore;

    public enum Method {
        HEAD, GET, POST, PUT, DELETE, TRACE, OPTIONS, CONNECT, PATCH
    }

    private HttpMock() {
    }

    public static void start(int port, String contextPath) throws IOException {
        start("localhost", port, contextPath);
    }

    public static void start(String host, int port, String contextPath) throws IOException {
        httpMockCore = new HttpMockCoreFactory(host, port, contextPath).create();
        httpMockCore.init();
        httpMockCore.setCommonHeader("Access-Control-Allow-Origin", "*");
        httpMockCore.setCommonHeader("Access-Control-Allow-Methods", "GET, HEAD, PUT, DELETE, POST, OPTIONS");
        httpMockCore.setCommonHeader("Access-Control-Allow-Headers", "Content-Type, Accept, Accept-Charset, " +
                "Authorization");
        httpMockCore.setCommonHeader("Access-Control-Max-Age", "1728000");
        httpMockCore.start();
    }

    public static void stop() {
        httpMockCore.stop();
    }

    public static OngoingMocking given(Method method, String path) {
        return httpMockCore.given(method, path);
    }

    public static OngoingMocking given(Method method, String path, String payloadContentType, String payload) {
        return httpMockCore.given(method, path, payloadContentType, payload);
    }

    public static void verify() {
        httpMockCore.verify();
    }
}
