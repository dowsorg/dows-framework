package org.dows.framework.rest.degrade;

import lombok.Data;

@Data
public class DegradeRule {

    private String resourceName;

    private double count;

    private int timeWindow;

    private DegradeStrategy degradeStrategy;
}
