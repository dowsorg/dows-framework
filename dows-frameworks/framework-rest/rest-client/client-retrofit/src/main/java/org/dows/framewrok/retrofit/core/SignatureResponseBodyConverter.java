package org.dows.framewrok.retrofit.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import okhttp3.ResponseBody;
import org.dows.framework.crypto.api.ApiCryptor;
import org.dows.framework.crypto.api.InputMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import retrofit2.Converter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/16/2022
 */
@Data
@Builder
public class SignatureResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private ApiCryptor apiCryptor;
    private ObjectMapper objectMapper;
    private Class<T> respClass;
    private List<Annotation> annotations;

    @Override
    public T convert(ResponseBody responseBody) throws IOException {
        okhttp3.MediaType mediaType = responseBody.contentType();
        HttpHeaders httpHeaders = HttpHeaders.writableHttpHeaders(HttpHeaders.EMPTY);
        httpHeaders.setContentType(MediaType.parseMediaType(mediaType.toString()));
        InputMessage inputMessage = new InputMessage();
        inputMessage.setBody(responseBody.byteStream());
        inputMessage.setHeaders(httpHeaders);
        InputStream body = apiCryptor.requestBefore(inputMessage, annotations, null, null).getBody();
        return objectMapper.readValue(body, respClass);
    }

}
