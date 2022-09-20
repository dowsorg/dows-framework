package org.dows.framework.rest;

import lombok.SneakyThrows;
import org.dows.framework.rest.annotation.RestClient;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class ClassPathRestClientScanner extends ClassPathBeanDefinitionScanner {

    private final ClassLoader classLoader;

    public ClassPathRestClientScanner(BeanDefinitionRegistry registry, ClassLoader classLoader) {
        super(registry, false);
        this.classLoader = classLoader;
    }

    public void registerFilters() {
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(RestClient.class);
        this.addIncludeFilter(annotationTypeFilter);
    }


    @SneakyThrows
    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            logger.warn("No RestClient was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
        } else {
            processBeanDefinitions(beanDefinitions);
        }
        return beanDefinitions;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        if (beanDefinition.getMetadata().isInterface()) {
            try {
                Class<?> target = ClassUtils.forName(beanDefinition.getMetadata().getClassName(), classLoader);
                return !target.isAnnotation();
            } catch (Exception ex) {
                logger.error("load class exception:", ex);
            }
        }
        return false;
    }


    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) throws ClassNotFoundException {
        GenericBeanDefinition definition;
        for (BeanDefinitionHolder holder : beanDefinitions) {
            definition = (GenericBeanDefinition) holder.getBeanDefinition();
            if (logger.isDebugEnabled()) {
                logger.debug("Creating RestClientBean with name '" + holder.getBeanName()
                        + "' and '" + definition.getBeanClassName() + "' Interface");
            }
            definition.getConstructorArgumentValues().addGenericArgumentValue(Objects.requireNonNull(definition.getBeanClassName()));
            Class<?> resolveBeanClass = definition.resolveBeanClass(classLoader);
            assert resolveBeanClass != null;
            RestClientTyp restClientTyp = resolveBeanClass.getAnnotation(RestClient.class).value();
            // beanClass全部设置为FactoryBean 交由工厂bean 完成各自的创建
            Class<?> clz = null;
            if (restClientTyp == RestClientTyp.RETROFIT) {
                clz = Class.forName("org.dows.framework.rest.RetrofitFactoryBean");
            } else if (restClientTyp == RestClientTyp.FEIGN) {
                clz = Class.forName("org.dows.framewrok.feign.FeignFactoryBean");
            } else if (restClientTyp == RestClientTyp.HTTPCLIENT) {
                clz = Class.forName("org.dows.framewrok.httpclient.HttpClientFactoryBean");
            }
            if (clz == null) {
                //throw new RestException("必须指定实现的客户端");
            }
            definition.setBeanClass(clz);
        }
    }
}
