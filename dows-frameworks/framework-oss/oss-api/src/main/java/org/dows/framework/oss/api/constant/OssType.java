package org.dows.framework.oss.api.constant;


public enum OssType {

    /**
     * 本地磁盘存储
     */
    LOCAL(OssConstant.OssType.LOCAL),

    /**
     * FTP协议存储
     */
    FTP(OssConstant.OssType.FTP),

    /**
     * SFTP存储
     */
    SFTP(OssConstant.OssType.SFTP),

    /**
     * 阿里OSS存储
     */
    ALI(OssConstant.OssType.ALI),

    /**
     * 七牛云存储
     */
    QINIU(OssConstant.OssType.QINIU),

    /**
     * MinIO存储
     */
    MINIO(OssConstant.OssType.MINIO),


    /**
     * 腾讯云存储
     */
    TENCENT(OssConstant.OssType.TENCENT),

    ;

    private final String value;

    OssType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
