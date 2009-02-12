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

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.synyx.hera.core.OrderAwarePluginRegistry;


/**
 * This {@link BeanFactoryPostProcessor} automatically looksup bean instances
 * from the {@link BeanFactory} hierarchy and registers
 * {@link OrderAwarePluginRegistry} instances for them.
 * 
 * @see org.synyx.hera.core.support.BeanListBeanFactoryPostProcessor
 * @author Oliver Gierke - gierke@synyx.de
 */
public class PluginRegistryBeanFactoryPostProcessor extends
        BeanListBeanFactoryPostProcessor {

    /**
     * Additionally wraps the
     * {@link org.springframework.beans.factory.config.ListFactoryBean}
     * {@link BeanDefinition} into a {@link OrderAwarePluginRegistry}.
     * 
     * @see com.synyx.minos.core.plugin.support.BeanListBeanFactoryPostProcessor#
     *      wrapBeanDefinition
     *      (org.springframework.beans.factory.config.BeanDefinition)
     * @return a {@link BeanDefinition} containing a
     *         {@link OrderAwarePluginRegistry}
     */
    @Override
    protected BeanDefinition wrapListBeanDefinition(
            BeanDefinition beanDefinition) {

        // Create PluginRegistry bean definition to wrap actual bean definition
        BeanDefinitionBuilder builder =
                BeanDefinitionBuilder
                        .rootBeanDefinition(OrderAwarePluginRegistry.class);

        builder.addPropertyValue("plugins", beanDefinition);

        return getSourcedBeanDefinition(builder);
    }
}
