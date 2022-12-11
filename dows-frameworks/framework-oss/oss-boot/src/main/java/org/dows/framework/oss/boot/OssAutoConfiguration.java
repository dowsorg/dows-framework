package org.dows.framework.oss.boot;

import cn.hutool.extra.spring.EnableSpringUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@EnableSpringUtil
@Configuration
@ComponentScan(basePackages = "org.dows.framework.oss")
@EnableConfigurationProperties(OssProperties.class)
public class OssAutoConfiguration {

}
