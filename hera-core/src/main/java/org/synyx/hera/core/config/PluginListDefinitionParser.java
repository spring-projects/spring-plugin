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

    protected static final String PACKAGE = "org.synyx.hera.core.support.";


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
     * #getBeanClassName(org.w3c.dom.Element)
     */
    @Override
    protected String getBeanClassName(Element element) {

        return PACKAGE + "BeanListBeanFactoryPostProcessor";
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
