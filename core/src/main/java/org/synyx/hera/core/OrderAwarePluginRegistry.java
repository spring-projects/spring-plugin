/*
 * Copyright 2002-2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.synyx.hera.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;


/**
 * {@link PluginRegistry} implementation that be made aware of a certain
 * ordering of {@link Plugin}s. By default it orders {@link Plugin}s by
 * regarding {@link org.springframework.core.Ordered} interface or
 * {@link org.springframework.core.annotation.Order} annotation. To alter
 * ordering behaviour use one of the factory methods accepting a
 * {@link Comparator} as parameter.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class OrderAwarePluginRegistry<T extends Plugin<S>, S> extends
        SimplePluginRegistry<T, S> {

    /**
     * Comparator regarding {@link org.springframework.core.Ordered} interface
     * or {@link org.springframework.core.annotation.Order} annotation.
     */
    @SuppressWarnings("unchecked")
    private static final Comparator<Object> DEFAULT_COMPARATOR =
            new AnnotationAwareOrderComparator();

    /**
     * Comparator reverting the {@value #DEFAULT_COMPARATOR}.
     */
    private static final Comparator<Object> DEFAULT_REVERSE_COMPARATOR =
            new RevertingComparator<Object>(DEFAULT_COMPARATOR);

    private Comparator<? super T> comparator;


    /**
     * Creates a new {@link OrderAwarePluginRegistry} with the given
     * {@link Plugin}s and {@link Comparator}.
     * 
     * @param plugins the {@link Plugin}s to be contained in the registry or
     *            {@literal null} if the registry shall be empty initally.
     * @param comparator the {@link Comparator} to be used for ordering the
     *            {@link Plugin}s or {@literal null} if the {@code
     *            #DEFAULT_COMPARATOR} shall be used.
     */
    protected OrderAwarePluginRegistry(List<? extends T> plugins,
            Comparator<? super T> comparator) {

        super(plugins);

        this.comparator = DEFAULT_COMPARATOR;
        if (comparator != null) {
            this.comparator = comparator;
        }
    }


    /**
     * Creates a new {@link OrderAwarePluginRegistry} using the {@code
     * #DEFAULT_COMPARATOR}.
     * 
     * @param <T>
     * @param <S>
     * @return
     */
    public static <S, T extends Plugin<S>> OrderAwarePluginRegistry<T, S> create() {

        return create(null, null);
    }


    /**
     * Creates a new {@link OrderAwarePluginRegistry} using the given
     * {@link Comparator} for ordering contained {@link Plugin}s.
     * 
     * @param <T>
     * @param <S>
     * @return
     */
    public static <S, T extends Plugin<S>> OrderAwarePluginRegistry<T, S> create(
            Comparator<? super T> comparator) {

        return create(null, comparator);
    }


    /**
     * Creates a new {@link OrderAwarePluginRegistry} with the given plugins.
     * 
     * @param <S>
     * @param <T>
     * @param plugins
     * @return
     */
    public static <S, T extends Plugin<S>> OrderAwarePluginRegistry<T, S> create(
            List<? extends T> plugins) {

        return create(plugins, DEFAULT_COMPARATOR);
    }


    /**
     * Creates a new {@link OrderAwarePluginRegistry} with the given plugins and
     * the order of the plugins reverted.
     * 
     * @param <S>
     * @param <T>
     * @param plugins
     * @return
     */
    public static <S, T extends Plugin<S>> OrderAwarePluginRegistry<T, S> createReverse(
            List<? extends T> plugins) {

        return create(plugins, DEFAULT_REVERSE_COMPARATOR);
    }


    /**
     * Creates a new {@link OrderAwarePluginRegistry} with the given plugins.
     * 
     * @param <S>
     * @param <T>
     * @param plugins
     * @return
     */
    public static <S, T extends Plugin<S>> OrderAwarePluginRegistry<T, S> create(
            List<? extends T> plugins, Comparator<? super T> comparator) {

        return new OrderAwarePluginRegistry<T, S>(plugins, comparator);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hera.core.PluginRegistry#setPlugins(java.util.List)
     */
    @Override
    public void setPlugins(List<? extends T> plugins) {

        Collections.sort(plugins, comparator);
        super.setPlugins(plugins);
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hera.core.PluginRegistry#addPlugin(org.synyx.hera.core.Plugin)
     */
    @Override
    public OrderAwarePluginRegistry<T, S> addPlugin(T plugin) {

        super.addPlugin(plugin);
        Collections.sort(getPlugins(), comparator);
        return this;
    }


    /**
     * Returns a new {@link OrderAwarePluginRegistry} with the order of the
     * plugins reverted.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public OrderAwarePluginRegistry<T, S> reverse() {

        return create(getPlugins(), new RevertingComparator(comparator));
    }

    /**
     * Comparator to revert the ordering of the given delegate.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    private static final class RevertingComparator<T> implements Comparator<T> {

        private final Comparator<? super T> delegate;


        /**
         * Creates a new {@link RevertingComparator}.
         * 
         * @param delegate
         */
        public RevertingComparator(Comparator<? super T> delegate) {

            this.delegate = delegate;
        }


        /*
         * (non-Javadoc)
         * 
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(T first, T second) {

            return -1 * delegate.compare(first, second);
        }
    }
}
