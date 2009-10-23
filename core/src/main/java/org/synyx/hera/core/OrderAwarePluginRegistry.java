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
    private Comparator<T> comparator = new AnnotationAwareOrderComparator();


    /**
     * Creates a new {@link SimplePluginRegistry}.
     * 
     * @param <T>
     * @param <S>
     * @return
     */
    public static <S, T extends Plugin<S>> PluginRegistry<T, S> create() {

        return new OrderAwarePluginRegistry<T, S>();
    }


    /**
     * Creates a new {@link OrderAwarePluginRegistry} with the given plugins.
     * 
     * @param <S>
     * @param <T>
     * @param plugins
     * @return
     */
    public static <S, T extends Plugin<S>> PluginRegistry<T, S> create(
            List<T> plugins) {

        PluginRegistry<T, S> registry = create();
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
}
