package org.dows.framework.oss.boot;

import org.dows.framework.oss.api.OssSetting;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    private String endpoint;
    // 代码里是 secretId
    private String accessKey;
    private String secretKey;

    private Map<String, OssSetting> settings;
}
