package org.synyx.plugin.core.config;

import org.w3c.dom.Element;


/**
 * @author Oliver Gierke - gierke@synyx.de
 */
public class PluginRegistryDefinitionParser extends PluginListDefinitionParser {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
     * #getBeanClassName(org.w3c.dom.Element)
     */
    @Override
    protected String getBeanClassName(Element element) {

        return "org.synyx.plugin.core.support.PluginRegistryBeanFactoryPostProcessor";
    }
}
