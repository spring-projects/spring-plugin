/*
 * Copyright 2008-2019 the original author or authors.
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;

/**
 * {@link PluginRegistry} implementation that be made aware of a certain ordering of {@link Plugin}s. By default it
 * orders {@link Plugin}s by regarding {@link org.springframework.core.Ordered} interface or
 * {@link org.springframework.core.annotation.Order} annotation. To alter ordering behaviour use one of the factory
 * methods accepting a {@link Comparator} as parameter.
 * 
 * @author Oliver Gierke
 */
public class OrderAwarePluginRegistry<T extends Plugin<S>, S> extends SimplePluginRegistry<T, S> {

	/**
	 * Comparator regarding {@link org.springframework.core.Ordered} interface or
	 * {@link org.springframework.core.annotation.Order} annotation.
	 */
	static final Comparator<Object> DEFAULT_COMPARATOR = new AnnotationAwareOrderComparator();

	/**
	 * Comparator reverting the {@value #DEFAULT_COMPARATOR}.
	 */
	static final Comparator<Object> DEFAULT_REVERSE_COMPARATOR = DEFAULT_COMPARATOR.reversed();

	private final Comparator<? super T> comparator;

	/**
	 * Creates a new {@link OrderAwarePluginRegistry} with the given {@link Plugin}s and {@link Comparator}.
	 * 
	 * @param plugins the {@link Plugin}s to be contained in the registry or {@literal null} if the registry shall be
	 *          empty initally.
	 * @param comparator the {@link Comparator} to be used for ordering the {@link Plugin}s or {@literal null} if the
	 *          {@code #DEFAULT_COMPARATOR} shall be used.
	 */
	protected OrderAwarePluginRegistry(List<? extends T> plugins, Comparator<? super T> comparator) {

		super(plugins);

		Assert.notNull(comparator, "Comparator must not be null!");

		this.comparator = comparator;
	}

	/**
	 * Creates a new {@link OrderAwarePluginRegistry} using the {@code #DEFAULT_COMPARATOR}.
	 * 
	 * @return
	 * @since 2.0
	 */
	public static <S, T extends Plugin<S>> OrderAwarePluginRegistry<T, S> empty() {
		return create(Collections.emptyList());
	}

	/**
	 * Creates a new {@link OrderAwarePluginRegistry} using the given {@link Comparator} for ordering contained
	 * {@link Plugin}s.
	 * 
	 * @param comparator must not be {@literal null}.
	 * @return
	 * @since 2.0
	 */
	public static <S, T extends Plugin<S>> OrderAwarePluginRegistry<T, S> of(Comparator<? super T> comparator) {

		Assert.notNull(comparator, "Comparator must not be null!");

		return create(Collections.emptyList(), comparator);
	}

	/**
	 * Creates a new {@link OrderAwarePluginRegistry} with the given plugins.
	 * @param plugins must not be {@literal null}.
	 * @return
	 * @since 2.0
	 */
	@SafeVarargs
	public static <S, T extends Plugin<S>> OrderAwarePluginRegistry<T, S> of(T... plugins) {
		return create(Arrays.asList(plugins), DEFAULT_COMPARATOR);
	}

	/**
	 * Creates a new {@link OrderAwarePluginRegistry} with the given plugins.
	 *
	 * @param plugins must not be {@literal null}.
	 * @return
	 * @since 2.0
	 */
	public static <S, T extends Plugin<S>> OrderAwarePluginRegistry<T, S> of(List<? extends T> plugins) {
		return create(plugins, DEFAULT_COMPARATOR);
	}

	/**
	 * Creates a new {@link OrderAwarePluginRegistry} with the given {@link Plugin}s and the order of the {@link Plugin}s
	 * reverted.
	 * 
	 * @param plugins must not be {@literal null}.
	 * @return
	 * @since 2.0
	 */
	public static <S, T extends Plugin<S>> OrderAwarePluginRegistry<T, S> ofReverse(List<? extends T> plugins) {
		return create(plugins, DEFAULT_REVERSE_COMPARATOR);
	}

