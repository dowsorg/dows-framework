package org.dows.framewrok.retrofit;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/10/2022
 */
public class RequestParamUtil {

    /**
     * @param retrofitRequestAdapter
     * @return
     */
    public static Request handlerRequest(RetrofitRequestAdapter retrofitRequestAdapter) {
        // todo 获取signConent 设置到request中
        String signContent = retrofitRequestAdapter.getCryptoContentPayload();
        Request request = retrofitRequestAdapter.unwrap();
        if ("GET".equals(retrofitRequestAdapter.getMethod())) {
            request.newBuilder().url(retrofitRequestAdapter.getRequestUrl()).build();
        } else {

        }
        return request;
    }


    /**
     * 重新设置request参数
     *
     * @param request
     * @return
     */
    public static Request handlerRequest(Request request) {
        Map<String, String> params = RequestParamUtil.parseParams(request);
        if (params == null) {
            params = new HashMap<>();
        }
        //这里为公共的参数
        String method = request.method();
        if ("GET".equals(method)) {
            StringBuilder sb = new StringBuilder();
            // 追加参数
            sb.append("?").append(params);
            return request.newBuilder().url(sb.toString()).build();
        } else if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method) || "PATCH".equals(method)) {
            if (request.body() instanceof FormBody) {
                FormBody.Builder bodyBuilder = new FormBody.Builder();
                Iterator<Map.Entry<String, String>> entryIterator = params.entrySet().iterator();
                while (entryIterator.hasNext()) {
                    String key = entryIterator.next().getKey();
                    String value = entryIterator.next().getValue();
                    bodyBuilder.add(key, value);
                }
                return request.newBuilder().method(method, bodyBuilder.build()).build();
            }
        }
        return request;
    }

    /**
     * 解析请求参数
     *
     * @param request
     * @return
     */
    public static Map<String, String> parseParams(Request request) {
        //GET POST DELETE PUT PATCH
        String method = request.method();
        Map<String, String> params = null;
        if ("GET".equals(method)) {
            params = doGet(request);
        } else if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method) || "PATCH".equals(method)) {
            RequestBody body = request.body();
            if (body != null && body instanceof FormBody) {
                params = doForm(request);
            }
        }
        return params;
    }

    /**
     * 获取get方式的请求参数
     *
     * @param request
     * @return
     */
    private static Map<String, String> doGet(Request request) {
        Map<String, String> params = null;
        HttpUrl url = request.url();
        Set<String> strings = url.queryParameterNames();
        if (strings != null) {
            Iterator<String> iterator = strings.iterator();
            params = new HashMap<>();
            int i = 0;
            while (iterator.hasNext()) {
                String name = iterator.next();
                String value = url.queryParameterValue(i);
                params.put(name, value);
                i++;
            }
        }
        return params;
    }

    /**
     * 获取表单的请求参数
     *
     * @param request
     * @return
     */
    private static Map<String, String> doForm(Request request) {
        Map<String, String> params = null;
        FormBody body = null;
        try {
            body = (FormBody) request.body();
        } catch (ClassCastException c) {
        }
        if (body != null) {
            int size = body.size();
            if (size > 0) {
                params = new HashMap<>();
                for (int i = 0; i < size; i++) {
                    params.put(body.name(i), body.value(i));
                }
            }
        }
        return params;
    }


}
