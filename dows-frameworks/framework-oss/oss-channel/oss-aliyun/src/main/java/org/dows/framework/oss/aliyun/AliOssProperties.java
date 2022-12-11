package org.dows.framework.oss.aliyun;

import cn.hutool.core.text.CharPool;
import org.dows.framework.oss.aliyun.model.AliOssConfig;
import org.dows.framework.oss.api.constant.OssConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(OssConstant.OSS + CharPool.DOT + OssConstant.OssType.ALI)
public class AliOssProperties extends AliOssConfig implements InitializingBean {

    private Boolean enable = false;

    private Map<String, AliOssConfig> ossConfig = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        if (ossConfig.isEmpty()) {
            this.init();
        } else {
            ossConfig.values().forEach(AliOssConfig::init);
        }
    }

}
