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
package org.springframework.plugin.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.util.Assert;

/**
 * Registry for {@link Plugin}s. Allows sophisticated typesafe access to implementations of interfaces extending {link
 * Plugin}.
 *
 * @param <T> the concrete {@link Plugin} interface
 * @param <S> the delimiter type
 * @author Oliver Gierke
 */
public interface PluginRegistry<T extends Plugin<S>, S> extends Iterable<T> {

	/**
	 * Creates a new {@link PluginRegistry} using the {@code #DEFAULT_COMPARATOR}.
	 *
	 * @return
	 * @since 2.0
	 */
	public static <S, T extends Plugin<S>> PluginRegistry<T, S> empty() {
		return of(Collections.emptyList());
	}

	/**
	 * Creates a new {@link PluginRegistry} using the given {@link Comparator} for ordering contained {@link Plugin}s.
	 *
	 * @param comparator must not be {@literal null}.
	 * @return
	 * @since 2.0
	 */
	public static <S, T extends Plugin<S>> PluginRegistry<T, S> of(Comparator<? super T> comparator) {

		Assert.notNull(comparator, "Comparator must not be null!");

		return of(Collections.emptyList(), comparator);
	}

	/**
	 * Creates a new {@link PluginRegistry} with the given plugins.
	 *
	 * @param plugins must not be {@literal null}.
	 * @return
	 * @since 2.0
	 */
	@SafeVarargs
	public static <S, T extends Plugin<S>> PluginRegistry<T, S> of(T... plugins) {
		return of(Arrays.asList(plugins), OrderAwarePluginRegistry.DEFAULT_COMPARATOR);
	}

	/**
	 * Creates a new {@link OrderAwarePluginRegistry} with the given plugins.
	 *
	 * @param plugins must not be {@literal null}.
	 * @return
	 * @since 2.0
	 */
	public static <S, T extends Plugin<S>> PluginRegistry<T, S> of(List<? extends T> plugins) {
		return of(plugins, OrderAwarePluginRegistry.DEFAULT_COMPARATOR);
	}

	/**
	 * Creates a new {@link OrderAwarePluginRegistry} with the given plugins.
	 *
	 * @param plugins
	 * @return
	 * @since 2.0
	 */
	public static <S, T extends Plugin<S>> PluginRegistry<T, S> of(List<? extends T> plugins,
			Comparator<? super T> comparator) {

		Assert.notNull(plugins, "Plugins must not be null!");
		Assert.notNull(comparator, "Comparator must not be null!");

		return OrderAwarePluginRegistry.of(plugins, comparator);
	}

	/**
	 * Returns the first {@link Plugin} found for the given delimiter. Thus, further configured {@link Plugin}s are
	 * ignored.
	 *
	 * @param delimiter must not be {@literal null}.
	 * @return a plugin for the given delimiter or {@link Optional#empty()} if none found.
	 */
	Optional<T> getPluginFor(S delimiter);

	/**
	 * Returns the first {@link Plugin} found for the given delimiter. Thus, further configured {@link Plugin}s are
	 * ignored.
	 *
	 * @param delimiter must not be {@literal null}.
	 * @return a {@link Plugin} for the given originating system or {@link Optional#empty()} if none found.
	 * @throws IllegalArgumentException in case no {@link Plugin} for the given delimiter
	 */
	T getRequiredPluginFor(S delimiter) throws IllegalArgumentException;

	/**
	 * Returns the first {@link Plugin} found for the given delimiter. Thus, further configured {@link Plugin}s are
	 * ignored.
	 *
	 * @param delimiter must not be {@literal null}.
	 * @param message a {@link Supplier} to produce an exception message in case no plugin is found.
	 * @return a {@link Plugin} for the given originating system or {@link Optional#empty()} if none found.
	 * @throws IllegalArgumentException in case no {@link Plugin} for the given delimiter
	 */
	T getRequiredPluginFor(S delimiter, Supplier<String> message) throws IllegalArgumentException;

	/**
	 * Returns all plugins for the given delimiter.
	 *
	 * @param delimiter must not be {@literal null}.
	 * @return a list of plugins or an empty list if none found
	 */
	List<T> getPluginsFor(S delimiter);

	/**
	 * Retrieves a required plugin from the registry or throw the given exception if none can be found. If more than one
	 * plugins are found the first one will be returned.
	 *
	 * @param <E> the exception type to be thrown in case no plugin can be found.
	 * @param delimiter must not be {@literal null}.
	 * @param ex a lazy {@link Supplier} to produce an exception in case no plugin can be found, must not be
	 *          {@literal null}.
	 * @return a single plugin for the given delimiter
	 * @throws E if no plugin can be found for the given delimiter
	 */
	<E extends Exception> T getPluginFor(S delimiter, Supplier<E> ex) throws E;

	/**
	 * Retrieves all plugins for the given delimiter or throws an exception if no plugin can be found.
	 *
	 * @param <E> the exception type to be thrown.
	 * @param delimiter must not be {@literal null}.
	 * @param ex a lazy {@link Supplier} to produce an exception in case no plugin can be found, must not be
	 *          {@literal null}.
	 * @return all plugins for the given delimiter
	 * @throws E if no plugin can be found
	 */
	<E extends Exception> List<T> getPluginsFor(S delimiter, Supplier<E> ex) throws E;

	/**
	 * Returns the first {@link Plugin} supporting the given delimiter or the given plugin if none can be found.
	 *
	 * @param delimiter must not be {@literal null}.
	 * @param plugin must not be {@literal null}.
	 * @return a single {@link Plugin} supporting the given delimiter or the given {@link Plugin} if none found
	 */
	T getPluginOrDefaultFor(S delimiter, T plugin);

	/**
	 * Returns the first {@link Plugin} supporting the given delimiter or the given lazily-provided plugin if none can be
	 * found.
	 *
	 * @param delimiter can be {@literal null}.
	 * @param defaultSupplier must not be {@literal null}.
	 * @return a single {@link Plugin} supporting the given delimiter or the given lazily provided {@link Plugin} if none
	 *         found.
	 */
	T getPluginOrDefaultFor(S delimiter, Supplier<T> defaultSupplier);

	/**
	 * Returns all {@link Plugin}s supporting the given delimiter or the given plugins if none found.
	 *
	 * @param delimiter must not be {@literal null}.
	 * @param plugins must not be {@literal null}.
	 * @return all {@link Plugin}s supporting the given delimiter or the given {@link Plugin}s if none found, will never
	 *         be {@literal null}.
	 */
	List<T> getPluginsFor(S delimiter, List<? extends T> plugins);

	/**
	 * Returns the number of registered plugins.
	 *
	 * @return the number of plugins in the registry
	 */
	int countPlugins();

	/**
	 * Returns whether the registry contains a given plugin.
	 *
	 * @param plugin must not be {@literal null}.
	 * @return
	 */
	boolean contains(T plugin);

	/**
	 * Returns whether the registry contains a {@link Plugin} matching the given delimiter.
	 *
	 * @param delimiter must not be {@literal null}.
	 * @return
	 */
	boolean hasPluginFor(S delimiter);

	/**
	 * Returns all {@link Plugin}s contained in this registry. Will return an immutable {@link List} to prevent outside
	 * modifications of the {@link PluginRegistry} content.
	 *
	 * @return will never be {@literal null}.
	 */
	List<T> getPlugins();
}
