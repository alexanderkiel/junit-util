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

import org.junit.Test;

import static net.alexanderkiel.junit.Assert.assertBasicEqualsAndHashCodeBehavior;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:alexander.kiel@life.uni-leipzig.de">Alexander Kiel</a>
 * @version $Id$
 */
public class BasicAuthTokenTest {

    @Test
    public void testConstructor() throws Exception {
        BasicAuthToken authToken = new BasicAuthToken("foo", "bar");

        assertEquals("username", "foo", authToken.getUsername());
        assertEquals("password", "bar", authToken.getPassword());
    }

    @Test
    public void testAuthHeaderValue() throws Exception {
        BasicAuthToken authToken = new BasicAuthToken("foo", "bar");

        assertEquals("auth header value", "Basic Zm9vOmJhcg==", authToken.getAuthHeaderValue());
    }

    @Test
    public void testEqualsOnUsername() throws Exception {
        BasicAuthToken authToken1 = new BasicAuthToken("foo", "bar");
        BasicAuthToken authToken2 = new BasicAuthToken("foo", "bar");
        BasicAuthToken authToken3 = new BasicAuthToken("foo1", "bar");

        assertBasicEqualsAndHashCodeBehavior(authToken1, authToken2, authToken3);
    }

    @Test
    public void testEqualsOnPassword() throws Exception {
        BasicAuthToken authToken1 = new BasicAuthToken("foo", "bar");
        BasicAuthToken authToken2 = new BasicAuthToken("foo", "bar");
        BasicAuthToken authToken3 = new BasicAuthToken("foo", "bar1");

        assertBasicEqualsAndHashCodeBehavior(authToken1, authToken2, authToken3);
    }
}
