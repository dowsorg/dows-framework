//package org.dows.framewrok.retrofit.interceptor;
//
//import cn.hutool.json.JSONUtil;
//import com.sun.jndi.toolkit.url.UrlUtil;
//import okhttp3.*;
//import okio.Buffer;
//
//import java.io.IOException;
//import java.net.URLDecoder;
//import java.util.*;
//
///**
// * @author lait.zhang@gmail.com
// * @description: TODO
// * @weixin SH330786
// * @date 4/10/2022
// */
//public class RequestInterceptor implements Interceptor {
//
//    /**
//     * headerLines 参数List
//     */
//    private List<String> headerLinesList = new ArrayList<>();
//    /**
//     * header 参数Map
//     */
//    private Map<String, String> headerParamsMap = new HashMap<>();
//    /**
//     * url 参数Map
//     */
//    private Map<String, String> urlParamsMap = new HashMap<>();
//    /**
//     * body 参数Map
//     */
//    private Map<String, String> bodyParamsMap = new HashMap<>();
//    /**
//     * 加入动态参数
//     */
//    private DynamicParams dynamicParams;
//    /**
//     * 提交的是否是Json数据
//     */
//    private boolean isJson;
//
//    private RequestInterceptor(boolean isJson) {
//        this.isJson = isJson;
//    }
//
//    @Override
//    public Response intercept(Chain chain) throws IOException {
//        Request request = chain.request();
//        Request.Builder requestBuilder = request.newBuilder().addHeader("Connection", "close");
//
//        Headers.Builder headerBuilder = request.headers().newBuilder();
//        if (headerLinesList.size() > 0) {
//            for (String line : headerLinesList) {
//                headerBuilder.add(line);
//            }
//        }
//        if (headerParamsMap.size() > 0) {
//            for (Map.Entry entry : headerParamsMap.entrySet()) {
//                headerBuilder.add((String) entry.getKey(), (String) entry.getValue());
//            }
//        }
//        requestBuilder.headers(headerBuilder.build());
//
//        if (urlParamsMap.size() > 0) {
//            injectParamsIntoUrl(request, requestBuilder, urlParamsMap);
//        }
//
//
//        if (request.method().equals("POST")) {
//            RequestBody body = request.body();
//            // POST上传文件
//            if (body instanceof MultipartBody) {
//                StringBuilder stringBuilder = new StringBuilder();
//                if (bodyParamsMap.size() > 0) {
//                    for (Map.Entry entry : bodyParamsMap.entrySet()) {
//                        stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
//                    }
//                }
//                String postBodyString = stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
//                //相同
//                if (null != dynamicParams) {
//                    HttpUrl url = request.url();
//                    String route = url.toString().replace(UrlUtil.host, "");
//                    if (route.contains("?")) {
//                        route = route.substring(0, route.indexOf("?"));
//                    }
//                    // 此处把已有参数传到dynamicParams中，可以做一些签名加密添加动态参数等操作，最终返回key1=value1&key2=value2&...	下面
//                    postBodyString = dynamicParams.signParams(postBodyString, request.method(), route);
//                }
//
//                MultipartBody.Builder newMultipartBodyBuilder = new MultipartBody.Builder();
//                newMultipartBodyBuilder.setType(MultipartBody.FORM);
//                // 最终参数作为form参数放到MultipartBody
//                TreeMap<String, String> treeMap = splitPostString(postBodyString);
//                for (Map.Entry entry : treeMap.entrySet()) {
//                    newMultipartBodyBuilder.addFormDataPart((String) entry.getKey(), (String) entry.getValue());
//                }
//
//                // File可以不参与签名，最后把File作为part参数放到MultipartBody中
//                for (MultipartBody.Part part : ((MultipartBody) request.body()).parts()) {
//                    newMultipartBodyBuilder.addPart(part);
//                }
//
//                requestBuilder.post(newMultipartBodyBuilder.build());
//            } else {
//                // 一般POST请求
//                FormBody.Builder formBodyBuilder = new FormBody.Builder();
//                // add new params to new formBodyBuilder
//                if (bodyParamsMap.size() > 0) {
//                    for (Map.Entry entry : bodyParamsMap.entrySet()) {
//                        formBodyBuilder.add((String) entry.getKey(), (String) entry.getValue());
//                    }
//                }
//                // add old params to new formBodyBuilder
//                FormBody formBody = formBodyBuilder.build();
//                String postBodyString = bodyToString(request.body());
//
//                postBodyString += ((postBodyString.length() > 0) ? "&" : "") + bodyToString(formBody);
//
//                if (null != dynamicParams) {
//                    HttpUrl url = request.url();
//                    String route = url.toString().replace(UrlUtil.host, "");
//                    if (route.contains("?")) {
//                        // /api/goods/sale/list?o=hot 类似这样的需要把问号后面的去掉
//                        route = route.substring(0, route.indexOf("?"));
//                    }
//                    postBodyString = dynamicParams.signParams(postBodyString, request.method(), route);
//                }
//                if (isJson) {
//                    Map<String, String> stringMap = new HashMap<>();
//                    for (String value : postBodyString.split("&")) {
//                        stringMap.put(value.split("=")[0], value.split("=")[1]);
//                    }
//                    postBodyString = JSONUtil.toJsonStr(stringMap);
//                }
//                requestBuilder.post(RequestBody.create(postBodyString, formBody.contentType()));
//            }
//        } else {// GET
//            FormBody.Builder formBodyBuilder = new FormBody.Builder();
//            if (bodyParamsMap.size() > 0) {
//                for (Map.Entry entry : bodyParamsMap.entrySet()) {
//                    formBodyBuilder.add((String) entry.getKey(), (String) entry.getValue());
//                }
//            }
//
//            HttpUrl httpUrl = request.url();
//            Set<String> queryKeys = httpUrl.queryParameterNames();
//            for (String key : queryKeys) {
//                String value = httpUrl.queryParameter(key);
//                if (value != null) formBodyBuilder.add(key, value);
//            }
//
//            FormBody formBody = formBodyBuilder.build();
//            String postBodyString = bodyToString(request.body());
//
//            postBodyString += ((postBodyString.length() > 0) ? "&" : "") + bodyToString(formBody);
//            postBodyString = URLDecoder.decode(postBodyString, "utf8");
//            if (null != dynamicParams) {
//                HttpUrl url = request.url();
//                String route = url.toString().replace(UrlUtil.host, "");
//                if (route.contains("?")) {
//                    // /api/goods/sale/list?o=hot 类似这样的需要把问号后面的去掉
//                    route = route.substring(0, route.indexOf("?"));
//                }
//                postBodyString = dynamicParams.signParams(postBodyString, request.method(), route);
//            }
//            Map<String, String> stringMap = new HashMap<>();
//            for (String value : postBodyString.split("&")) {
//                String[] split = value.split("=");
//                stringMap.put(split[0], split.length > 1 ? split[1] : "");
//            }
//
//            // if can't inject into body, then inject into url
//            injectParamsIntoUrl(request, requestBuilder, stringMap);
//            // injectParamsIntoUrl(request, requestBuilder, bodyParamsMap);
//        }
//
//        request = requestBuilder.build();
//
//        Response originalResponse = chain.proceed(request);
//        Response priorResponse = originalResponse.priorResponse();
//        // 如果是重定向，那么就执行重定向请求后再返回数据。
//        if (null != priorResponse && priorResponse.isRedirect()) {
//            Request redirectRequest = request.newBuilder().url(originalResponse.request().url()).build();
//            originalResponse = chain.proceed(redirectRequest);
//        }
//        return originalResponse;
//    }
//
//    // func to inject params into url
//    private void injectParamsIntoUrl(Request request, Request.Builder requestBuilder, Map<String, String> paramsMap) {
//        HttpUrl.Builder httpUrlBuilder = request.url().newBuilder();
//        if (paramsMap.size() > 0) {
//            for (Map.Entry entry : paramsMap.entrySet()) {
////                httpUrlBuilder.addQueryParameter((String) entry.getKey(), (String) entry.getValue());
//                httpUrlBuilder.setQueryParameter((String) entry.getKey(), (String) entry.getValue());
//            }
//        }
////        LzLogUtil.e("request url", httpUrlBuilder.toString());
//        requestBuilder.url(httpUrlBuilder.build());
//    }
//
//    // RequestBody to String
//    private String bodyToString(final RequestBody request) {
//        try {
//            final Buffer buffer = new Buffer();
//            if (request != null)
//                request.writeTo(buffer);
//            else
//                return "";
//            return buffer.readUtf8();
//        } catch (IOException e) {
//            return "did not work";
//        }
//    }
//
//    // MultipartBody比较特殊，把MultipartBody转换成 k1=v1&k2=v2&kn=vn
//    private String multipartBodyToString(MultipartBody body) {
//        String string = "";
//        try {
//            Buffer buffer = new Buffer();
//            body.writeTo(buffer);
//            String postParams = buffer.readUtf8();
//            String[] split = postParams.split("\n");
//
//            List<String> names = new ArrayList<>();
//            // Content-Disposition: form-data; name="key"
//            for (String s : split) {
//                if (s.contains("Content-Disposition")) {
//                    String key = s.replace("Content-Disposition: form-data; name=", "")
//                            .replace("\"", "")
//                            .replace("\r", "")
//                            .replace("\t", "");
////                    if (str.contains(";")) {
////                        key = key.substring(key.indexOf(";") + 1).trim();
////                    }
//                    names.add(key.trim());
//                }
//            }
//            List<MultipartBody.Part> parts = body.parts();
//            StringBuilder builder = new StringBuilder();
//            for (int i = 0; i < parts.size(); i++) {
//                MultipartBody.Part part = parts.get(i);
//                RequestBody body1 = part.body();
//                if (body1.contentLength() < 100) {
//                    Buffer buffer1 = new Buffer();
//                    body1.writeTo(buffer1);
//                    String value = buffer1.readUtf8();
//                    //打印 name和value
//                    if (names.size() > i) {
////                        LogUtil.e("aaaaaaaa", "params-->" + names.get(i) + "=" + value);
//                        builder.append(names.get(i)).append("=").append(value).append("&");
//                    }
//                } else {
//                    if (names.size() > i) {
////                        LogUtil.e("aaaaaaaa", "params-->" + names.get(i));
//                        builder.append(names.get(i));
//                    }
//                }
//            }
//            string = builder.toString();
//            if (string.lastIndexOf("&") == (string.length() - 1)) {
//                string = string.substring(0, string.length() - 1);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return string;
//    }
//
//    private TreeMap<String, String> splitPostString(String postBodyString) {
//        TreeMap<String, String> map = new TreeMap<>();
//        for (String s : postBodyString.split("&")) {
//            String[] keyValue = s.split("=");
//            map.put(keyValue[0], keyValue.length > 1 ? keyValue[1] : "");
//        }
//        return map;
//    }
//
//    public void setIBasicDynamic(DynamicParams dynamicParams) {
//        this.dynamicParams = dynamicParams;
//    }
//
//    public static class Builder {
//        RequestInterceptor interceptor;
//
//        /**
//         * @param isJson 提交的是否是Json数据
//         */
//        public Builder(boolean isJson) {
//            interceptor = new RequestInterceptor(isJson);
//        }
//
//        public RequestInterceptor.Builder addHeaderLine(String headerLine) {
//            int index = headerLine.indexOf(":");
//            if (index == -1) {
//                throw new IllegalArgumentException("Unexpected header: " + headerLine);
//            }
//            interceptor.headerLinesList.add(headerLine);
//            return this;
//        }
//
//        public RequestInterceptor.Builder addHeaderLinesList(List<String> headerLinesList) {
//            for (String headerLine : headerLinesList) {
//                int index = headerLine.indexOf(":");
//                if (index == -1) {
//                    throw new IllegalArgumentException("Unexpected header: " + headerLine);
//                }
//                interceptor.headerLinesList.add(headerLine);
//            }
//            return this;
//        }
//
//        public RequestInterceptor.Builder addHeaderParam(String key, String value) {
//            interceptor.headerParamsMap.put(key, value);
//            return this;
//        }
//
//        public RequestInterceptor.Builder addHeaderParamsMap(Map<String, String> headerParamsMap) {
//            interceptor.headerParamsMap.putAll(headerParamsMap);
//            return this;
//        }
//
//        public RequestInterceptor.Builder addUrlParam(String key, String value) {
//            interceptor.urlParamsMap.put(key, value);
//            return this;
//        }
//
//        public RequestInterceptor.Builder urlParamsMap(Map<String, String> urlParamsMap) {
//            interceptor.urlParamsMap.putAll(urlParamsMap);
//            return this;
//        }
//
//        public RequestInterceptor.Builder addBodyParam(String key, String value) {
//            interceptor.bodyParamsMap.put(key, value);
//            return this;
//        }
//
//        public RequestInterceptor.Builder addBodyParamsMap(Map<String, String> bodyParamsMap) {
//            interceptor.bodyParamsMap.putAll(bodyParamsMap);
//            return this;
//        }
//
//        public RequestInterceptor build() {
//            return interceptor;
//        }
//    }
//}
