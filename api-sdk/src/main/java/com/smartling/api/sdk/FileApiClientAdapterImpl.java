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

import static com.smartling.api.sdk.file.FileApiParams.CONDITIONS;
import static com.smartling.api.sdk.file.FileApiParams.FILE_TYPES;
import static com.smartling.api.sdk.file.FileApiParams.FILE_URI;
import static com.smartling.api.sdk.file.FileApiParams.LAST_MODIFIED_AFTER;
import static com.smartling.api.sdk.file.FileApiParams.LIMIT;
import static com.smartling.api.sdk.file.FileApiParams.LOCALE;
import static com.smartling.api.sdk.file.FileApiParams.OFFSET;
import static com.smartling.api.sdk.file.FileApiParams.ORDERBY;
import static com.smartling.api.sdk.file.FileApiParams.LAST_UPLOADED_AFTER;
import static com.smartling.api.sdk.file.FileApiParams.LAST_UPLOADED_BEFORE;
import static com.smartling.api.sdk.file.FileApiParams.URI_MASK;
import static com.smartling.api.sdk.file.FileApiParams.NEW_FILE_URI;

import com.google.gson.reflect.TypeToken;
import com.smartling.api.sdk.dto.ApiResponse;
import com.smartling.api.sdk.dto.ApiResponseWrapper;
import com.smartling.api.sdk.dto.EmptyResponse;
import com.smartling.api.sdk.dto.file.FileLastModified;
import com.smartling.api.sdk.dto.file.FileList;
import com.smartling.api.sdk.dto.file.FileStatus;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.dto.file.UploadFileData;
import com.smartling.api.sdk.exceptions.ApiException;
import com.smartling.api.sdk.util.DateFormatter;
import com.smartling.api.sdk.file.FileApiParams;
import com.smartling.api.sdk.file.FileListSearchParams;
import com.smartling.api.sdk.file.FileType;
import com.smartling.api.sdk.file.RetrievalType;
import com.smartling.api.sdk.file.parameters.FileUploadParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetFileParameterBuilder;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

import com.smartling.api.sdk.util.HttpUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;

/**
 * Base implementation of the {@link FileApiClientAdapter}.
 */
public class FileApiClientAdapterImpl extends BaseApiClientAdapter implements FileApiClientAdapter
{
    private static final Log logger = LogFactory.getLog(FileApiClientAdapterImpl.class);

    private final static String UPLOAD_FILE_API_URL     = "%s/file/upload?";
    private final static String GET_FILE_API_URL        = "%s/file/get?";
    private final static String GET_FILE_LIST_API_URL   = "%s/file/list?";
    private final static String GET_FILE_STATUS_API_URL = "%s/file/status?";
    private final static String GET_FILE_LAST_MODIFIED  = "%s/file/last_modified?";
    private final static String RENAME_FILE_URL         = "%s/file/rename?";
    private final static String DELETE_FILE_URL         = "%s/file/delete?";

    public FileApiClientAdapterImpl(final String apiKey, final String projectId)
    {
        super(apiKey, projectId);
    }

    public FileApiClientAdapterImpl(final String apiKey, final String projectId, final ProxyConfiguration proxyConfiguration)
    {
        super(apiKey, projectId, proxyConfiguration);
    }

    public FileApiClientAdapterImpl(final boolean productionMode, final String apiKey, final String projectId)
    {
        super(productionMode, apiKey, projectId);
    }

    public FileApiClientAdapterImpl(final boolean productionMode, final String apiKey, final String projectId, final ProxyConfiguration proxyConfiguration)
    {
        super(productionMode, apiKey, projectId, proxyConfiguration);
    }

    public FileApiClientAdapterImpl(final String baseApiUrl, final String apiKey, final String projectId)
    {
        super(baseApiUrl, apiKey, projectId);
    }

    public FileApiClientAdapterImpl(final String baseApiUrl, final String apiKey, final String projectId, final ProxyConfiguration proxyConfiguration)
    {
        super(baseApiUrl, apiKey, projectId, proxyConfiguration);
    }

    @Override
    public StringResponse getFile(final String fileUri, final String locale, final RetrievalType retrievalType) throws ApiException
    {
        final GetFileParameterBuilder getFileParameterBuilder = new GetFileParameterBuilder()
                .fileUri(fileUri)
                .locale(locale)
                .retrievalType(retrievalType);

        return getFile(getFileParameterBuilder);
    }

