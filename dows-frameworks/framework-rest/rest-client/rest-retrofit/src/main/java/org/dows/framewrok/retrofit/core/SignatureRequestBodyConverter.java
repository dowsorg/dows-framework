package org.dows.framewrok.retrofit.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.dows.crypto.api.ApiCryptor;
import retrofit2.Converter;

import java.io.ByteArrayOutputStream;
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
public class SignatureRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private ObjectMapper objectMapper;
    private ApiCryptor apiCryptor;
    private List<Annotation> annotations;

    public static final byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    @Override
    public RequestBody convert(T o) throws IOException {
        Object o1 = apiCryptor.responseBefore(o, annotations, null, null);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), objectMapper.writeValueAsBytes(o1)
        );
        return requestBody;
    }
}
