package org.dows.framework.rest;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.dows.framework.rest.annotation.RestClient;
import org.dows.framework.rest.degrade.DegradeRuleInitializer;
import org.dows.framework.rest.degrade.FallbackFactory;
import org.dows.framework.rest.factory.CallAdapterFactory;
import org.dows.framework.rest.factory.ConverterFactory;
import org.dows.framework.rest.interceptor.RestInterceptor;
import org.dows.framework.rest.log.LogLevel;
import org.dows.framework.rest.log.LogStrategy;
import org.dows.framework.rest.parser.ResourceNameParser;
import org.dows.framework.rest.property.*;
import org.dows.framework.rest.util.BeanExtendUtils;
import org.dows.framework.rest.util.RestClientUtil;
import org.dows.framewrok.retrofit.RetrofitUtils;
import org.dows.framewrok.retrofit.core.RetrofitResourceNameParser;
import org.dows.framewrok.retrofit.interceptor.*;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * oktttp的工厂bean ,创建RestClient
 *
 * @param <T>
 */
@Slf4j
public class RetrofitFactoryBean<T> implements FactoryBean<T>, EnvironmentAware, ApplicationContextAware {

    private static final Map<Class<? extends CallAdapterFactory>, CallAdapter.Factory> CALL_ADAPTER_FACTORIES_CACHE = new HashMap<>(4);
    private static final Map<Class<? extends ConverterFactory>, Converter.Factory> CONVERTER_FACTORIES_CACHE = new HashMap<>(4);

    private final Map<String, RestConnectionPool> poolRegistry = new ConcurrentHashMap<>(4);
    private final Map<String, RestInterceptor> restInterceptors = new ConcurrentHashMap<>();

    private final Class<T> retrofitInterface;
    private final RestClient restClient;

    private RestProperties restProperties;
    private ResourceNameParser resourceNameParser;

    private Environment environment;
    private ApplicationContext applicationContext;

    public RetrofitFactoryBean(Class<T> retrofitInterface) {
        this.retrofitInterface = retrofitInterface;
        restClient = retrofitInterface.getAnnotation(RestClient.class);
    }

    private static <T> T getTargetBean(T bean) {
        Object object = bean;
        if (AopUtils.isAopProxy(object)) {
            try {
                object = ((Advised) object).getTargetSource().getTarget();
            } catch (Exception e) {
                throw new RuntimeException("get target bean failed", e);
            }
        }
        return (T) object;
    }

    @Override
    public Class<T> getObjectType() {
        return this.retrofitInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.restProperties = applicationContext.getBean(RestProperties.class);
        this.resourceNameParser = applicationContext.getBean(RetrofitResourceNameParser.class);
        applicationContext.getBeansOfType(RestInterceptor.class).forEach((key, val) -> {
            restInterceptors.put(val.intetceptTyp().prefix() + key, val);
        });
        Map<String, RestConnectionPool> pool = restProperties.getPool();
        if (pool != null) {
            poolRegistry.putAll(pool);
        }
    }