    @Override
    public StringResponse getFile(final GetFileParameterBuilder getFileParameterBuilder) throws ApiException
    {
        logger.debug(String.format("Get file: fileUri = %s, projectId = %s, apiKey = %s, locale = %s",
                                   getFileParameterBuilder.getFileUri(), this.projectId, maskApiKey(this.apiKey), getFileParameterBuilder.getLocale()));

        final List<NameValuePair> paramsList = getFileParameterBuilder.getNameValueList();
        final String params = buildParamsQuery(paramsList.toArray(new NameValuePair[paramsList.size()]));

        final HttpGet getRequest = new HttpGet(buildUrl(GET_FILE_API_URL, params));

        final StringResponse stringResponse = HttpUtils.executeHttpCall(getRequest, proxyConfiguration);
        logger.debug(String.format("Get file: %s", SUCCESS_CODE));

        return stringResponse;
    }

    @Override
    public ApiResponse<FileList> getFilesList(final FileListSearchParams fileListSearchParams) throws ApiException
    {
        logger.debug(String.format("Get files list: fileUriMask = %s, projectId = %s, apiKey = %s, locale = %s",
                fileListSearchParams.getUriMask(), this.projectId, maskApiKey(this.apiKey), fileListSearchParams.getLocale()));

        final String params = buildFileListParams(fileListSearchParams);
        final HttpGet getRequest = new HttpGet(buildUrl(GET_FILE_LIST_API_URL, params));

        final StringResponse response = HttpUtils.executeHttpCall(getRequest, proxyConfiguration);
        final ApiResponse apiResponse = getApiResponse(response.getContents(), new TypeToken<ApiResponseWrapper<FileList>>() {});
        logger.debug(String.format("Get files list: %s. %s", apiResponse.getCode(), getApiResponseMessages(apiResponse)));

        return apiResponse;
    }

    @Override
    public ApiResponse<FileStatus> getFileStatus(final String fileUri, final String locale) throws ApiException
    {
        logger.debug(String.format("Get file satatus: fileUri = %s, projectId = %s, apiKey = %s, locale = %s", fileUri, this.projectId, maskApiKey(this.apiKey), locale));

        final String params = buildParamsQuery(new BasicNameValuePair(FILE_URI, fileUri), new BasicNameValuePair(LOCALE, locale));
        final HttpGet getRequest = new HttpGet(buildUrl(GET_FILE_STATUS_API_URL, params));

        final StringResponse response = HttpUtils.executeHttpCall(getRequest, proxyConfiguration);
        final ApiResponse apiResponse = getApiResponse(response.getContents(), new TypeToken<ApiResponseWrapper<FileStatus>>() {});
        logger.debug(String.format("Get file status: %s. %s", apiResponse.getCode(), getApiResponseMessages(apiResponse)));

        return apiResponse;
    }

    @Override
    public ApiResponse<UploadFileData> uploadFile(final File fileToUpload, final String charsetName, final FileUploadParameterBuilder fileUploadParameterBuilder)
            throws ApiException
    {
        logger.debug(String.format("Upload file: fileUri = %s, projectId = %s, apiKey = %s, localesToApprove = %s",
                fileUploadParameterBuilder.getFileUri(), this.projectId, maskApiKey(this.apiKey), StringUtils.join(fileUploadParameterBuilder.getLocalesToApprove(), ", ")));

        final List<NameValuePair> paramsList = fileUploadParameterBuilder.getNameValueList();
        final String params = buildParamsQuery(paramsList.toArray(new NameValuePair[paramsList.size()]));
        final HttpPost httpPostFile = createFileUploadHttpPostRequest(params, fileToUpload,
                createContentType(fileUploadParameterBuilder.getFileType(), Charset.forName(charsetName)));

        final StringResponse response = HttpUtils.executeHttpCall(httpPostFile, proxyConfiguration);
        final ApiResponse apiResponse = getApiResponse(response.getContents(), new TypeToken<ApiResponseWrapper<UploadFileData>>() {});
        logger.debug(String.format("Upload file: %s. %s", apiResponse.getCode(), getApiResponseMessages(apiResponse)));

        return apiResponse;
    }

    @Override
    public ApiResponse<EmptyResponse> deleteFile(final String fileUri) throws ApiException
    {
        logger.debug(String.format("Delete file: fileUri = %s, projectId = %s, apiKey = %s",
                fileUri, this.projectId, maskApiKey(this.apiKey)));

        final String params = buildParamsQuery(new BasicNameValuePair(FILE_URI, fileUri));
        final HttpDelete httpDeleteFileRequest = new HttpDelete(buildUrl(DELETE_FILE_URL, params));

        final StringResponse response = HttpUtils.executeHttpCall(httpDeleteFileRequest, proxyConfiguration);
        final ApiResponse apiResponse = getApiResponse(response.getContents(), new TypeToken<ApiResponseWrapper<EmptyResponse>>() {});
        logger.debug(String.format("Delete file: %s. %s", apiResponse.getCode(), getApiResponseMessages(apiResponse)));

        return apiResponse;
    }

