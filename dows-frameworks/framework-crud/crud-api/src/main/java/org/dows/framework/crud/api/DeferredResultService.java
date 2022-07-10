package org.dows.framework.crud.api;

import org.dows.framework.api.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
public class DeferredResultService {

    private Map<String, Consumer<Response>> taskMap;

    public DeferredResultService() {
        taskMap = new ConcurrentHashMap<>();
    }

    /**
     * 将请求id与setResult映射
     *
     * @param requestId
     * @param deferredResult
     */
    public void process(String requestId, DeferredResult<Response> deferredResult) {
        // 请求超时的回调函数
        deferredResult.onTimeout(() -> {
            taskMap.remove(requestId);
            Response Response = new Response();
            Response.setCode(HttpStatus.REQUEST_TIMEOUT.value());
            deferredResult.setResult(Response);
        });

        Optional.ofNullable(taskMap)
                .filter(t -> !t.containsKey(requestId))
                .orElseThrow(() -> new IllegalArgumentException(String.format("requestId=%s is existing", requestId)));

        // 这里的Consumer，就相当于是传入的DeferredResult对象的地址
        // 所以下面settingResult方法中"taskMap.get(requestId)"就是Controller层创建的对象
        taskMap.putIfAbsent(requestId, deferredResult::setResult);
    }

    /**
     * 这里相当于异步的操作方法
     * 设置DeferredResult对象的setResult方法
     *
     * @param requestId
     * @param Response
     */
    public void settingResult(String requestId, Response Response) {
        if (taskMap.containsKey(requestId)) {
            Consumer<Response> ResponseConsumer = taskMap.get(requestId);
            // 这里相当于DeferredResult对象的setResult方法
            ResponseConsumer.accept(Response);
            taskMap.remove(requestId);
        }
    }
}