	/**
	 * Creates a new {@link OrderAwarePluginRegistry} with the given plugins.
	 * 
	 * @param plugins
	 * @return
	 * @since 2.0
	 */
	public static <S, T extends Plugin<S>> OrderAwarePluginRegistry<T, S> of(List<? extends T> plugins,
			Comparator<? super T> comparator) {

		Assert.notNull(plugins, "Plugins must not be null!");
		Assert.notNull(comparator, "Comparator must not be null!");

		return new OrderAwarePluginRegistry<>(plugins, comparator);
	}

	/**
	 * Creates a new {@link OrderAwarePluginRegistry} using the {@code #DEFAULT_COMPARATOR}.
	 *
	 * @return
	 * @deprecated since 2.0, for removal in 2.1. Prefer {@link PluginRegistry#empty()}.
	 */
	@Deprecated
	public static <S, T extends Plugin<S>> OrderAwarePluginRegistry<T, S> create() {
		return empty();
	}

	/**
	 * Creates a new {@link OrderAwarePluginRegistry} using the given {@link Comparator} for ordering contained
	 * {@link Plugin}s.
	 *
	 * @param comparator must not be {@literal null}.
	 * @return
	 * @deprecated since 2.0, for removal in 2.1. Prefer {@link PluginRegistry#of(Comparator)}.
	 */
	@Deprecated
	public static <S, T extends Plugin<S>> OrderAwarePluginRegistry<T, S> create(Comparator<? super T> comparator) {

		Assert.notNull(comparator, "Comparator must not be null!");

		return of(Collections.emptyList(), comparator);
	}

	/**
	 * Creates a new {@link OrderAwarePluginRegistry} with the given plugins.
	 *
	 * @param plugins must not be {@literal null}.
	 * @return
	 * @deprecated since 2.0, for removal in 2.1. Prefer {@link PluginRegistry#of(List)}.
	 */
	@Deprecated
	public static <S, T extends Plugin<S>> OrderAwarePluginRegistry<T, S> create(List<? extends T> plugins) {
		return of(plugins, DEFAULT_COMPARATOR);
	}

	/**
	 * Creates a new {@link OrderAwarePluginRegistry} with the given {@link Plugin}s and the order of the {@link Plugin}s
	 * reverted.
	 *
	 * @param plugins must not be {@literal null}.
	 * @return
	 * @deprecated since 2.0, for removal in 2.1. Prefer {@link OrderAwarePluginRegistry#ofReverse(List)}
	 */
	@Deprecated
	public static <S, T extends Plugin<S>> OrderAwarePluginRegistry<T, S> createReverse(List<? extends T> plugins) {
		return of(plugins, DEFAULT_REVERSE_COMPARATOR);
	}

	/**
	 * Creates a new {@link OrderAwarePluginRegistry} with the given plugins.
	 *
	 * @param plugins must not be {@literal null}.
	 * @return
	 * @deprecated since 2.0, for removal in 2.1. Prefer {@link PluginRegistry#of(List, Comparator)}.
	 */
	@Deprecated
	public static <S, T extends Plugin<S>> OrderAwarePluginRegistry<T, S> create(List<? extends T> plugins,
			Comparator<? super T> comparator) {

		return of(plugins, comparator);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.plugin.core.PluginRegistrySupport#initialize(java.util.List)
	 */
	@Override
	protected List<T> initialize(List<T> plugins) {

		List<T> result = super.initialize(plugins);
		Collections.sort(result, comparator);
		return result;
	}

	/**
	 * Returns a new {@link OrderAwarePluginRegistry} with the order of the plugins reverted.
	 * 
	 * @return
	 */
	public OrderAwarePluginRegistry<T, S> reverse() {

		List<T> copy = new ArrayList<>(getPlugins());
		return create(copy, comparator.reversed());
	}
}
