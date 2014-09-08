/*
 * Copyright 2012 Smartling, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smartling.api.sdk.dto.file;

/**
 * Internal class used in storing the string content of the response along with the encoding.
 */
public class StringResponse
{
    private final String contents;
    private final byte[] contentsRaw;
    private final String encoding;

    public StringResponse(final String contents, final byte[] contentsRaw, final String encoding)
    {
        this.contents = contents;
        this.contentsRaw = contentsRaw;
        this.encoding = encoding;
    }

    public String getContents()
    {
        return contents;
    }

    public byte[] getContentsRaw()
    {
        return contentsRaw;
    }

    public String getEncoding()
    {
        return encoding;
    }
}
