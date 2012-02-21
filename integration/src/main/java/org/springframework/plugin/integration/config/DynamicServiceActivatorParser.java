/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.plugin.integration.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractConsumerEndpointParser;
import org.springframework.plugin.core.support.PluginRegistryFactoryBean;
import org.springframework.plugin.integration.PluginRegistryAwareMessageHandler;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} to create {@link PluginRegistryAwareMessageHandler} beans.
 * 
 * @author Oliver Gierke
 */
public class DynamicServiceActivatorParser extends AbstractConsumerEndpointParser {

	/*
	 * (non-Javadoc)
	 * @see org.springframework.integration.config.xml.AbstractConsumerEndpointParser#parseHandler(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected BeanDefinitionBuilder parseHandler(Element element, ParserContext parserContext) {

		Object source = parserContext.extractSource(element);

		String pluginType = element.getAttribute("plugin-type");
		String method = element.getAttribute("method");

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(PluginRegistryAwareMessageHandler.class);
		builder.addConstructorArgValue(getRegistryBeanDefinition(pluginType, source));
		builder.addConstructorArgValue(pluginType);
		builder.addConstructorArgValue(method);

		String delimiter = element.getAttribute("delimiter");

		if (StringUtils.hasText(delimiter)) {
			builder.addPropertyValue("delimiterExpression", delimiter);
		}

		String invocationArguments = element.getAttribute("invocation-arguments");

		if (StringUtils.hasText(invocationArguments)) {
			builder.addPropertyValue("invocationArgumentsExpression", invocationArguments);
		}

		AbstractBeanDefinition definition = builder.getBeanDefinition();
		definition.setSource(source);

		return builder;
	}

	/**
	 * Creates a {@link BeanDefinition} for a {@link PluginRegistryFactoryBean}.
	 * 
	 * @param pluginType
	 * @param source
	 * @return
	 */
	private AbstractBeanDefinition getRegistryBeanDefinition(String pluginType, Object source) {

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(PluginRegistryFactoryBean.class);
		builder.addPropertyValue("type", pluginType);

		AbstractBeanDefinition definition = builder.getBeanDefinition();
		definition.setSource(source);
		return definition;
	}
}
