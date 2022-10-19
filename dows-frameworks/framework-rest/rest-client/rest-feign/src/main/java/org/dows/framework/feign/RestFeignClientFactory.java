package org.dows.framework.feign;

import org.dows.framework.rest.config.RestSetting;
import org.dows.framework.rest.config.TargetConfig;
import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.httpclient.ApacheHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 远程服务客户端工厂默认实现，基于feign实现
 */
@Slf4j
public class RestFeignClientFactory extends AbstractRestClientFactory {
    /**
     * apacheHttpClient
     */
    private ApacheHttpClient apacheHttpClient;
    /**
     * FastJson编码器
     */
    private Encoder encoder;
    /**
     * FastJson解码器
     */
    private Decoder decoder;

    public RestFeignClientFactory(RestSetting restSetting) {
        super(restSetting);
        apacheHttpClient = new ApacheHttpClient(createHttpClient());
        encoder = new JacksonEncoder();
        decoder = new JacksonDecoder();
        init();
    }

    /**
     * 绕过验证
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                                           String paramString) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                                           String paramString) {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    /**
     * 执行构建
     *
     * @param clazz
     * @param targetConfig
     * @param <T>
     * @return
     */
    @Override
    protected <T> T doBuildClient(Class<T> clazz, TargetConfig targetConfig) {
        Retryer retryer = targetConfig.getRetryCount() == 0 ? Retryer.NEVER_RETRY :
                new Retryer.Default(targetConfig.getPeriod(), targetConfig.getPeriod(), targetConfig.getRetryCount());
        Request.Options options = new Request.Options(targetConfig.getConnectionTimeout(),
                TimeUnit.MILLISECONDS, targetConfig.getSocketTimeout(), TimeUnit.MILLISECONDS, true);
        return Feign.builder()
                .client(apacheHttpClient)
                .options(options)
                .retryer(retryer)
                .encoder(encoder)
                .decoder(decoder)
                .target(clazz, targetConfig.getUrl());
    }

    /**
     * 创建HttpClient
     *
     * @return
     */
    private CloseableHttpClient createHttpClient() {
        CloseableHttpClient client;
        // 设置协议http和https对应的处理socket链接工厂的对象
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", getSSLConnectionSocketFactory())
                .build();
        // 初始化线程池
        PoolingHttpClientConnectionManager pccm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        pccm.setMaxTotal(restSetting.getMaxTotal());
        // 单路由最大并发数
        pccm.setDefaultMaxPerRoute(restSetting.getDefaultMaxPerRoute());
        //配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom().setExpectContinueEnabled(true)
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                .setSocketTimeout(restSetting.getSocketTimeout())
                .setConnectTimeout(restSetting.getConnectionTimeout())
                .setConnectionRequestTimeout(restSetting.getConnectionRequestTimeout())
                .build();
        //构建
        client = HttpClients.custom()
                .setConnectionManager(pccm)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(new DefaultHttpRequestRetryHandler())
                .evictExpiredConnections()//开启清理
                .build();// 默认失败后重发3次
        return client;
    }

    /**
     * 获取SSLConnectionSocketFactory
     *
     * @return
     */
    private SSLConnectionSocketFactory getSSLConnectionSocketFactory() {
        try {
            if (restSetting.isSsl()) {
                return new SSLConnectionSocketFactory(createVerifySSL());
            } else {
                return new SSLConnectionSocketFactory(createIgnoreVerifySSL());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 本地信任证书SSLContext
     *
     * @return
     * @throws Exception
     */
    private SSLContext createVerifySSL() throws Exception {
        try (InputStream in = new FileInputStream(restSetting.getJksPath())) {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(in, restSetting.getJksPwd().toCharArray());
            // Trust own CA and all self-signed certs
            return SSLContexts.custom()
                    .loadKeyMaterial(keyStore, restSetting.getJksPwd().toCharArray())
                    .build();
        }
    }

}
