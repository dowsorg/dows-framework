package org.dows.framework.crud.mybatis.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.dows.framework.crud.mybatis.interceptor.MybatisSqlInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * logging:
 * level:
 * com.xxx.ddd: debug
 */
@Configuration
@ConditionalOnBean(SqlSessionFactory.class)
public class MybatisSqlAutoConfiguration {

    @Resource
    private List<SqlSessionFactory> sqlSessionFactoryList;

    @PostConstruct
    public void addSqlInterceptor() {
        MybatisSqlInterceptor interceptor = new MybatisSqlInterceptor();
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
        }
    }
}
