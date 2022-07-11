package org.dows.framework.rest.balance;

import org.dows.framework.api.exceptions.RestException;
import org.dows.framework.api.status.RestStatusCode;

import java.net.URI;

public class NoValidServiceInstanceBalance implements ServiceBalance {


    /**
     * Chooses a ServiceInstance URI from the LoadBalancer for the specified service.
     *
     * @param serviceId The service ID to look up the LoadBalancer.
     * @return Return the uri of ServiceInstance
     */
    @Override
    public URI choose(String serviceId) {
        throw new RestException(RestStatusCode.ServiceInstanceChooseException);
    }
}
