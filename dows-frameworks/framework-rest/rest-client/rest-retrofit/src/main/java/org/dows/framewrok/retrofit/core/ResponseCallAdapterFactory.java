package org.dows.framewrok.retrofit.core;

import org.dows.framework.rest.factory.CallAdapterFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.springframework.util.Assert;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * 同步调用执行，直接返回 #{@link Response} 对象
 */
@Slf4j
public final class ResponseCallAdapterFactory extends CallAdapter.Factory implements CallAdapterFactory {

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (Response.class.isAssignableFrom(getRawType(returnType))) {
            return new ResponseCallAdapter(returnType);
        }
        return null;
    }

    final class ResponseCallAdapter<R> implements CallAdapter<R, Response<R>> {

        private final Type returnType;

        ResponseCallAdapter(Type returnType) {
            this.returnType = returnType;
        }

        @Override
        public Type responseType() {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            Assert.notEmpty(actualTypeArguments, "Response must specify generic parameters!");
            return actualTypeArguments[0];
        }


        @Override
        public Response<R> adapt(Call<R> call) {
            Request request = call.request();
            try {
                return call.execute();
            } catch (IOException e) {
                log.info(e.getMessage());
                //throw Objects.requireNonNull(Exception.errorExecuting(request, e));
            }
            return null;
        }
    }
}
