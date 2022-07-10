package org.dows.framework.crud.api;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class PrintOutProcessor implements Processor<String> {
    @Override
    public void process(List<String> list) {
        log.info("开始发送，发送数量:" + list.size());

        for (String s : list) {
            log.info(s);
        }

        log.info("发送结束");
    }
}
