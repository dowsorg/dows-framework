package org.dows.framewrok.retrofit;

import org.dows.framework.rest.RestRequest;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Request;
import okio.Buffer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/5/2022
 */
public class RetrofitRequestAdapter implements RestRequest<Request> {

    private Request request;
    //    @Getter
//    private Crypto crypto;
    @Setter
    @Getter
    private String cryptoContentPayload;

    public RetrofitRequestAdapter(Request request) {
        this.request = request;
    }

//    public RetrofitRequestAdapter(Request request, Crypto crypto) {
//        this.request = request;
//        this.crypto = crypto;
//    }

    @Override
    public Map<String, String> getAllHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        for (String key : request.headers().names()) {
            headers.put(key, request.header(key));
        }
        return headers;
    }

    @Override
    public String getContentType() {
        if (request.body() != null) {
            return (request.body().contentType() != null) ? request.body().contentType().toString() : null;
        }
        return null;
    }

    @Override
    public String getHeader(String key) {
        return request.header(key);
    }

    @Override
    public InputStream getMessagePayload() throws IOException {
        if (request.body() == null) {
            return null;
        }
        Buffer buf = new Buffer();
        request.body().writeTo(buf);
        return buf.inputStream();
    }

    @Override
    public Map<String, String> getRequestParams(Request request) {
        return RequestParamUtil.parseParams(request);
    }

    @Override
    public String getMethod() {
        return request.method();
    }

    @Override
    public String getRequestUrl() {
        return request.url().toString();
    }

    @Override
    public void setRequestUrl(String url) {
        request = request.newBuilder().url(url).build();
    }

    @Override
    public void setHeader(String key, String value) {
        request = request.newBuilder().header(key, value).build();
    }

    @Override
    public Request unwrap() {
        return request;
    }
}
