package org.dows.framework.crud.api;

import org.dows.framework.api.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;


@RestController
@RequestMapping(value = "/deferred-result")
public class DeferredResultController {

    /**
     * 为了方便测试，简单模拟一个
     * 多个请求用同一个requestId会出问题
     */
    private final String requestId = "haha";
    @Autowired
    private DeferredResultService deferredResultService;

    @GetMapping(value = "/get")
    public DeferredResult<Response> get(@RequestParam(value = "timeout", required = false, defaultValue = "10000") Long timeout) {
        DeferredResult<Response> deferredResult = new DeferredResult<>(timeout);
        deferredResultService.process(requestId, deferredResult);
        return deferredResult;
    }

    /**
     * 设置DeferredResult对象的result属性，模拟异步操作
     *
     * @param desired
     * @return
     */
    @GetMapping(value = "/result")
    public String settingResult(@RequestParam(value = "desired", required = false, defaultValue = "成功") String desired) {
        Response response = null;
        if (Response.ok().equals(desired)) {
            response = Response.ok(HttpStatus.OK.value());
        } else {
            response = Response.fail(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        deferredResultService.settingResult(requestId, response);
        return "Done";
    }
}
