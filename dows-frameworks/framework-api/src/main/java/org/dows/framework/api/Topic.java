package org.dows.framework.api;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 1/18/2022
 */
public interface Topic {
    String getTopic();

    String getDescr();

    enum DefaultTopic implements Topic {
        ;

        @Override
        public String getTopic() {
            return null;
        }

        @Override
        public String getDescr() {
            return null;
        }
    }
}
