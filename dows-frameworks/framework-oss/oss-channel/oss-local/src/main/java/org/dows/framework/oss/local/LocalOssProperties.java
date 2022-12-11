package org.dows.framework.oss.local;

import cn.hutool.core.text.CharPool;
import org.dows.framework.oss.api.constant.OssConstant;
import org.dows.framework.oss.local.model.LocalOssConfig;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;


/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/26/2022
 */
@Data
@ConfigurationProperties(OssConstant.OSS + CharPool.DOT + OssConstant.OssType.LOCAL)
public class LocalOssProperties extends LocalOssConfig implements InitializingBean {

    private Boolean enable = false;

    private Map<String, LocalOssConfig> ossConfig = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        if (ossConfig.isEmpty()) {
            this.init();
        } else {
            ossConfig.values().forEach(LocalOssConfig::init);
        }
    }
}
