package org.dows.framework;

import lombok.RequiredArgsConstructor;
import org.dows.framework.oss.local.LocalOssClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocalOssBiz {

    private final LocalOssClient localOssClient;


    public  void uoload(){
//        localOssClient.upLoad()
    }
}
