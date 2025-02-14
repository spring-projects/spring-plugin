/*
 * Copyright 2008-2019 the original author or authors.
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
package org.springframework.plugin.core.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.plugin.core.OrderAwarePluginRegistry;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.core.PluginRegistry;

/**
 * {@link FactoryBean} to create {@link PluginRegistry} instances.
 *
 * @author Oliver Gierke
 */
public class PluginRegistryFactoryBean<T extends Plugin<S>, S>
		implements FactoryBean<PluginRegistry<T, S>>, BeanFactoryAware, ApplicationContextAware, InitializingBean {

	private Collection<Class<?>> exclusions = Collections.emptySet();
	private @Nullable Class<T> type;
	private @Nullable ListableBeanFactory factory;

	/**
	 * Configures the type of beans to be looked up.
	 *
	 * @param type the type to set
	 */
	public void setType(Class<T> type) {
		this.type = type;
	}

	/**
	 * Configures the types to be excluded from the lookup.
	 *
	 * @param exclusions
	 */
	public void setExclusions(Class<?>[] exclusions) {
		this.exclusions = Arrays.asList(exclusions);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

		if (!(beanFactory instanceof ListableBeanFactory factory)) {
			throw new IllegalArgumentException("Expected a ListableBeanFactory!");
		}

		this.factory = factory;
	}

	/**
	 * @see ApplicationContextAware#setApplicationContext(ApplicationContext)
	 * @deprecated since 4.0, in favor of {@link #setBeanFactory(BeanFactory)}.
	 */
	@Override
	@Deprecated
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		setBeanFactory(applicationContext);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@NonNull
	public OrderAwarePluginRegistry<T, S> getObject() {

		var type = this.type;

		if (type == null) {
			throw new IllegalStateException("No plugin type configured!");
		}

		var factory = this.factory;

		if (factory == null) {
			throw new IllegalStateException("No ListableBeanFactory configured!");
		}

		Supplier<List<? extends T>> plugins = () -> factory.getBeanProvider(type, false)
				.stream(Predicate.not(exclusions::contains))
				.toList();

		return OrderAwarePluginRegistry.of(plugins);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@NonNull
	public Class<?> getObjectType() {
		return OrderAwarePluginRegistry.class;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	public boolean isSingleton() {
		return true;
	}

	/**
	 * @see InitializingBean#afterPropertiesSet()
	 * @deprecated since 4.0, not needed anymore.
	 */
	@Override
	@Deprecated
	public void afterPropertiesSet() {
		// Only here for backwards-compatibility
	}
}
