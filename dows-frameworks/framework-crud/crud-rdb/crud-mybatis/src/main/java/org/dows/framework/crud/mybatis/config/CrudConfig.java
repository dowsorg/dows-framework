package org.dows.framework.crud.mybatis.config;

import org.dows.framework.crud.mybatis.CrudContext;
import org.dows.framework.crud.mybatis.utils.FieldFillHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CrudConfig {

    @Bean
    public CrudContext crudContext() {
        return new CrudContext();
    }

    @Bean
    public FieldFillHandler fieldFillConfig() {
        return new FieldFillHandler();
    }
}
