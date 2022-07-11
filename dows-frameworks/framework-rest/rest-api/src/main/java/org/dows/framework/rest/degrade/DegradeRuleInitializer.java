package org.dows.framework.rest.degrade;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/2/2022
 */
public interface DegradeRuleInitializer {
    void addDegradeRule(DegradeRule degradeRule);
}
