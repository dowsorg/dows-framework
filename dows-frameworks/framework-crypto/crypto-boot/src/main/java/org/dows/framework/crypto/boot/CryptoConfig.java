package org.dows.framework.crypto.boot;

import org.dows.crypto.api.CryptoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CryptoConfig {
    @Bean
    @ConfigurationProperties(prefix = "api.crypto")
    public CryptoProperties apiCryptoConfig() {
        return new CryptoProperties();
    }
}
