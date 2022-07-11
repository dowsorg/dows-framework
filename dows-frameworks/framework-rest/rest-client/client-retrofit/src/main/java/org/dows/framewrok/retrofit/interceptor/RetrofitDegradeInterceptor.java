package org.dows.framewrok.retrofit.interceptor;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.ResourceTypeConstants;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import org.dows.framework.rest.exception.RetrofitBlockException;
import org.dows.framework.rest.interceptor.InterceptTyp;
import org.dows.framework.rest.parser.ResourceNameParser;
import org.dows.framework.rest.property.RestProperties;
import org.dows.framewrok.retrofit.RetrofitInterceptor;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
public class RetrofitDegradeInterceptor implements RetrofitInterceptor {

    private final Environment environment;
    private final ResourceNameParser resourceNameParser;
    private final RestProperties restProperties;

    @PostConstruct
    public void init() {
        initBalance(restProperties.getDegrade(), log);
    }

    @Override
    public InterceptTyp intetceptTyp() {
        return InterceptTyp.DEGRADE;
    }

    @Override
    public Response doIntercept(Chain chain) throws Exception {
        Request request = chain.request();
        String resName = getResourceName(request, resourceNameParser, environment);
        Entry entry = null;
        try {
            entry = SphU.entry(resName, ResourceTypeConstants.COMMON_WEB, EntryType.OUT);
            return chain.proceed(request);
        } catch (BlockException e) {
            throw new RetrofitBlockException(e);
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
    }


}
