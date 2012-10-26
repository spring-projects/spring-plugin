/*
 * Copyright 2012 the original author or authors.
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
package org.springframework.plugin.core.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.plugin.core.support.PluginRegistryFactoryBean;
import org.springframework.util.StringUtils;

/**
 * {@link ImportBeanDefinitionRegistrar} to register {@link PluginRegistryFactoryBean} instances for type listed in
 * {@link EnablePluginRegistries}. Picks up {@link Qualifier} annotations used on the plugin interface and forwards them
 * to the bean definition for the factory.
 * 
 * @author Oliver Gierke
 */
public class PluginRegistriesBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.context.annotation.ImportBeanDefinitionRegistrar#registerBeanDefinitions(org.springframework.core.type.AnnotationMetadata, org.springframework.beans.factory.support.BeanDefinitionRegistry)
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

		Class<?>[] types = (Class<?>[]) importingClassMetadata.getAnnotationAttributes(
				EnablePluginRegistries.class.getName()).get("value");

		for (Class<?> type : types) {

			BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(PluginRegistryFactoryBean.class);
			builder.addPropertyValue("type", type);

			AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
			Qualifier annotation = type.getAnnotation(Qualifier.class);

			// If the plugin interface has a Qualifier annotation, propagate that to the bean definition of the registry
			if (annotation != null) {
				AutowireCandidateQualifier qualifierMetadata = new AutowireCandidateQualifier(Qualifier.class);
				qualifierMetadata.setAttribute(AutowireCandidateQualifier.VALUE_KEY, annotation.value());
				beanDefinition.addQualifier(qualifierMetadata);
			}

			// Default
			String beanName = annotation == null ? StringUtils.uncapitalize(type.getSimpleName() + "Registry") : annotation
					.value();
			registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
		}
	}
}
