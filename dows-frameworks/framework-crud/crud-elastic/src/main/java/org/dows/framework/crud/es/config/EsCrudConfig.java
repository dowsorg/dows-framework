package org.dows.framework.crud.es.config;

//import org.dows.sms.crud.es.EsUtil;

import org.dows.framework.crud.es.ElasticSearchRestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = {"org.dows.framework.crud.es"})
@Configuration
public class EsCrudConfig {

    @Bean
    public ElasticSearchRestClient getElasticSearchRestClient() {
        return new ElasticSearchRestClient();
    }

}
