package org.dows.framewrok.retrofit.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.dows.crypto.api.ApiCryptor;
import org.dows.crypto.api.annotation.NotDecrypt;
import org.dows.crypto.api.annotation.SignatureCrypto;
import org.dows.crypto.api.annotation.SymmetricCrypto;
import org.dows.framework.rest.factory.ConverterFactory;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/16/2022
 */
public final class CryptoConverterFactory extends Converter.Factory implements ConverterFactory {
    private final List<ApiCryptor> apiCryptors;
    private final ObjectMapper objectMapper;

    private CryptoConverterFactory(List<ApiCryptor> apiCryptors, ObjectMapper objectMapper) {
        this.apiCryptors = apiCryptors;
        this.objectMapper = objectMapper;
    }

    public static CryptoConverterFactory create(List<ApiCryptor> apiCryptors, ObjectMapper objectMapper) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                    @Override
                    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
                        return new JsonPrimitive(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    }
                })
                .serializeNulls()
                .create();

        return new CryptoConverterFactory(apiCryptors, objectMapper);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        Class<?> respClass = null;
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            if (!(rawType instanceof Class)) {
                throw new IllegalArgumentException();
            }
            respClass = (Class<?>) rawType;
        }
        List<Annotation> annotationList = Arrays.asList(annotations);
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(NotDecrypt.class)) {
                return null;
            } else if (annotation.annotationType().equals(SignatureCrypto.class)) {
                ApiCryptor apiCryptor = apiCryptors.stream().filter(ac -> ac.isCanRealize(SignatureCrypto.class))
                        .findFirst().get();
                return SignatureResponseBodyConverter.builder()
                        .apiCryptor(apiCryptor)
                        .annotations(annotationList)
                        .objectMapper(objectMapper)
                        .respClass((Class<Object>) respClass)
                        .build();
            } else if (annotation.annotationType().equals(SymmetricCrypto.class)) {
                ApiCryptor apiCryptor = apiCryptors.stream().filter(ac -> ac.isCanRealize(SymmetricCrypto.class))
                        .findFirst().get();
                return SymmetricResponseBodyConverter.builder()
                        .apiCryptor(apiCryptor)
                        .annotations(annotationList)
                        .objectMapper(objectMapper)
                        .build();
            }
        }
        return null;
    }

    /**
     * parseParameterAnnotation方法里，根据不同的参数注解，调用 Retrofit 的 requestBodyConverter和stringConverter方法。
     * 其中只有@Body和@Part、@PartMap使用的requestBodyConverter。
     *
     * @param type
     * @param parameterAnnotations
     * @param methodAnnotations
     * @param retrofit
     * @return
     */
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations, Retrofit retrofit) {
        for (Annotation annotation : methodAnnotations) {
            if (annotation.annotationType().equals(NotDecrypt.class)) {
                return null;
            } else if (annotation.annotationType().equals(SignatureCrypto.class)) {
                ApiCryptor apiCryptor = apiCryptors.stream().filter(ac -> ac.isCanRealize(SignatureCrypto.class))
                        .findFirst().get();
                return SignatureRequestBodyConverter.builder()
                        .apiCryptor(apiCryptor)
                        .annotations(Arrays.asList(methodAnnotations))
                        .objectMapper(objectMapper)
                        .build();
            } else if (annotation.annotationType().equals(SymmetricCrypto.class)) {
                ApiCryptor apiCryptor = apiCryptors.stream().filter(ac -> ac.isCanRealize(SymmetricCrypto.class))
                        .findFirst().get();
                return SymmetricRequestBodyConverter.builder()
                        .apiCryptor(apiCryptor)
                        .annotations(Arrays.asList(methodAnnotations))
                        .objectMapper(objectMapper)
                        .build();
            }
        }
        return null;
    }

    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return super.stringConverter(type, annotations, retrofit);
    }


}
