package org.dows.framewrok.retrofit.core;

import okhttp3.Request;
import okhttp3.ResponseBody;
import org.dows.framework.api.exceptions.RestException;
import org.dows.framework.rest.factory.CallAdapterFactory;
import retrofit2.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;


/**
 * 同步调用，如果返回的http状态码是是成功，返回responseBody 反序列化之后的对象。否则，抛出异常！异常信息中包含请求和响应相关信息。
 */
public final class BodyCallAdapterFactory extends CallAdapter.Factory implements CallAdapterFactory {

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (Call.class.isAssignableFrom(getRawType(returnType))) {
            return null;
        }
        if (CompletableFuture.class.isAssignableFrom(getRawType(returnType))) {
            return null;
        }
        if (Response.class.isAssignableFrom(getRawType(returnType))) {
            return null;
        }
        return new BodyCallAdapter(returnType, annotations, retrofit);
    }

    final class BodyCallAdapter<R> implements CallAdapter<R, R> {

        private final Type returnType;

        private final Retrofit retrofit;

        private final Annotation[] annotations;

        BodyCallAdapter(Type returnType, Annotation[] annotations, Retrofit retrofit) {
            this.returnType = returnType;
            this.retrofit = retrofit;
            this.annotations = annotations;
        }

        @Override
        public Type responseType() {
            return returnType;
        }

        @Override
        public R adapt(Call<R> call) {
            Response<R> response;
            Request request = call.request();
            try {
                response = call.execute();
            } catch (IOException e) {
                throw new RestException(e.getMessage());
            }

            if (response.isSuccessful()) {
                return response.body();
            }

            ResponseBody errorBody = response.errorBody();
            if (errorBody == null) {
                return null;
            }
            Converter<ResponseBody, R> converter = retrofit.responseBodyConverter(responseType(), annotations);
            try {
                return converter.convert(Objects.requireNonNull(errorBody));
            } catch (IOException e) {
                //throw Objects.requireNonNull(RetrofitException.errorExecuting(request, e));
                throw new RestException(e.getMessage());
            }
        }
    }
}
