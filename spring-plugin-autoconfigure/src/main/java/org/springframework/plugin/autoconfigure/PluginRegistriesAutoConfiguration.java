/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.plugin.autoconfigure;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.config.PluginRegistriesBeanDefinitionRegistrar;

/**
 * Registers plugin registries for direct plugin interfaces implemented by Spring beans.
 *
 * @author Gabriel Hall
 */
@AutoConfiguration
public final class PluginRegistriesAutoConfiguration {

	@Bean
	static PluginRegistryBeanDefinitionRegistrar pluginRegistryBeanDefinitionRegistrar() {
		return new PluginRegistryBeanDefinitionRegistrar();
	}

	static final class PluginRegistryBeanDefinitionRegistrar
			implements BeanDefinitionRegistryPostProcessor, BeanFactoryAware {

		private @Nullable ListableBeanFactory beanFactory;

		@Override
		public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

			ListableBeanFactory beanFactory = getBeanFactory();

			for (Class<?> pluginType : getPluginTypes(beanFactory)) {
				ResolvableType registryType = PluginRegistriesBeanDefinitionRegistrar.getTargetType(pluginType,
						PluginRegistry.class);

				if (beanFactory.getBeanNamesForType(registryType, true, false).length == 0) {
					PluginRegistriesBeanDefinitionRegistrar.registerPluginRegistry(pluginType, registry);
				}
			}
		}

		@Override
		public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {}

		@Override
		public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
			this.beanFactory = (ListableBeanFactory) beanFactory;
		}

		private static Set<Class<?>> getPluginTypes(ListableBeanFactory beanFactory) {

			Set<Class<?>> result = new LinkedHashSet<>();

			for (String beanName : beanFactory.getBeanNamesForType(Plugin.class, true, false)) {

				Class<?> beanType = beanFactory.getType(beanName, false);

				if (beanType != null) {
					collectPluginTypes(beanType, result);
				}
			}

			return result;
		}

		private ListableBeanFactory getBeanFactory() {

			if (this.beanFactory == null) {
				throw new IllegalStateException("No ListableBeanFactory configured!");
			}

			return this.beanFactory;
		}

		private static void collectPluginTypes(Class<?> type, Set<Class<?>> result) {

			if (type.isInterface() && type != Plugin.class && Plugin.class.isAssignableFrom(type)) {
				result.add(type);
				return;
			}

			for (Class<?> candidate : type.getInterfaces()) {

				if (candidate != Plugin.class && Plugin.class.isAssignableFrom(candidate)) {
					result.add(candidate);
				}
			}

			Class<?> superclass = type.getSuperclass();

			if (superclass != null && superclass != Object.class) {
				collectPluginTypes(superclass, result);
			}
		}
	}
}
