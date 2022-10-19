package org.dows.sequence.core;

import org.dows.sequence.api.SequenceConfig;
import org.dows.sequence.api.WorkerIdHandler;
import org.dows.sequence.api.WorkerLoader;

import java.util.Collection;

public final class WorkerIdHandlerFactory {

    static {
        ServiceLoaderFactory.init(WorkerLoader.class);
    }

    public static WorkerIdHandler getWorkerIdHandler(String namespace, SequenceConfig butterflyConfig) {
        Collection<WorkerLoader> workerLoaderCollection = ServiceLoaderFactory.getChildObject(WorkerLoader.class);
        for (WorkerLoader allocator : workerLoaderCollection) {
            if (allocator.acceptConfig(butterflyConfig)) {
                return allocator.loadIdHandler(namespace, butterflyConfig);
            }
        }
//        throw new ButterflyException("not find workerId allocator, please add butterfly-worker-allocator-db or butterfly-worker-allocator-distribute");
        return null;
    }
}
