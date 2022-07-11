package org.dows.framework.rest.degrade;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.rest.property.DegradeProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class RetrofitDegradeRuleInitializer implements ApplicationListener<ApplicationReadyEvent>, DegradeRuleInitializer {

    private static final List<DegradeRule> LIST = new CopyOnWriteArrayList<>();
    private final DegradeProperty degradeProperty;

    public RetrofitDegradeRuleInitializer(DegradeProperty degradeProperty) {
        this.degradeProperty = degradeProperty;
    }

    @Override
    public void addDegradeRule(DegradeRule degradeRule) {
        if (degradeRule == null) {
            return;
        }
        LIST.add(degradeRule);
    }

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!degradeProperty.isEnable()) {
            return;
        }

        DegradeType degradeType = degradeProperty.getDegradeType();
        switch (degradeType) {
            case SENTINEL: {
                try {
                    Class.forName("com.alibaba.csp.sentinel.SphU");
                    List<com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule> rules = new ArrayList<>();

                    for (DegradeRule degradeRule : LIST) {
                        DegradeStrategy degradeStrategy = degradeRule.getDegradeStrategy();
                        int grade;
                        switch (degradeStrategy) {
                            case AVERAGE_RT: {
                                grade = 0;
                                break;
                            }
                            case EXCEPTION_RATIO: {
                                grade = 1;
                                break;
                            }
                            default: {
                                throw new IllegalArgumentException("Not currently supported! degradeStrategy=" + degradeStrategy);
                            }
                        }
                        String resourceName = degradeRule.getResourceName();
                        // add degrade rule
                        com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule rule = new com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule()
                                .setGrade(grade)
                                // Max allowed response time
                                .setCount(degradeRule.getCount())
                                // Retry timeout (in second)
                                .setTimeWindow(degradeRule.getTimeWindow());
                        rule.setResource(resourceName);
                        rules.add(rule);
                    }
                    DegradeRuleManager.loadRules(rules);

                } catch (Exception e) {
                    log.warn("com.alibaba.csp.sentinel not found! No SentinelDegradeInterceptor is set.");
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Not currently supported! degradeType=" + degradeType);
            }

        }
    }


}
