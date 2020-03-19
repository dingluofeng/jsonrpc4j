package com.googlecode.jsonrpc4j.okhttp;

import static com.googlecode.jsonrpc4j.JsonRpcBasicServer.JSONRPC_CONTENT_TYPE;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.googlecode.jsonrpc4j.IJsonRpcClient;
import com.googlecode.jsonrpc4j.JsonRpcClient;
import com.googlecode.jsonrpc4j.okhttp.exceprions.OkHttpException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JsonRpcOkhttpClient extends JsonRpcClient implements IJsonRpcClient {

	private final AtomicReference<URL> serviceUrl = new AtomicReference<>();

    private final OkHttpClient okHttpClient;

	private final Map<String, String> headers = new HashMap<>();

    private String contentType = JSONRPC_CONTENT_TYPE;

    public JsonRpcOkhttpClient(URL serviceUrl, ObjectMapper mapper, OkHttpClient okHttpClient,
        Map<String, String> headers) {
		super(mapper);
        this.okHttpClient = okHttpClient;
		this.serviceUrl.set(serviceUrl);
		if (headers != null) {
			this.headers.putAll(headers);
		}
	}

	/**
	 * @return the headers
	 */
	public Map<String, String> getHeaders() {
		return Collections.unmodifiableMap(headers);
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(Map<String, String> headers) {
		this.headers.clear();
		this.headers.putAll(headers);
	}

	public URL getServiceUrl() {
		return serviceUrl.get();
	}

	public void setServiceUrl(URL serviceUrl) {
		this.serviceUrl.set(serviceUrl);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void invoke(String methodName, Object argument) throws Throwable {
		invoke(methodName, argument, null, new HashMap<String, String>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object invoke(String methodName, Object argument, Type returnType) throws Throwable {
		return invoke(methodName, argument, returnType, new HashMap<String, String>());
	}

    @Override
    public Object invoke(String methodName, Object argument, Type returnType, Map<String, String> extraHeaders)
        throws Throwable {
        Request.Builder requestBuilder = new Request.Builder().url(getServiceUrl());
        // add extraHeaders
        addHeaders(requestBuilder, extraHeaders);

        final ObjectNode request = super.createRequest(methodName, argument);
        try {
            MediaType mediaType = MediaType.parse(contentType);
            ObjectMapper objectMapper = this.getObjectMapper();
            byte[] writeValueAsBytes = objectMapper.writeValueAsBytes(request);
            RequestBody requestBody = RequestBody.create(mediaType, writeValueAsBytes);

            try {
                requestBuilder.post(requestBody);
                Response response = okHttpClient.newCall(requestBuilder.build()).execute();
                // read and return value
                try (InputStream answer = response.body().byteStream()) {
                    return super.readResponse(returnType, answer);
                }
            } catch (JsonMappingException e) {
                // JsonMappingException inherits from IOException
                throw e;
            } catch (IOException e) {
                throw new OkHttpException("Caught error with no response body.", e);
            }
        } finally {
        }
    }

    private final void addHeaders(Request.Builder request, Map<String, String> extraHeaders) {
        Map<String, String> headers = getHeaders();
        for (Entry<String, String> entry : headers.entrySet()) {
            request.addHeader(entry.getKey(), entry.getValue());
        }

        for (Entry<String, String> entry : extraHeaders.entrySet()) {
            request.addHeader(entry.getKey(), entry.getValue());
        }
    }

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T invoke(String methodName, Object argument, Class<T> clazz) throws Throwable {
		return (T) invoke(methodName, argument, Type.class.cast(clazz));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T invoke(String methodName, Object argument, Class<T> clazz, Map<String, String> extraHeaders) throws Throwable {
		return (T) invoke(methodName, argument, Type.class.cast(clazz), extraHeaders);
	}

}
