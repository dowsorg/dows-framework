package org.dows.framework.rest.property;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dows.framework.rest.retry.RetryRule;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/3/2022
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RetryProperty extends RestProperty {

    /**
     * 全局最大重试次数，最大可设置为100
     */
    private int maxRetries = 1;

    /**
     * 全局重试时间间隔
     */
    private int interval = 100;

    /**
     * 重试规则，默认 响应状态码不是2xx 或者 发生IO异常 时触发重试
     */

    private RetryRule[] retryRules = {RetryRule.RESPONSE_STATUS_NOT_2XX, RetryRule.OCCUR_IO_EXCEPTION};
}