    /**
     * 获取clinet
     *
     * @return
     * @throws Exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getObject() throws Exception {
        // 检测RestClient注解的接口
        RetrofitUtils.checkRetrofitInterface(retrofitInterface, restClient, restProperties);
        Retrofit retrofit = getRetrofit(retrofitInterface);
        // source
        T source = retrofit.create(retrofitInterface);
        Class<?> fallbackClass = restClient.fallback();
        Object fallback = null;
        if (!void.class.isAssignableFrom(fallbackClass)) {
            try {
                fallback = applicationContext.getBean(fallbackClass);
            } catch (Exception e) {
                log.info("fallback class is not found ,because {}", e.getMessage());
            }
        }
        Class<?> fallbackFactoryClass = restClient.fallbackFactory();
        FallbackFactory<?> fallbackFactory = null;
        if (!void.class.isAssignableFrom(fallbackFactoryClass)) {
            try {
                fallbackFactory = (FallbackFactory) applicationContext.getBean(fallbackFactoryClass);
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        }
        // 加载降级规则
        DegradeRuleInitializer degradeRuleInitializer = applicationContext.getBean(DegradeRuleInitializer.class);
        RetrofitUtils.loadDegradeRules(retrofitInterface, resourceNameParser, environment, degradeRuleInitializer);
        // 创建代理对象并返回
        return (T) Proxy.newProxyInstance(retrofitInterface.getClassLoader(),
                new Class<?>[]{retrofitInterface},
                new RestInvocationHandler(source, fallback, fallbackFactory, restProperties.getDegrade()));
    }

    /**
     * 获取Retrofit实例，一个restClient接口对应一个Retrofit实例
     *
     * @param retrofitClientInterfaceClass
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    private synchronized Retrofit getRetrofit(Class<?> retrofitClientInterfaceClass) throws InstantiationException,
            IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        RestClient restClient = retrofitClientInterfaceClass.getAnnotation(RestClient.class);
        String baseUrl = RestClientUtil.convertBaseUrl(restClient, restClient.baseUrl(), environment);

        OkHttpClient client = getOkHttpClient(retrofitClientInterfaceClass);
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .validateEagerly(restClient.validateEagerly())
                .client(client);

        // 添加CallAdapter.Factory
        Class<? extends CallAdapterFactory>[] callAdapterFactoryClasses = restClient.callAdapterFactories();
        List<Class<? extends CallAdapterFactory>> globalCallAdapterFactoryClasses = restProperties.getGlobalCallAdapterFactories();
        // 可以设置默认
        List<CallAdapter.Factory> callAdapterFactories = getCallAdapterFactories(callAdapterFactoryClasses, globalCallAdapterFactoryClasses);
        if (!CollectionUtils.isEmpty(callAdapterFactories)) {
            callAdapterFactories.forEach(retrofitBuilder::addCallAdapterFactory);
        }
        // 添加Converter.Factory
        Class<? extends ConverterFactory>[] converterFactoryClasses = restClient.converterFactories();
        List<Class<? extends ConverterFactory>> globalConverterFactoryClasses = restProperties.getGlobalConverterFactories();

        List<Converter.Factory> converterFactories = getConverterFactories(converterFactoryClasses, globalConverterFactoryClasses);
        if (!CollectionUtils.isEmpty(converterFactories)) {
            converterFactories.forEach(retrofitBuilder::addConverterFactory);
        }
        return retrofitBuilder.build();
    }

    /**
     * 获取okhttp 客户端
     *
     * @param retrofitClientInterfaceClass
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    private synchronized OkHttpClient getOkHttpClient(Class<?> retrofitClientInterfaceClass)
            throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        RestClient restClient = retrofitClientInterfaceClass.getAnnotation(RestClient.class);
        Method method = RetrofitUtils.findOkHttpClientBuilderMethod(retrofitClientInterfaceClass);
        OkHttpClient.Builder okHttpClientBuilder;
        if (method != null) {
            okHttpClientBuilder = (OkHttpClient.Builder) method.invoke(null);
        } else {
            okHttpClientBuilder = RetrofitUtils.createPoorOkHttpClientBuilder(restClient, restProperties,
                    getConnectionPool(retrofitClientInterfaceClass));
        }
        // 获取全局拦截器keys
        Set<String> keySet = restInterceptors.keySet();
        // okhttp拦截器
        List<Interceptor> interceptors = new ArrayList<>();
        // 如果启用日志，设置默认或自定义拦截器
        LogProperty logProperty = restProperties.getLog();
        if (logProperty.isEnable() && restClient.enableLog()) {
            LogLevel logLevel = restClient.logLevel();
            LogStrategy logStrategy = restClient.logStrategy();
            if (logLevel.equals(LogLevel.NULL) && logStrategy.equals(LogStrategy.NULL)) {
                // 使用全局默认
                try {
                    interceptors.add((Interceptor) applicationContext.getBean(logProperty.getInterceptor()));
                } catch (Exception e) {
                    interceptors.add(applicationContext.getBean(RetrofitLoggingInterceptor.class));
                }
            } else {
                // 创建自定义
                Class<? extends RestInterceptor> loggingInterceptorClass = logProperty.getInterceptor();
                Constructor<? extends RestInterceptor> constructor = loggingInterceptorClass.getConstructor(RestProperties.class);
                interceptors.add((Interceptor) constructor.newInstance(logLevel, logStrategy));
            }
        }
        // 过滤
        FilterProperty filterProperty = restProperties.getFilter();
        if (filterProperty.isEnable()) {
            try {
                interceptors.add((Interceptor) applicationContext.getBean(filterProperty.getInterceptor()));
            } catch (Exception e) {
                interceptors.add(applicationContext.getBean(RetrofitFilterInterceptor.class));
            }
        }

        // 安全&鉴权&验签
        /*SecurityProperty securityProperty = restProperties.getSecurity();
        if (securityProperty.isEnable()) {
            try {
                interceptors.add((Interceptor) applicationContext.getBean(securityProperty.getInterceptor()));
            } catch (Exception e) {
                interceptors.add(applicationContext.getBean(RetrofitSecurityInterceptor.class));
            }
        }*/

