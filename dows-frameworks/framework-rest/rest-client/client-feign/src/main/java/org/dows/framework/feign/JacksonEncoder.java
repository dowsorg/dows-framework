package org.dows.framework.feign;

import feign.RequestTemplate;
import feign.Util;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.dows.framework.feign.util.FeignJacksonUtil;

import java.lang.reflect.Type;

/**
 * jackson编码器
 */
public class JacksonEncoder implements Encoder {
    @Override
    public void encode(Object object, Type bodyType, RequestTemplate requestTemplate) throws EncodeException {
        try {
            if (bodyType == String.class) {
                requestTemplate.body(object.toString());
            } else if (bodyType == byte[].class) {
                requestTemplate.body(((byte[]) object), Util.UTF_8);
            } else {
                requestTemplate.body(FeignJacksonUtil.obj2json(object));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new EncodeException(String.format("%s is not a type supported by this encoder.", object.getClass()));
        }
    }
}
