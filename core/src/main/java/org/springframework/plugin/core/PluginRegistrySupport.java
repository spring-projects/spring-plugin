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
package org.springframework.plugin.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

/**
 * Base class for {@link PluginRegistry} implementations. Implements an initialization mechanism triggered on forst
 * invocation of {@link #getPlugins()}.
 * 
 * @author Oliver Gierke
 */
public abstract class PluginRegistrySupport<T extends Plugin<S>, S> implements PluginRegistry<T, S>, Iterable<T> {

	private List<T> plugins;
	private boolean initialized;

	/**
	 * Creates a new {@link PluginRegistrySupport} instance using the given plugins.
	 * 
	 * @param plugins must not be {@literal null}.
	 */
	@SuppressWarnings("unchecked")
	public PluginRegistrySupport(List<? extends T> plugins) {

		Assert.notNull(plugins, "Plugins must not be null!");

		this.plugins = plugins == null ? new ArrayList<>() : (List<T>) plugins;
		this.initialized = false;
	}

	/**
	 * Returns all registered plugins. Only use this method if you really need to access all plugins. For distinguished
	 * access to certain plugins favour accessor methods like {link #getPluginFor} over this one. This method should only
	 * be used for testing purposes to check registry configuration.
	 * 
	 * @return all plugins of the registry
	 */
	public List<T> getPlugins() {

		if (!initialized) {
			this.plugins = initialize(this.plugins);
			this.initialized = true;
		}

		return plugins;
	}

	/**
	 * Callback to initialize the plugin {@link List}. Will create a defensive copy of the {@link List} to potentially
	 * unwrap a {@link List} proxy. Will filter {@literal null} values from the source list as well.
	 * 
	 * @param plugins must not be {@literal null}.
	 * @return
	 */
	protected synchronized List<T> initialize(List<T> plugins) {

		Assert.notNull(plugins, "Plugins must not be null!");

		return plugins.stream() //
				.filter(it -> it != null) //
				.collect(Collectors.toList());
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
