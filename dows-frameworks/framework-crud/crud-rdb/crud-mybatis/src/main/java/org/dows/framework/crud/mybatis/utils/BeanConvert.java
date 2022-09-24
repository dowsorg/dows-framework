package org.dows.framework.crud.mybatis.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class BeanConvert {

    public static <T> T convert(Object obj, Class<T> clzz) {
        if (null == obj || null == clzz) {
            return null;
        }
        try {
            T t = clzz.newInstance();
            BeanUtils.copyProperties(obj, t);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static <T> List<T> converts(List<?> objs, Class<T> clzz) {
        List<T> list = new ArrayList<>();
        try {
            for (Object obj : objs) {
                T t = clzz.newInstance();
                BeanUtils.copyProperties(obj, t);
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
