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
import java.net.URL;

/**
 * @author Alexander Kiel
 * @version $Id$
 */
public class ClasspathResourceResponse extends BaseResponse {

	private final String contentType;
	private final URL classpathResponse;

	public ClasspathResourceResponse(int statusCode, String contentType, URL classpathResponse) {
		super(statusCode);
		this.contentType = contentType;
		this.classpathResponse = classpathResponse;
	}

	public String getContentType() {
		return contentType;
	}

	public boolean hasBody() {
		return true;
	}

	public long getBodyLength() {
		return 0;
	}

	public InputStream getBodyInputStream() throws IOException {
		return classpathResponse.openStream();
	}

	@Override
	public String toString() {
		return "ClasspathResourceResponse" +
				"[" + super.toString() +
				", contentType = '" + contentType + "'" +
				", classpathResponse = " + classpathResponse +
				"]";
	}
}
