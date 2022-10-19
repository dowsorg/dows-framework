package org.dows.framework.feign;

import feign.Response;
import feign.Util;
import feign.codec.Decoder;
import org.dows.framework.api.exceptions.RemotingException;
import org.dows.framework.feign.util.FeignJacksonUtil;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 基于spring jackson 的json解码器
 */
public class JacksonDecoder implements Decoder {
    @Override
    public Object decode(Response response, Type type) throws IOException {
        if (response.status() != 404 && response.status() != 204) {
            Response.Body body = response.body();
            if (body == null) {
                return null;
            } else if (String.class.equals(type)) {
                return Util.toString(body.asReader(Util.UTF_8));
            } else if (byte[].class.equals(type)) {
                return Util.toByteArray(response.body().asInputStream());
            } else {
                try {
                    return FeignJacksonUtil.json2pojo(Util.toString(body.asReader(Util.UTF_8)), type.getClass());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RemotingException(e.getMessage());
                }
            }
        } else {
            return Util.emptyValueOf(type);
        }
    }
}
