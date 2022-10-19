package org.dows.sequence.boot;

import lombok.extern.slf4j.Slf4j;
import org.dows.sequence.api.SequenceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sound.midi.Sequence;


/**
 * @author lait.zhang@gmail.com
 * @description: 自动配置类
 * @weixin SH330786
 * @date 1/17/2022
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = SequenceConfig.class)
@ConditionalOnClass(Sequence.class)
@ConditionalOnProperty(prefix = SequenceConfig.PREFIX, value = SequenceConfig.ENABLED, matchIfMissing = true)
public class SequenceAutoConfiguration {
    @Autowired
    private SequenceConfig sequenceConfig;

    @Bean
    @ConditionalOnMissingBean(Sequence.class)
    public Sequence createSequence() {
//        log.info("初始化Sequence生成器,实现类名称:{}", sequenceConfig.getImpl());
//        SequenceHolder.sequence = ExtensionLoader.getExtensionLoader(IdSequence.class)
//                .getExtension(sequenceConfig.getImpl());
//        log.info("初始化Sequence生成器成功,实现类:{}", SequenceHolder.sequence);
        return SequenceHolder.sequence;
    }
}
