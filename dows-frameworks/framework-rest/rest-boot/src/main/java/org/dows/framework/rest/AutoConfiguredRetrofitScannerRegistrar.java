package org.dows.framework.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;

@Slf4j
public class AutoConfiguredRetrofitScannerRegistrar implements BeanFactoryAware, ImportBeanDefinitionRegistrar,
        ResourceLoaderAware, BeanClassLoaderAware {

    private BeanFactory beanFactory;

    private ResourceLoader resourceLoader;

    private ClassLoader classLoader;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!AutoConfigurationPackages.has(this.beanFactory)) {
            log.debug("Could not determine auto-configuration package, automatic retrofit scanning disabled.");
            return;
        }

        log.debug("Searching for retrofits annotated with @RestClient");

        List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
        if (log.isDebugEnabled()) {
            packages.forEach(pkg -> log.debug("Using auto-configuration base package '{}'", pkg));
        }
        //扫描指定路径下的@RsetClient注释的接口，并将其注册到BeanDefinitionRegistry
        ClassPathRestClientScanner scanner = new ClassPathRestClientScanner(registry, classLoader);
        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }
        String[] packageArr = packages.toArray(new String[0]);
        scanner.registerFilters();
        // Scan and register to BeanDefinition
        scanner.doScan(packageArr);
    }


    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
