package org.dows.framework.crud.mybatis.batch;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Data;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.dows.framework.crud.api.utils.EntityUtil;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;

/**
 * 请求合并批次插叙
 *
 * @param <M>
 * @param <T>
 */
public class QueueServiceImpl<M extends BaseMapper<T>, T extends Model<T>> extends ServiceImpl<M, T> {
    private final ConcurrentLinkedQueue<FutureModel<T>> taskQueue = new ConcurrentLinkedQueue();

    public QueueServiceImpl() {
    }

    private static <T> List<T> extractElement(Queue<T> queue, int size) {
        Objects.requireNonNull(queue);
        List<T> lists = new ArrayList(size);

        for (int i = 0; i < size; ++i) {
            lists.add(queue.poll());
        }

        return lists;
    }

    @PostConstruct
    public void init() {
        RequstConfig config = this.createRequstConfig();
        Runnable runnable = this.getRunnable(config.getMaxRequestSize());
        BasicThreadFactory threadFactory = (new BasicThreadFactory.Builder()).namingPattern("scheduled-thread-pool-%d").daemon(true).build();
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(config.getCorePoolSize(), threadFactory);
        service.scheduleAtFixedRate(runnable, 0L, (long) config.getRequestInterval(), TimeUnit.MILLISECONDS);
    }

    protected RequstConfig createRequstConfig() {
        RequstConfig config = new RequstConfig();
        config.setMaxRequestSize(100);
        config.setCorePoolSize(1);
        config.setRequestInterval(200);
        return config;
    }

    private Runnable getRunnable(int maxRequestSize) {
        return () -> {
            int size = Math.min(this.taskQueue.size(), maxRequestSize);
            if (size != 0) {
                List<FutureModel<T>> requests = extractElement(this.taskQueue, size);
                Set<Serializable> ids = EntityUtil.toSet(requests, FutureModel::getId);
                List<T> list = super.listByIds(ids);
                Map<Serializable, T> map = EntityUtil.toMap(list, Model::pkVal, (e) -> {
                    return e;
                });
                requests.forEach((e) -> {
                    e.getFuture().complete(map.get(e.getId()));
                });
            }

        };
    }

    public T getById(Serializable id) {
        CompletableFuture<T> future = new CompletableFuture();
        this.taskQueue.add(new FutureModel(id, future));

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    @Data
    public static class RequstConfig {
        private Integer maxRequestSize;
        private Integer corePoolSize;
        private Integer requestInterval;
    }

    @Data
    private static class FutureModel<T> {
        private Serializable id;
        private CompletableFuture<T> future;

        public FutureModel(Serializable id, CompletableFuture<T> future) {
            this.id = id;
            this.future = future;
        }
    }
}
