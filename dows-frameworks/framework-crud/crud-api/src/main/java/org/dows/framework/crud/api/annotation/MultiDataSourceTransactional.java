package org.dows.framework.crud.api.annotation;

import java.lang.annotation.*;

/**
 * 多数据源事务注解
 * https://www.jianshu.com/p/e5bc043863c7
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MultiDataSourceTransactional {

    /**
     * 事务管理器数组
     */
    String[] transactionManagers() default {};

}
