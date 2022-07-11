package org.dows.framework.rest.balance;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.util.Assert;

import java.net.URI;

public class SpringCloudServiceInstanceBalance implements ServiceBalance {

    private final LoadBalancerClient loadBalancerClient;

    public SpringCloudServiceInstanceBalance(LoadBalancerClient loadBalancerClient) {
        this.loadBalancerClient = loadBalancerClient;
    }


    /**
     * @param serviceId The service ID to look up the LoadBalancer.
     * @return
     */
    @Override
    public URI choose(String serviceId) {
        ServiceInstance serviceInstance = loadBalancerClient.choose(serviceId);
        Assert.notNull(serviceInstance, "can not found service instance! serviceId=" + serviceId);
        return serviceInstance.getUri();
    }
}
