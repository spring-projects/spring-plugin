package org.synyx.hera.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;


/**
 * {@link PluginRegistry} implementation that can handle {@link Plugin}s using
 * the {@link org.springframework.core.Ordered} interface or
 * {@link org.springframework.core.annotation.Order} annotation.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class OrderAwarePluginRegistry<T extends Plugin<S>, S> extends
        SimplePluginRegistry<T, S> {

    @SuppressWarnings("unchecked")
    private static final Comparator<Object> DEFAULT_COMPARATOR =
            new AnnotationAwareOrderComparator();

    private static final Comparator<Object> REVERSE_COMPARATOR =
            new Comparator<Object>() {

                public int compare(Object o1, Object o2) {

                    return -1 * DEFAULT_COMPARATOR.compare(o1, o2);
                }
            };

    private Comparator<Object> comparator;


    protected OrderAwarePluginRegistry(Comparator<Object> comparator) {

        this.comparator = comparator;
    }


    /**
     * Creates a new {@link SimplePluginRegistry}.
     * 
     * @param <T>
     * @param <S>
     * @return
     */
    public static <S, T extends Plugin<S>> OrderAwarePluginRegistry<T, S> create() {

        return new OrderAwarePluginRegistry<T, S>(DEFAULT_COMPARATOR);
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

        OrderAwarePluginRegistry<T, S> registry = create();
        registry.setPlugins(plugins);

        return registry;
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

        OrderAwarePluginRegistry<T, S> registry =
                new OrderAwarePluginRegistry<T, S>(REVERSE_COMPARATOR);
        registry.setPlugins(plugins);

        return registry;
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
    public void addPlugin(T plugin) {

        super.addPlugin(plugin);
        Collections.sort(getPlugins(), comparator);
    }


    /**
     * Returns a new {@link OrderAwarePluginRegistry} with the order of the
     * plugins reverted.
     * 
     * @return
     */
    public OrderAwarePluginRegistry<T, S> reverse() {

        return createReverse(getPlugins());
    }
}
