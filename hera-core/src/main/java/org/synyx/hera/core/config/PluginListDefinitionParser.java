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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
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
     * Returns the name of the {@link BeanFactoryPostProcessor} to be
     * registered.
     * 
     * @return
     */
    protected String getPostProcessorName() {

        return PACKAGE + "BeanListBeanFactoryPostProcessor";
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

        if (context.isNested()) {
            return parseNested(element, context);
        } else {
            return parseStandalone(element, context);
        }
    }


    /**
     * Parses a nested {@code <list />} or {@code <registry />} element.
     * Registers the {@link BeanFactoryPostProcessor} as in standalone mode but
     * returns a reference to the list or registry created by instead of the
     * {@link BeanFactoryPostProcessor} itself.
     * 
     * @param element
     * @param context
     * @return
     */
    private AbstractBeanDefinition parseNested(Element element,
            ParserContext context) {

        // Step 1 - Create reference wrapped in a bean definition
        BeanDefinitionBuilder builder =
                BeanDefinitionBuilder
                        .genericBeanDefinition(RuntimeBeanNameReference.class
                                .getName());

        // Extract id attribute from element or generate custom one
        String idAttribute = element.getAttribute("id");
        String listId =
                StringUtils.hasText(idAttribute) ? idAttribute
                        : BeanDefinitionReaderUtils.generateBeanName(builder
                                .getBeanDefinition(), context.getRegistry(),
                                true);

        // Let reference point to the bean with the calculated id
        builder.addConstructorArgValue(listId);

        // Step 2 - Register BeanPostProcessor to unwrap the reference
        registerReferenceResolver(element, context);

        // Step 3 - Set the id to let the BeanFactoryPostProcessor (BFPP)
        // register the list as reference target
        element.setAttribute("id", listId);

        // Step 4 - Register BFPP ourselves
        AbstractBeanDefinition definition = parseStandalone(element, context);
        String beanFactoryxPostProcessorId =
                resolveId(element, definition, context);

        BeanDefinitionHolder holder =
                new BeanDefinitionHolder(definition,
                        beanFactoryxPostProcessorId);
        registerBeanDefinition(holder, context.getRegistry());

        // Step 5 - Return the reference to the list created by the BFPP
        return getSourcedBeanDefinition(builder, element, context);
    }


    /**
     * Registers a {@link BeanReferenceBeanPostProcessor} to automatically
     * unwrap {@link BeanDefinition}s that contain a reference to other beans.
     * 
     * @param element
     * @param context
     */
    private void registerReferenceResolver(Element element,
            ParserContext context) {

        BeanDefinitionBuilder builder =
                BeanDefinitionBuilder
                        .genericBeanDefinition(BeanReferenceBeanPostProcessor.class);

        BeanDefinition definition =
                getSourcedBeanDefinition(builder, element, context);

        registerBeanDefinition(new BeanDefinitionHolder(definition, generateId(
                definition, context)), context.getRegistry());
    }


    /**
     * Parses a standalone {@code <list />} or {@code <registry />} element and
     * registers a {@link BeanFactoryPostProcessor} to automatically register
     * all beans of the type specified in {@code class} property under the given
     * id.
     * 
     * @param element
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
    private AbstractBeanDefinition parseStandalone(Element element,
            ParserContext context) {

        ManagedMap map = new ManagedMap();
        map.put(element.getAttribute("id"), element.getAttribute("class"));

        BeanDefinitionBuilder builder =
                BeanDefinitionBuilder
                        .genericBeanDefinition(getPostProcessorName());
        builder.addPropertyValue("lists", map);

        String initFactories = element.getAttribute("init-factories");

        if (StringUtils.hasText(initFactories)) {
            builder.addPropertyValue("allowEagerInit", initFactories);
        }

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


    /**
     * Generates a custom id for the given {@link BeanDefinition}.
     * 
     * @param definition
     * @param context
     * @return
     */
    private String generateId(BeanDefinition definition, ParserContext context) {

        return context.getReaderContext().generateBeanName(definition);
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
