package org.dows.framework.crud.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: 非查询自动填入
 * @Author :lait.zhang@gmail.com
 * @Date:14:58 2022/1/24
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TenantNameInsert {
}
