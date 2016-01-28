/*
 * Copyright 2008-2016 the original author or authors.
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
import java.util.Collections;
import java.util.List;

/**
 * Basic implementation of {@link PluginRegistry}. Simply holds all given plugins in a list dropping {@literal null}
 * values silently on adding.
 * 
 * @param <T> the concrete plugin interface
 * @param <S> the delimiter type
 * @author Oliver Gierke
 */
public class SimplePluginRegistry<T extends Plugin<S>, S> extends PluginRegistrySupport<T, S> {

	/**
	 * Creates a new {@code SimplePluginRegistry}. Will create an empty registry if {@literal null} is provided.
	 * 
	 * @param plugins must not be {@literal null}.
	 */
	protected SimplePluginRegistry(List<? extends T> plugins) {
		super(plugins);
	}

	/**
	 * Creates a new {@link SimplePluginRegistry}.
	 * 
	 * @param <T> the plugin type
	 * @param <S> the delimiter type
	 * @return
	 */
	public static <S, T extends Plugin<S>> SimplePluginRegistry<T, S> create() {
		return create(Collections.<T> emptyList());
	}

	/**
	 * Creates a new {@link SimplePluginRegistry} with the given {@link Plugin} s.
	 * 
	 * @param <T> the plugin type
	 * @param <S> the delimiter type
	 * @return
	 */
	public static <S, T extends Plugin<S>> SimplePluginRegistry<T, S> create(List<? extends T> plugins) {
		return new SimplePluginRegistry<T, S>(plugins);
	}

	/* (non-Javadoc)
	 * @see org.springframework.plugin.core.PluginRegistrySupport#getPlugins()
	 */
	@Override
	public List<T> getPlugins() {
		return Collections.unmodifiableList(super.getPlugins());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.plugin.core.PluginRegistry#getPluginFor(java.lang.Object)
	 */
	public T getPluginFor(S delimiter) {

		for (T plugin : super.getPlugins()) {
			if (plugin != null && plugin.supports(delimiter)) {
				return plugin;
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.plugin.core.PluginRegistry#getPluginsFor(java.lang.Object)
	 */
	public List<T> getPluginsFor(S delimiter) {

		List<T> result = new ArrayList<T>();

		for (T plugin : super.getPlugins()) {
			if (plugin != null && plugin.supports(delimiter)) {
				result.add(plugin);
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.plugin.core.PluginRegistry#getPluginFor(java.lang.Object, java.lang.Exception)
	 */
	public <E extends Exception> T getPluginFor(S delimiter, final E ex) throws E {

		return getPluginFor(delimiter, new Supplier<E>() {

			@Override
			public E get() {
				return ex;
			}
		});
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.plugin.core.PluginRegistry#getPluginFor(java.lang.Object, org.springframework.plugin.core.PluginRegistry.Supplier)
	 */
	@Override
	public <E extends Exception> T getPluginFor(S delimiter, Supplier<E> ex) throws E {

		T plugin = getPluginFor(delimiter);

		if (null == plugin) {
			throw ex.get();
		}

		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.plugin.core.PluginRegistry#getPluginsFor(java.lang.Object, java.lang.Exception)
	 */
	public <E extends Exception> List<T> getPluginsFor(S delimiter, final E ex) throws E {

		return getPluginsFor(delimiter, new Supplier<E>() {

			@Override
			public E get() {
				return ex;
			}
		});
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.plugin.core.PluginRegistry#getPluginsFor(java.lang.Object, org.springframework.plugin.core.PluginRegistry.ExceptionProvider)
	 */
	@Override
	public <E extends Exception> List<T> getPluginsFor(S delimiter, Supplier<E> ex) throws E {

		List<T> result = getPluginsFor(delimiter);

		if (result.isEmpty()) {
			throw ex.get();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.plugin.core.PluginRegistry#getPluginFor(java.lang.Object, org.springframework.plugin.core.Plugin)
	 */
	public T getPluginFor(S delimiter, T plugin) {

		T candidate = getPluginFor(delimiter);

		return null == candidate ? plugin : candidate;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.plugin.core.PluginRegistry#getPluginsFor(java.lang.Object, java.util.List)
	 */
	public List<T> getPluginsFor(S delimiter, List<? extends T> plugins) {

		List<T> candidates = getPluginsFor(delimiter);

		return candidates.isEmpty() ? new ArrayList<T>(plugins) : candidates;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.plugin.core.PluginRegistry#countPlugins()
	 */
	public int countPlugins() {

		return super.getPlugins().size();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.plugin.core.PluginRegistry#contains(org.springframework.plugin.core.Plugin)
	 */
	public boolean contains(T plugin) {
		return super.getPlugins().contains(plugin);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.plugin.core.PluginRegistry#hasPluginFor(java.lang.Object)
	 */
	public boolean hasPluginFor(S delimiter) {
		return null != getPluginFor(delimiter);
	}
}
