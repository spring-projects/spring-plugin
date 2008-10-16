/*
 * Copyright 2002-2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.synyx.hera.core.support;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.synyx.hera.core.PluginRegistry;


/**
 * Simple {@link BeanFactoryPostProcessor} to autocreate lists of the configured
 * {@code lists} property. The post processor will register
 * {@link ListFactoryBean}s for each list named with the given key containing
 * all beans of the {@link org.springframework.context.ApplicationContext}
 * implementing the interface defines as lists value. E.g.:
 * 
 * <pre>
 * &lt;bean class=&quot;org.synyx.plugin.core.support.BeanListFactoryPostProcessor&quot;&gt;
 *    &lt;property name=&quot;lists&quot;&gt;
 *       &lt;map&gt;
 *          &lt;entry key=&quot;beanName&quot; value=&quot;org.synyx.plugin.core.Plugin&quot; /&gt;
 *       &lt;/map&gt;
 *    &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 * 
 * This would register all Spring beans implementing the
 * {@link org.synyx.hera.core.Plugin} interface in a list bean with the name
 * {@coder beanName}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class BeanListBeanFactoryPostProcessor implements
        BeanFactoryPostProcessor {

    private static final Log log =
            LogFactory.getLog(BeanListBeanFactoryPostProcessor.class);

    private Map<String, Class<?>> lists = new HashMap<String, Class<?>>();

    private boolean initFactories = false;


    /**
     * Setter to inject required plugin registry configuration. The map's keys
     * will be used as bean ids for the resulting {@link PluginRegistry}
     * instances. These registry instances will contain all beans having the
     * given type.
     * 
     * @param registryMap
     */
    public void setLists(Map<String, Class<?>> lists) {

        this.lists = lists;
    }


    /**
     * Setter to activate {@link org.springframework.beans.factory.FactoryBean}
     * scanning. This allows auto-registration of factory bean targets (the
     * objects actually created by the factory bean} but comes with the drawback
     * of having to initialize those factories eagerly. This can cause
     * unpredictable behaviour if other {@link BeanFactoryPostProcessor}s would
     * have been applied to these factories. Defaults to {@value #initFactories}
     * .
     * 
     * @param initFactories the allowEagerInit to set
     */
    public void setInitFactories(boolean initFactories) {

        this.initFactories = initFactories;
    }


    /*
     * (non-Javadoc)
     * 
     * @seeorg.springframework.beans.factory.config.BeanFactoryPostProcessor#
     * postProcessBeanFactory
     * (org.springframework.beans.factory.config.ConfigurableListableBeanFactory
     * )
     */
    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory beanFactory) throws BeansException {

        if (!(beanFactory instanceof BeanDefinitionRegistry)) {
            throw new IllegalArgumentException(
                    "Given beanFactory is not a BeanDefinitionRegistry!");
        }

        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

        // Do for each required wrapper
        for (String wrapperName : lists.keySet()) {

            Class<?> requiredType = lists.get(wrapperName);

            // Find implementations of the component type
            List<RuntimeBeanReference> beans =
                    getBeanReferencesOfType(beanFactory, requiredType);

            registerListBeanDefinition(wrapperName, requiredType, beans,
                    registry);
        }
    }


    /**
     * Registers a {@link ListFactoryBean} containing the given beans under the
     * given id. Allows subclasses to modify or wrap the actually registered
     * bean definition by calling
     * {@link #wrapListBeanDefinition(BeanDefinition)} with the bean definition
     * of the {Ïlinl {@link ListFactoryBean}.
     * 
     * @param id
     * @param beans
     * @param registry
     */
    protected <T> void registerListBeanDefinition(String id, Class<T> type,
            List<RuntimeBeanReference> beans, BeanDefinitionRegistry registry) {

        // Create ListFactory bean definition
        BeanDefinitionBuilder listDefinitionBuilder =
                BeanDefinitionBuilder.rootBeanDefinition(ListFactoryBean.class);

        // Set the implementations as source for the wrapper
        listDefinitionBuilder.addPropertyValue("sourceList", beans);

        BeanDefinition beanDefinition =
                wrapListBeanDefinition(getSourcedBeanDefinition(listDefinitionBuilder));

        if (log.isDebugEnabled()) {
            log.debug(String.format(
                    "Registering bean '%s' of type %s containing %s!", id,
                    beanDefinition.getBeanClassName(), beans.toString()));
        }

        // Register wrapper under defined name
        registry.registerBeanDefinition(id, beanDefinition);
    }


    /**
     * Template method to allow subclasses to wrap the list bean definition into
     * another bean definition that will actually be registered. The default
     * implementation simply returns the given bean definition.
     * 
     * @param beanDefinition
     * @return
     */
    protected BeanDefinition wrapListBeanDefinition(
            BeanDefinition beanDefinition) {

        return beanDefinition;
    }


    /**
     * Returns a {@link BeanDefinitionBuilder}s {@link BeanDefinition} setting
     * the current {@link PluginRegistryBeanFactoryPostProcessor} as source.
     * 
     * @param builder
     * @return
     */
    protected BeanDefinition getSourcedBeanDefinition(
            BeanDefinitionBuilder builder) {

        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        beanDefinition.setSource(this);

        return beanDefinition;
    }


    /**
     * Returns bean references to beans with the given type.
     * 
     * @param beanFactory
     * @param type
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List<RuntimeBeanReference> getBeanReferencesOfType(
            ConfigurableListableBeanFactory beanFactory, Class<?> type) {

        // Lookup bean candidates either via Spring or manually
        List<String> beanNames =
                Arrays.asList(beanFactory.getBeanNamesForType(type, true,
                        initFactories));

        List<RuntimeBeanReference> beanReferences =
                new ManagedList(beanNames.size());

        for (String beanName : beanNames) {

            RuntimeBeanReference reference = new RuntimeBeanReference(beanName);
            reference.setSource(this);

            beanReferences.add(reference);
        }

        return (List<RuntimeBeanReference>) beanReferences;
    }
}