    @Override
    public ApiResponse<EmptyResponse> renameFile(final String fileUri, final String newFileUri) throws ApiException
    {
        logger.debug(String.format("Rename file: fileUri = %s, projectId = %s, apiKey = %s",
                fileUri, this.projectId, maskApiKey(this.apiKey)));

        final String params = buildParamsQuery(new BasicNameValuePair(FILE_URI, fileUri), new BasicNameValuePair(NEW_FILE_URI, newFileUri));
        final HttpPost httpPostRequest = new HttpPost(buildUrl(RENAME_FILE_URL, params));

        final StringResponse response = HttpUtils.executeHttpCall(httpPostRequest, proxyConfiguration);
        final ApiResponse apiResponse = getApiResponse(response.getContents(), new TypeToken<ApiResponseWrapper<EmptyResponse>>() {});
        logger.debug(String.format("Rename file: %s. %s", apiResponse.getCode(), getApiResponseMessages(apiResponse)));

        return apiResponse;
    }

    @Override
    public ApiResponse<FileLastModified> getLastModified(final String fileUri, final Date lastModifiedAfter, final String locale) throws ApiException
    {
        logger.debug(String.format("Get last modified: fileUri = %s, projectId = %s, apiKey = %s, locale = %s",
                fileUri, this.projectId, maskApiKey(this.apiKey), locale));

        final String params = buildParamsQuery(
                new BasicNameValuePair(FILE_URI, fileUri),
                new BasicNameValuePair(LAST_MODIFIED_AFTER, DateFormatter.format(lastModifiedAfter)),
                new BasicNameValuePair(LOCALE, locale)
        );
        final HttpGet getRequest = new HttpGet(buildUrl(GET_FILE_LAST_MODIFIED, params));

        final StringResponse response = HttpUtils.executeHttpCall(getRequest, proxyConfiguration);
        final ApiResponse apiResponse = getApiResponse(response.getContents(), new TypeToken<ApiResponseWrapper<FileLastModified>>() {});
        logger.debug(String.format("Get last modified: %s. %s", apiResponse.getCode(), getApiResponseMessages(apiResponse)));

        return apiResponse;
    }

    private HttpPost createFileUploadHttpPostRequest(final String apiParameters, final File file, final ContentType contentType)
    {
        final MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
                .addPart(FileApiParams.FILE, new FileBody(file, contentType, file.getName()));

        final HttpPost httpPost = new HttpPost(String.format(UPLOAD_FILE_API_URL, baseApiUrl) + apiParameters);
        httpPost.setEntity(multipartEntityBuilder.build());

        return httpPost;
    }

    private String buildFileListParams(final FileListSearchParams fileListSearchParams)
    {
        final List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(LOCALE, fileListSearchParams.getLocale()));
        nameValuePairs.add(new BasicNameValuePair(URI_MASK, fileListSearchParams.getUriMask()));
        nameValuePairs.add(new BasicNameValuePair(LAST_UPLOADED_AFTER, DateFormatter.format(fileListSearchParams.getLastUploadedAfter())));
        nameValuePairs.add(new BasicNameValuePair(LAST_UPLOADED_BEFORE, DateFormatter.format(fileListSearchParams.getLastUploadedBefore())));
        nameValuePairs.add(new BasicNameValuePair(OFFSET, null == fileListSearchParams.getOffset() ? null : String.valueOf(fileListSearchParams.getOffset())));
        nameValuePairs.add(new BasicNameValuePair(LIMIT, null == fileListSearchParams.getLimit() ? null : String.valueOf(fileListSearchParams.getLimit())));
        nameValuePairs.addAll(getNameValuePairs(FILE_TYPES, fileListSearchParams.getFileTypes()));
        nameValuePairs.addAll(getNameValuePairs(CONDITIONS, fileListSearchParams.getConditions()));
        nameValuePairs.addAll(getNameValuePairs(ORDERBY, fileListSearchParams.getOrderBy()));

        return buildParamsQuery(nameValuePairs.toArray(new NameValuePair[nameValuePairs.size()]));
    }

    private ContentType createContentType(final FileType fileType, final Charset charset)
    {
        return fileType.isTextFormat()
                ? ContentType.create(fileType.getMimeType(), charset)
                : ContentType.create(fileType.getMimeType());
    }

}
