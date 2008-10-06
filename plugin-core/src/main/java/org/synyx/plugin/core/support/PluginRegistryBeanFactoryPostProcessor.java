package org.synyx.plugin.core.support;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.synyx.plugin.core.PluginRegistry;


/**
 * This {@link BeanFactoryPostProcessor} automatically looksup bean instances
 * from the {@link BeanFactory} hierarchy and registers {@link PluginRegistry}
 * instances for them.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class PluginRegistryBeanFactoryPostProcessor extends
        BeanListBeanFactoryPostProcessor {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.synyx.minos.core.plugin.support.BeanListBeanFactoryPostProcessor#
     * wrapBeanDefinition
     * (org.springframework.beans.factory.config.BeanDefinition)
     */
    @Override
    protected BeanDefinition wrapListBeanDefinition(
            BeanDefinition beanDefinition) {

        // Create PluginRegistry bean definition
        BeanDefinitionBuilder builder =
                BeanDefinitionBuilder.rootBeanDefinition(PluginRegistry.class);

        // Set list definition
        builder.addPropertyValue("plugins", beanDefinition);

        return getSourcedBeanDefinition(builder);
    }
}
