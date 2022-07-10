package org.dows.framework.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ResponseBodyAdviceExcludeProperties.class)
@ConfigurationProperties(prefix = "exception.response")
public class ResponseBodyAdviceExcludeProperties {

    /**
     * 根据 contextPath 路径排除对返回结果的包装. 例如: /sso, 默认为 null 值.
     */
    private String contextPathExclude;

    public String getContextPathExclude() {
        return contextPathExclude;
    }

    public void setContextPathExclude(String contextPathExclude) {
        this.contextPathExclude = contextPathExclude;
    }

    ;

}
