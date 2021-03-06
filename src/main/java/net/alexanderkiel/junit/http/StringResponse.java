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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Alexander Kiel
 */
public class StringResponse extends BaseResponse {

    private final String contentType;
    private final String body;

    public StringResponse(int statusCode, String contentType, String body) {
        super(statusCode);
        if (contentType == null) {
            throw new NullPointerException("Can't resolve the argument 'contentType'.");
        }
        if (body == null) {
            throw new NullPointerException("Can't resolve the argument 'body'.");
        }
        this.contentType = contentType;
        this.body = body;
    }

    public String getContentType() {
        return contentType;
    }

    public boolean hasBody() {
        return true;
    }

    public long getBodyLength() {
        return body.length();
    }

    public InputStream getBodyInputStream() {
        return new ByteArrayInputStream(body.getBytes(UTF_8));
    }

    @Override
    public String toString() {
        return "StringResponse[" + super.toString() + ", contentType = '" + contentType + "', body = '" + body + "']";
    }
}
