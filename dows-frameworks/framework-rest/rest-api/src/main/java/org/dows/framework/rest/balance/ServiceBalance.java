package org.dows.framework.rest.balance;

import java.net.URI;


@FunctionalInterface
public interface ServiceBalance {


    /**
     * Chooses a ServiceInstance URI from the LoadBalancer for the specified service.
     *
     * @param serviceId The service ID to look up the LoadBalancer.
     * @return Return the uri of ServiceInstance
     */
    URI choose(String serviceId);

}
