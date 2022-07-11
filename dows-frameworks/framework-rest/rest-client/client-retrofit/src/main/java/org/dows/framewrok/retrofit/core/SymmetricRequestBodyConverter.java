package org.dows.framewrok.retrofit.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.dows.framework.crypto.api.ApiCryptor;
import retrofit2.Converter;

import java.io.IOException;
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
public class SymmetricRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private ObjectMapper objectMapper;
    private ApiCryptor apiCryptor;
    private List<Annotation> annotations;


    @Override
    public RequestBody convert(T o) throws IOException {
        Object o1 = apiCryptor.responseBefore(o, annotations, null, null);
        RequestBody requestBody = RequestBody.create(objectMapper.writeValueAsBytes(o1),
                MediaType.parse("application/json;charset=UTF-8"));
        return requestBody;
    }
}