        // 如果有serviceId,增加balace拦截器
        BalanceProperty balanceProperty = restProperties.getBalance();
        if (balanceProperty.isEnable() && StringUtils.hasText(restClient.serviceId())) {
            try {
                interceptors.add((Interceptor) applicationContext.getBean(balanceProperty.getInterceptor()));
            } catch (Exception e) {
                interceptors.add(applicationContext.getBean(RetrofitBalanceInterceptor.class));
            }
        }
        // 重试
        RetryProperty retryProperty = restProperties.getRetry();
        if (retryProperty.isEnable()) {
            try {
                interceptors.add((Interceptor) applicationContext.getBean(retryProperty.getInterceptor()));
            } catch (Exception e) {
                interceptors.add(applicationContext.getBean(RetrofitRetryInterceptor.class));
            }
        }
        // 降级
        DegradeProperty degradeProperty = restProperties.getDegrade();
        if (degradeProperty.isEnable()) {
            try {
                interceptors.add((Interceptor) applicationContext.getBean(degradeProperty.getInterceptor()));
            } catch (Exception e) {
                interceptors.add(applicationContext.getBean(RetrofitDegradeInterceptor.class));
            }
        }
        // 自定义扩展拦截器
        interceptors.addAll(findInterceptorByAnnotation(retrofitClientInterfaceClass));
        interceptors.forEach(okHttpClientBuilder::addInterceptor);
        return okHttpClientBuilder.build();
    }

    /**
     * 获取拦截器集合
     *
     * @param keyPrefix
     * @param keySet
     * @return
     */
    private List<RestInterceptor> getInterceptors(String keyPrefix, Set<String> keySet) {
        List<RestInterceptor> restInterceptorsList = new ArrayList<>();
        for (String key : keySet) {
            if (key.startsWith(keyPrefix)) {
                restInterceptorsList.add(restInterceptors.get(key));
            }
        }
        return restInterceptorsList;
    }

    /**
     * 获取okhttp3 connection pool
     *
     * @param retrofitClientInterfaceClass
     * @return
     */
    private synchronized RestConnectionPool getConnectionPool(Class<?> retrofitClientInterfaceClass) {
        RestClient retrofitClient = retrofitClientInterfaceClass.getAnnotation(RestClient.class);
        String poolName = retrofitClient.poolName();
        Map<String, RestConnectionPool> poolRegistry = restProperties.getPool();
        Assert.notNull(poolRegistry, "poolRegistry does not exist! Please set restConfigBean.poolRegistry!");
        RestConnectionPool connectionPool = poolRegistry.get(poolName);
        Assert.notNull(connectionPool, "The connection pool corresponding to the current poolName does not exist! poolName = " + poolName);
        return connectionPool;
    }

    /**
     * 获取restClient接口类上定义的拦截器集合
     *
     * @param restClientInterfaceClass
     * @return the interceptor list
     */
    @SuppressWarnings("unchecked")
    private List<Interceptor> findInterceptorByAnnotation(Class<?> restClientInterfaceClass)
            throws InstantiationException, IllegalAccessException {
        List<Interceptor> interceptors = new ArrayList<>();
        List<Annotation> interceptAnnotations = RetrofitUtils.getInterceptAnnotations(restClientInterfaceClass);
        for (Annotation interceptAnnotation : interceptAnnotations) {
            // 获取注解属性数据。Get annotation attribute data
            Map<String, Object> annotationAttributes = AnnotationUtils.getAnnotationAttributes(interceptAnnotation);
            Object handler = annotationAttributes.get("handler");
            Assert.notNull(handler, "@InterceptMark annotations must be configured: Class<? extends BasePathMatchInterceptor> handler()");
            Assert.notNull(annotationAttributes.get("include"), "@InterceptMark annotations must be configured: String[] include()");
            Assert.notNull(annotationAttributes.get("exclude"), "@InterceptMark annotations must be configured: String[] exclude()");
            Class<? extends RetrofitFilterInterceptor> interceptorClass = (Class<? extends RetrofitFilterInterceptor>) handler;
            RetrofitFilterInterceptor interceptor = getInterceptorInstance(interceptorClass);
            Map<String, Object> annotationResolveAttributes = new HashMap<>(8);
            // 占位符属性替换。Placeholder attribute replacement
            annotationAttributes.forEach((key, value) -> {
                if (value instanceof String) {
                    String newValue = environment.resolvePlaceholders((String) value);
                    annotationResolveAttributes.put(key, newValue);
                } else {
                    annotationResolveAttributes.put(key, value);
                }
            });
            RetrofitFilterInterceptor targetInterceptor = getTargetBean(interceptor);
            // 动态设置属性值。Set property value dynamically
            BeanExtendUtils.populate(targetInterceptor, annotationResolveAttributes);
            interceptors.add(interceptor);
        }
        return interceptors;
    }

    /**
     * 获取路径拦截器实例，优先从spring容器中取。如果spring容器中不存在，则无参构造器实例化一个。
     *
     * @param interceptorClass
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private RetrofitFilterInterceptor getInterceptorInstance(Class<? extends RetrofitFilterInterceptor> interceptorClass)
            throws IllegalAccessException, InstantiationException {
        // spring bean
        try {
            return getTargetBean(applicationContext.getBean(interceptorClass));
        } catch (BeansException e) {
            // spring容器获取失败，反射创建
            return interceptorClass.newInstance();
        }
    }

    private List<CallAdapter.Factory> getCallAdapterFactories(Class<? extends CallAdapterFactory>[] callAdapterFactoryClasses,
                                                              List<Class<? extends CallAdapterFactory>> globalCallAdapterFactoryClasses)
            throws IllegalAccessException, InstantiationException {
        List<Class<? extends CallAdapterFactory>> combineCallAdapterFactoryClasses = new ArrayList<>();
        if (callAdapterFactoryClasses != null && callAdapterFactoryClasses.length != 0) {
            combineCallAdapterFactoryClasses.addAll(Arrays.asList(callAdapterFactoryClasses));
        }
        if (globalCallAdapterFactoryClasses != null && globalCallAdapterFactoryClasses.size() != 0) {
            combineCallAdapterFactoryClasses.addAll(globalCallAdapterFactoryClasses);
        }
        if (combineCallAdapterFactoryClasses.isEmpty()) {
            return Collections.emptyList();
        }

        List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>();
        for (Class<? extends CallAdapterFactory> callAdapterFactoryClass : combineCallAdapterFactoryClasses) {
            CallAdapter.Factory callAdapterFactory = CALL_ADAPTER_FACTORIES_CACHE.get(callAdapterFactoryClass);
            if (callAdapterFactory == null) {
                try {
                    callAdapterFactory = (CallAdapter.Factory) applicationContext.getBean(callAdapterFactoryClass);
                } catch (Exception e) {
                    log.info(e.getMessage());
                }
                if (callAdapterFactory == null) {
                    callAdapterFactory = (CallAdapter.Factory) callAdapterFactoryClass.newInstance();
                }
                CALL_ADAPTER_FACTORIES_CACHE.put(callAdapterFactoryClass, callAdapterFactory);
            }
            callAdapterFactories.add(callAdapterFactory);
        }
        return callAdapterFactories;
    }

    private List<Converter.Factory> getConverterFactories(Class<? extends ConverterFactory>[] converterFactoryClasses,
                                                          List<Class<? extends ConverterFactory>> globalConverterFactoryClasses)
            throws IllegalAccessException, InstantiationException {
        List<Class<? extends ConverterFactory>> combineConverterFactoryClasses = new ArrayList<>();
        if (converterFactoryClasses != null && converterFactoryClasses.length != 0) {
            combineConverterFactoryClasses.addAll(Arrays.asList(converterFactoryClasses));
        }
        if (globalConverterFactoryClasses != null && globalConverterFactoryClasses.size() != 0) {
            combineConverterFactoryClasses.addAll(globalConverterFactoryClasses);
        }
        if (combineConverterFactoryClasses.isEmpty()) {
            return Collections.emptyList();
        }

        List<Converter.Factory> converterFactories = new ArrayList<>();
        for (Class<? extends ConverterFactory> converterFactoryClass : combineConverterFactoryClasses) {
            Converter.Factory converterFactory = CONVERTER_FACTORIES_CACHE.get(converterFactoryClass);
            if (converterFactory == null) {
                try {
                    converterFactory = (Converter.Factory) applicationContext.getBean(converterFactoryClass);
                } catch (Exception e) {
                    log.info(e.getMessage());
                }
                if (converterFactory == null) {
                    converterFactory = (Converter.Factory) converterFactoryClass.newInstance();
                }
                CONVERTER_FACTORIES_CACHE.put(converterFactoryClass, converterFactory);
            }
            converterFactories.add(converterFactory);
        }
        return converterFactories;
    }
}
