package org.dows.framework.oss.api;

import org.dows.framework.oss.api.constant.OssConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 断点续传参数
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SliceConfig {
    /**
     * 分片大小,默认5MB
     */
    private Long partSize = OssConstant.DEFAULT_PART_SIZE;

    /**
     * 并发线程数,默认等于CPU的核数
     */
    private Integer taskNum = OssConstant.DEFAULT_TASK_NUM;

    public void init() {
        if (this.getPartSize() <= 0) {
            log.warn("断点续传——分片大小必须大于0");
            this.setPartSize(OssConstant.DEFAULT_PART_SIZE);
        }
        if (this.getTaskNum() <= 0) {
            log.warn("断点续传——并发线程数必须大于0");
            this.setTaskNum(OssConstant.DEFAULT_TASK_NUM);
        }
    }
}
