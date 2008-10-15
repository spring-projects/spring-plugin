package org.synyx.hera.core.support;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.synyx.hera.core.PluginRegistry;


/**
 * This {@link BeanFactoryPostProcessor} automatically looksup bean instances
 * from the {@link BeanFactory} hierarchy and registers {@link PluginRegistry}
 * instances for them.
 * 
 * @see org.synyx.hera.core.support.BeanListBeanFactoryPostProcessor
 * @author Oliver Gierke - gierke@synyx.de
 */
public class PluginRegistryBeanFactoryPostProcessor extends
        BeanListBeanFactoryPostProcessor {

    /**
     * Additionally wraps the
     * {@link org.springframework.beans.factory.config.ListFactoryBean}
     * {@link BeanDefinition} into a {@link PluginRegistry}.
     * 
     * @see com.synyx.minos.core.plugin.support.BeanListBeanFactoryPostProcessor#
     *      wrapBeanDefinition
     *      (org.springframework.beans.factory.config.BeanDefinition)
     * @return a {@link BeanDefinition} containing a {@link PluginRegistry}
     */
    @Override
    protected BeanDefinition wrapListBeanDefinition(
            BeanDefinition beanDefinition) {

        // Create PluginRegistry bean definition to wrap actual bean definition
        BeanDefinitionBuilder builder =
                BeanDefinitionBuilder.rootBeanDefinition(PluginRegistry.class);

        builder.addPropertyValue("plugins", beanDefinition);

        return getSourcedBeanDefinition(builder);
    }
}
