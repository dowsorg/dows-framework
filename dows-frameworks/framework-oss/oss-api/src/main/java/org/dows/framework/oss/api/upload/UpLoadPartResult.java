package org.dows.framework.oss.api.upload;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 分块结果集
 */
@Accessors(chain = true)
@Data
public class UpLoadPartResult {

    /**
     * 分块号
     */
    private int number;
    /**
     * 分块在文件中的偏移量
     */
    private long offset;
    /**
     * 分块大小
     */
    private long length;
    /**
     * 分块失败标识
     */
    private boolean failed = false;
    /**
     * 分块上传失败异常
     */
    private Exception exception;
    /**
     * 分块crc
     */
    private Long partCrc;

    private UpLoadPartEntityTag entityTag;

    public UpLoadPartResult(int number, long offset, long length) {
        this.number = number;
        this.offset = offset;
        this.length = length;
    }

    public UpLoadPartResult(int number, long offset, long length, long partCrc) {
        this.number = number;
        this.offset = offset;
        this.length = length;
        this.partCrc = partCrc;
    }

}
