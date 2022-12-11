package org.dows.framework.oss.qiniu;

import cn.hutool.core.text.CharPool;
import org.dows.framework.oss.api.constant.OssConstant;
import org.dows.framework.oss.qiniu.model.QiNiuOssConfig;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 5/8/2022
 */
@Data
@ConfigurationProperties(OssConstant.OSS + CharPool.DOT + OssConstant.OssType.QINIU)
public class QiNiuOssProperties extends QiNiuOssConfig implements InitializingBean {

    private Boolean enable = false;

    private Map<String, QiNiuOssConfig> ossConfig = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        if (ossConfig.isEmpty()) {
            this.init();
        } else {
            ossConfig.values().forEach(QiNiuOssConfig::init);
        }
    }

}
