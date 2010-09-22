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

import org.jetbrains.annotations.NotNull;

import static org.apache.commons.codec.binary.Base64.encodeBase64;

/**
 * @author <a href="mailto:alexander.kiel@life.uni-leipzig.de">Alexander Kiel</a>
 * @version $Id$
 */
public final class BasicAuthToken {

    private final String username;
    private final String password;

    public BasicAuthToken(@NotNull String username, @NotNull String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAuthHeaderValue() {
        return "Basic " + new String(encodeBase64((username + ":" + password).getBytes(), false));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        BasicAuthToken other = (BasicAuthToken) obj;

        return username.equals(other.username) && password.equals(other.password);
    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BasicAuthToken[username = '" + username + "', password = '" + password + "']";
    }
}
