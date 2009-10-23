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
package org.synyx.hera.core.config;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;


/**
 * Bean definition parser to register {@code <list />} elements from the plugin
 * namespace.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class PluginListDefinitionParser extends AbstractBeanDefinitionParser {

    protected static final String PACKAGE = "org.synyx.hera.core.support.";


    /**
     * Returns the name of the
     * {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor}
     * to be registered.
     * 
     * @return
     */
    protected String getPostProcessorName() {

        return PACKAGE + "BeanListFactoryBean";
    }


    /*
     * (non-Javadoc)
     * 
     * @seeorg.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * parseInternal(org.w3c.dom.Element,
     * org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    protected AbstractBeanDefinition parseInternal(Element element,
            ParserContext context) {

        BeanDefinitionBuilder builder =
                BeanDefinitionBuilder
                        .genericBeanDefinition(getPostProcessorName());
        builder.addPropertyValue("type", element.getAttribute("class"));

        return getSourcedBeanDefinition(builder, element, context);
    }


    /**
     * Returns the bean definition prepared by the builder and has connected it
     * to the {@code source} object.
     * 
     * @param builder
     * @param source
     * @param context
     * @return
     */
    private AbstractBeanDefinition getSourcedBeanDefinition(
            BeanDefinitionBuilder builder, Object source, ParserContext context) {

        AbstractBeanDefinition definition = builder.getRawBeanDefinition();
        definition.setSource(context.extractSource(source));

        return definition;
    }


    /*
     * (non-Javadoc)
     * 
     * @seeorg.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * shouldGenerateIdAsFallback()
     */
    @Override
    protected boolean shouldGenerateIdAsFallback() {

        return true;
    }
}
