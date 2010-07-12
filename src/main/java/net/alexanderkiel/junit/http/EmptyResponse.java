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

import java.io.InputStream;

/**
 * @author Alexander Kiel
 * @version $Id$
 */
public class EmptyResponse extends BaseResponse {

	public EmptyResponse(int statusCode) {
		super(statusCode);
	}

	public String getContentType() {
		throw new UnsupportedOperationException("This response doesn't have a body.");
	}

	public boolean hasBody() {
		return false;
	}

	public long getBodyLength() {
		throw new UnsupportedOperationException("This response doesn't have a body.");
	}

	public InputStream getBodyInputStream() {
		throw new UnsupportedOperationException("This response doesn't have a body.");
	}

	@Override
	public String toString() {
		return "EmptyResponse[" + super.toString() + "]";
	}
}
