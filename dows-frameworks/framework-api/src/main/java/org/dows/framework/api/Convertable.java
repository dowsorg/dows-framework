package org.dows.framework.api;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;


public interface Convertable {
    default <T> T convert(Object obj, Class<T> clzz) {
        if (null == obj || null == clzz) {
            return null;
        }
        try {
            T t = clzz.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(obj, t);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    default <T> List<T> converts(List<?> objs, Class<T> clzz) {
        List<T> list = new ArrayList<>();
        try {
            for (Object obj : objs) {
                T t = clzz.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(obj, t);
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
