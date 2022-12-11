package org.dows.framework.oss.qiniu.model;

import org.dows.framework.oss.api.SliceConfig;
import org.dows.framework.oss.api.utils.OssPathUtil;
import org.dows.framework.oss.qiniu.constant.QiNiuRegion;
import lombok.Data;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 5/8/2022
 */
@Data
public class QiNiuOssConfig {

    private String basePath;
    private String accessKey;
    private String secretKey;
    private QiNiuRegion region = QiNiuRegion.AUTOREGION;
    private String bucketName;

    /**
     * 断点续传参数
     */
    private SliceConfig sliceConfig = new SliceConfig();

    public void init() {
        this.sliceConfig.init();
        basePath = OssPathUtil.valid(basePath);
    }
}
