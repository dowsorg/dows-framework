package org.dows.framework.rest.property;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dows.framework.rest.balance.ServiceBalance;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/3/2022
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BalanceProperty extends RestProperty {

    /**
     * 均衡器
     */
    private Class<? extends ServiceBalance> balancer;
}
