package org.dows.sequence.boot;

import javax.sound.midi.Sequence;
import java.util.Optional;


/**
 * @author lait.zhang@gmail.com
 * @description: 唯一编码生成接口Sequence接口对象持有者
 * @weixin SH330786
 * @date 1/17/2022
 */
public class SequenceHolder {
    /**
     * Sequence对象
     */
    static Sequence sequence;

    public static Optional<Sequence> getSequence() {
        return Optional.ofNullable(sequence);
    }
}
