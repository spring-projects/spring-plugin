/*
 * Copyright 2011-2014 the original author or authors.
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

import org.w3c.dom.Element;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.ServiceActivatorFactoryBean;
import org.springframework.integration.config.xml.AbstractConsumerEndpointParser;
import org.springframework.plugin.core.support.PluginRegistryFactoryBean;
import org.springframework.plugin.integration.PluginMethodInvocationService;
import org.springframework.util.StringUtils;

/**
 * {@link BeanDefinitionParser} to create {@link org.springframework.integration.handler.ServiceActivatingHandler}
 * for {@link PluginMethodInvocationService} bean.
 *
 * @author Oliver Gierke
 * @author Artem Bilan
 */
public class DynamicServiceActivatorParser extends AbstractConsumerEndpointParser {

	@Override
	protected BeanDefinitionBuilder parseHandler(Element element, ParserContext parserContext) {

		String pluginType = element.getAttribute("plugin-type");
		String method = element.getAttribute("method");

		BeanDefinitionBuilder serviceBuilder = BeanDefinitionBuilder.rootBeanDefinition(PluginMethodInvocationService.class);
		serviceBuilder.addConstructorArgValue(getRegistryBeanDefinition(pluginType));
		serviceBuilder.addConstructorArgValue(pluginType);
		serviceBuilder.addConstructorArgValue(method);

		String delimiter = element.getAttribute("delimiter");

		if (StringUtils.hasText(delimiter)) {
			serviceBuilder.addPropertyValue("delimiterExpression", delimiter);
		}

		String invocationArguments = element.getAttribute("invocation-arguments");

		if (StringUtils.hasText(invocationArguments)) {
			serviceBuilder.addPropertyValue("invocationArgumentsExpression", invocationArguments);
		}

		String serviceBeanId = BeanDefinitionReaderUtils.registerWithGeneratedName(serviceBuilder.getBeanDefinition(),
				parserContext.getRegistry());

		return BeanDefinitionBuilder.rootBeanDefinition(ServiceActivatorFactoryBean.class)
				.addPropertyValue("expressionString", "@'" + serviceBeanId + "'.invoke(#root)");
	}

	/**
	 * Create a {@link BeanDefinition} for a {@link PluginRegistryFactoryBean}.
	 * @param pluginType the pluginType to create.
	 * @return the {@link AbstractBeanDefinition} for {@link PluginRegistryFactoryBean}.
	 */
	private AbstractBeanDefinition getRegistryBeanDefinition(String pluginType) {

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(PluginRegistryFactoryBean.class);
		builder.addPropertyValue("type", pluginType);

		return builder.getBeanDefinition();
	}

}
