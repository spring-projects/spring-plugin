/*
 * Copyright 2012 the original author or authors.
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
package org.springframework.plugin.core;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.util.Assert;
import org.springframework.util.function.SingletonSupplier;

/**
 * Base class for {@link PluginRegistry} implementations. Implements an initialization mechanism triggered on forst
 * invocation of {@link #getPlugins()}.
 *
 * @author Oliver Gierke
 */
abstract class PluginRegistrySupport<T extends Plugin<S>, S> implements PluginRegistry<T, S>, Iterable<T> {

	private final Supplier<List<T>> plugins;

	/**
	 * Creates a new {@link PluginRegistrySupport} instance using the given plugins.
	 *
	 * @param plugins must not be {@literal null}.
	 */
	@SuppressWarnings("unchecked")
	public PluginRegistrySupport(List<? extends T> plugins) {

		Assert.notNull(plugins, "Plugins must not be null!");

		this.plugins = SingletonSupplier.of((List<T>) plugins.stream().filter(it -> it != null).toList());
	}

	@SuppressWarnings("unchecked")
	protected PluginRegistrySupport(Supplier<List<? extends T>> plugins) {

		this.plugins = () -> (List<T>) plugins.get().stream()
				.filter(it -> it != null)
				.toList();
	}

	/**
	 * Returns all registered plugins. Only use this method if you really need to access all plugins. For distinguished
	 * access to certain plugins favour accessor methods like {link #getPluginFor} over this one. This method should only
	 * be used for testing purposes to check registry configuration.
	 *
	 * @return all plugins of the registry
	 */
	public List<T> getPlugins() {
		return plugins.get();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return getPlugins().iterator();
	}
}
