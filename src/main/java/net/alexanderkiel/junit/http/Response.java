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
import java.io.InputStream;
import java.util.Map;

/**
 * @author Alexander Kiel
 * @version $Id$
 */
public interface Response {

    int getStatusCode();

    Map<String, String> getHeaders();

    String getContentType();

    /**
     * Determines whether this response has a body of data or payload.
     *
     * @return {@code true} if this response has a body of data; {@code false} otherwise.
     */
    boolean hasBody();

    /**
     * Returns the length of the body of this response in bytes.
     * <p/>
     * If the length is unknown 0 have to be returned and otherwise the InputStream returned by {@link
     * #getBodyInputStream()} have to return the exact amount of data, which is specified by this method.
     *
     * @return the length of the body of this response in bytes.
     * @throws UnsupportedOperationException if this response doesn't have a body.
     */
    long getBodyLength();

    InputStream getBodyInputStream() throws IOException;
}
