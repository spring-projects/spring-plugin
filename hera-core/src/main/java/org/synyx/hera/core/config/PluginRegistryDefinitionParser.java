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

import org.w3c.dom.Element;


/**
 * Simple extension of {@link PluginListDefinitionParser}. Simply registers a
 * {@code PluginListDefinitionParser} instead of the original class.
 * 
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

        return PACKAGE + "PluginRegistryBeanFactoryPostProcessor";
    }
}
