package org.dows.framework.oss.tencent;

import cn.hutool.core.text.CharPool;
import org.dows.framework.oss.api.constant.OssConstant;
import org.dows.framework.oss.tencent.model.TencentOssConfig;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(OssConstant.OSS + CharPool.DOT + OssConstant.OssType.TENCENT)
public class TencentOssProperties extends TencentOssConfig implements InitializingBean {

    private Boolean enable = false;

    private Map<String, TencentOssConfig> ossConfig = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        if (ossConfig.isEmpty()) {
            this.init();
        } else {
            ossConfig.values().forEach(TencentOssConfig::init);
        }
    }

}
