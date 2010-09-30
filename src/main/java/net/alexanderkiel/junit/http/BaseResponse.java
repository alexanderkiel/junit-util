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

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

/**
 * @author Alexander Kiel
 * @version $Id$
 */
abstract class BaseResponse implements Response {

    static final Charset UTF_8 = Charset.forName("UTF-8");

    private final int statusCode;
    private final Map<String, String> headers;

    BaseResponse(int statusCode) {
        this.statusCode = statusCode;
        headers = Collections.emptyMap();
    }

    BaseResponse(int statusCode, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.headers = unmodifiableMap(new HashMap<String, String>(headers));
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "statusCode = " + statusCode;
    }
}
