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
package com.smartling.api.sdk;

import static junit.framework.Assert.assertEquals;

import com.smartling.api.sdk.dto.ApiResponse;
import com.smartling.api.sdk.dto.Data;

import java.io.File;

import com.smartling.api.sdk.file.FileType;
import junit.framework.Assert;
import org.apache.commons.io.FilenameUtils;

public class ApiTestHelper
{
    private static final String   SUCCESS            = "SUCCESS";
    private static final FileType TEST_FILE_TYPE     = FileType.JAVA_PROPERTIES;
    private static final String   TEST_FILE_LOCATION = "resources/test.properties";

    public static String getApiKey()
    {
        String apiKey = System.getProperty("apiKey");
        Assert.assertNotNull("The apiKey system property must be set", apiKey);

        return apiKey;
    }

    public static String getProjectId()
    {
        String projectId = System.getProperty("projectId");
        Assert.assertNotNull("The projectId system property must be set", projectId);

        return projectId;
    }

    public static String getLocale()
    {
        String locale = System.getProperty("locale");
        Assert.assertNotNull("The locale system property must be set", locale);

        return locale;
    }

    public static String getCallbackUrl()
    {
        return System.getProperty("callbackUrl");
    }

    public static File getTestFile()
    {
        return new File(FilenameUtils.separatorsToSystem(ApiTestHelper.TEST_FILE_LOCATION));
    }

    public static FileType getTestFileType()
    {
        return TEST_FILE_TYPE;
    }

    public static boolean getTestMode()
    {
        return System.getProperty("testMode") == null ? false : Boolean.valueOf(System.getProperty("testMode"));
    }

    public static void verifyApiResponse(ApiResponse<? extends Data> apiResponse)
    {
        assertEquals(SUCCESS, apiResponse.getCode());
        assertEquals(0, apiResponse.getMessages().size());
    }
}
