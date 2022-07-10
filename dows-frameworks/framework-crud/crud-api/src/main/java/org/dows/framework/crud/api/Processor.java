package org.dows.framework.crud.api;

import java.util.List;

public interface Processor<T> {

    /**
     * 处理逻辑
     *
     * @param list 数据
     */
    void process(List<T> list);

}
