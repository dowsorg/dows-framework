package org.dows.framework.crud.mybatis.utils;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Slf4j
public class FieldFillHandler implements MetaObjectHandler {
    private final Map<String, Class<?>> fieldTypMap = new HashMap<>();

    @PostConstruct
    public void init() {
        fieldTypMap.put("deleted", Boolean.class);
        fieldTypMap.put("dt", Date.class);
        fieldTypMap.put("updateDt", Date.class);
        fieldTypMap.put("name", String.class);
        fieldTypMap.put("label", String.class);
        fieldTypMap.put("title", String.class);
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        fieldTypMap.forEach((k, v) -> {
            String fieldLetter = k + "Code";
            if (metaObject.hasSetter(k)) {
                Object o = metaObject.getValue(k);
                if (o == null && v.getName().equals("java.lang.Boolean")) {
                    fillStrategy(metaObject, k, false);
                } else if (o == null && v.getName().equals("java.util.Date")) {
                    fillStrategy(metaObject, k, new Date());
                }
            } else if (metaObject.hasSetter(fieldLetter)) {
                Object o = metaObject.getValue(fieldLetter);
                if (o != null && v.getName().equals("java.lang.String") && !o.toString().equals("")) {
                    try {
                        setFieldValByName(fieldLetter, PinyinHelper.getShortPinyin(o.toString()), metaObject);
                    } catch (PinyinException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        fieldTypMap.forEach((k, v) -> {
            String fieldLetter = k + "Code";
            if (metaObject.hasSetter(k)) {
                Object o = metaObject.getValue(k);
                if (o == null && v.getName().equals("java.lang.Boolean")) {
                    fillStrategy(metaObject, k, false);
                } else if (o == null && v.getName().equals("java.util.Date")) {
                    fillStrategy(metaObject, k, new Date());
                }
            } else if (metaObject.hasSetter(fieldLetter)) {
                Object o = metaObject.getValue(fieldLetter);
                if (o != null && v.getName().equals("java.lang.String") && !o.toString().equals("")) {
                    try {
                        setFieldValByName(fieldLetter, PinyinHelper.getShortPinyin(o.toString()), metaObject);
                    } catch (PinyinException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        });
    }


}
