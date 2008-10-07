package org.synyx.plugin.core.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;


/**
 * @author Oliver Gierke - gierke@synyx.de
 */
public class PluginListDefinitionParser extends
        AbstractSingleBeanDefinitionParser {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
     * #getBeanClassName(org.w3c.dom.Element)
     */
    @Override
    protected String getBeanClassName(Element element) {

        return "org.synyx.plugin.core.support.BeanListBeanFactoryPostProcessor";
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
     * #doParse(org.w3c.dom.Element,
     * org.springframework.beans.factory.support.BeanDefinitionBuilder)
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void doParse(Element element, BeanDefinitionBuilder builder) {

        ManagedMap map = new ManagedMap();
        map.put(element.getAttribute("id"), element.getAttribute("class"));

        builder.addPropertyValue("lists", map);

        String initFactories = element.getAttribute("init-factories");

        if (StringUtils.hasText(initFactories)) {
            builder.addPropertyValue("allowEagerInit", initFactories);
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @seeorg.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * shouldGenerateId()
     */
    @Override
    protected boolean shouldGenerateId() {

        return true;
    }
}
