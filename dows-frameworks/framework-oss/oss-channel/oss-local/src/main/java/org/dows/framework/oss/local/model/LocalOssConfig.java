package org.dows.framework.oss.local.model;

import org.dows.framework.oss.api.SliceConfig;
import org.dows.framework.oss.api.utils.OssPathUtil;
import lombok.Data;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 3/26/2022
 */
@Data
public class LocalOssConfig {

    /**
     * 数据存储路径
     */
    private String basePath;

    /**
     * 断点续传参数
     */
    private SliceConfig sliceConfig = new SliceConfig();

    public void init() {
        this.sliceConfig.init();
        basePath = OssPathUtil.valid(basePath);
    }

}
