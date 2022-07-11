package org.dows.framework.rest.degrade;

public interface FallbackFactory<T> {


    /**
     * Returns an instance of the fallback appropriate for the given cause
     *
     * @param cause fallback cause
     * @return 实现了retrofit接口的实例。an instance that implements the retrofit interface.
     */
    T create(Throwable cause);
}
