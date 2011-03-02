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

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author Alexander Kiel
 */
class HttpMockCoreFactory {

    private final String hostname;
    private final int port;
    private final String contextPath;

    HttpMockCoreFactory(String hostname, int port, String contextPath) {
        this.hostname = hostname;
        this.port = port;
        this.contextPath = contextPath;
    }

    HttpMockCore create() throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(hostname, port), 0);
        return new HttpMockCore(httpServer, contextPath);
    }

    @Override
    public String toString() {
        return "HttpMockCoreFactory[hostname = '" + hostname + "', port = " + port + "]";
    }
}
