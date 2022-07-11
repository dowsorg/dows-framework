package org.dows.framework.rest;

import com.dows.framework.crypto.boot.EnableCrypto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crypto.api.ApiCryptor;
import org.dows.framework.rest.degrade.RetrofitDegradeRuleInitializer;
import org.dows.framework.rest.property.RestProperties;
import org.dows.framewrok.retrofit.core.CryptoConverterFactory;
import org.dows.framewrok.retrofit.core.RetrofitResourceNameParser;
import org.dows.framewrok.retrofit.interceptor.RetrofitDegradeInterceptor;
import org.dows.framewrok.retrofit.interceptor.RetrofitFilterInterceptor;
import org.dows.framewrok.retrofit.interceptor.RetrofitLoggingInterceptor;
import org.dows.framewrok.retrofit.interceptor.RetrofitRetryInterceptor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.List;

@Slf4j
@Configuration
@EnableConfigurationProperties(RestProperties.class)
@AutoConfigureAfter({JacksonAutoConfiguration.class})
@EnableCrypto
public class RestAutoConfiguration {


    @Autowired
    private List<ApiCryptor> apiCryptors;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public static PrototypeInterceptorBdfProcessor prototypeInterceptorBdfProcessor() {
        return new PrototypeInterceptorBdfProcessor();
    }

    @Bean
    public CryptoConverterFactory cryptoConverterFactory() {
        return CryptoConverterFactory.create(apiCryptors, objectMapper);
    }

    @Bean
    public RetrofitDegradeInterceptor retrofitDegradeInterceptor(Environment environment, RestProperties restProperties) {
        return new RetrofitDegradeInterceptor(environment, retrofitResourceNameParser(), restProperties);
    }

    @Bean
    public RetrofitFilterInterceptor retrofitFilterInterceptor(RestProperties restProperties) {
        return new RetrofitFilterInterceptor(restProperties);
    }

    @Bean
    public RetrofitLoggingInterceptor retrofitLoggingInterceptor(RestProperties restProperties) {
        return new RetrofitLoggingInterceptor(restProperties);
    }


/*    @Bean
    public RetrofitBalanceInterceptor retrofitBalanceInterceptor(LoadBalancerClient loadBalancerClient){
        return new RetrofitBalanceInterceptor(springCloudServiceInstanceBalance(loadBalancerClient));
    }*/

/*    @Bean
    SpringCloudServiceInstanceBalance springCloudServiceInstanceBalance(){
        return new SpringCloudServiceInstanceBalance(loadBalancerClient());
    }

    @Bean
    LoadBalancerClient loadBalancerClient(){
        return new BlockingLoadBalancerClient();
    }*/

    @Bean
    public RetrofitRetryInterceptor retrofitRetryInterceptor(RestProperties restProperties) {
        return new RetrofitRetryInterceptor(restProperties);
    }

    @Bean
    public RetrofitResourceNameParser retrofitResourceNameParser() {
        return new RetrofitResourceNameParser();
    }

    @Bean
    public RetrofitDegradeRuleInitializer retrofitDegradeRuleInitializer(@Autowired RestProperties restProperties) {
        return new RetrofitDegradeRuleInitializer(restProperties.getDegrade());
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper jacksonObjectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Bean
    @ConditionalOnMissingBean
    @Autowired
    public JacksonConverterFactory jacksonConverterFactory(ObjectMapper objectMapper) {
        return JacksonConverterFactory.create(objectMapper);
    }


    @Configuration
    @Import({AutoConfiguredRetrofitScannerRegistrar.class})
    @ConditionalOnMissingBean(RetrofitFactoryBean.class)
    public static class RetrofitScannerRegistrarNotFoundConfiguration implements InitializingBean {
        @Override
        public void afterPropertiesSet() {
            log.debug("No {} found.", RetrofitFactoryBean.class.getName());
        }
    }

}
