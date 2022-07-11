package org.dows.framewrok.retrofit;

import org.dows.framework.rest.RestResponse;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/5/2022
 */
public class RetrofitResponseAdapter implements RestResponse<Response> {

    private Response response;

    public RetrofitResponseAdapter(Response response) {
        this.response = response;
    }

    @Override
    public int getStatusCode() throws IOException {
        return response.code();
    }

    @Override
    public String getReasonPhrase() throws Exception {
        return response.message();
    }

    @Override
    public InputStream getContent() throws IOException {
        return response.body().byteStream();
    }

    @Override
    public Response unwrap() {
        return response;
    }
}
