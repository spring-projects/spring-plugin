package org.synyx.hera.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Registry for plugins. Allows sophisticated typesafe access to implementations
 * of interfaces extending {link Plugin}.
 * 
 * @param <T> the concrete plugin interface
 * @param <S> the delimiter type
 * @author Oliver Gierke - gierke@synyx.de
 */
public class PluginRegistry<T extends Plugin<S>, S> implements Iterable<T> {

    // Registered plugins
    private List<T> plugins;


    /**
     * Creates a new {@code PluginRegistry}.
     */
    public PluginRegistry() {

        plugins = new ArrayList<T>();
    }


    /**
     * Creates a new {@link PluginRegistry}.
     * 
     * @param <T>
     * @param <S>
     * @return
     */
    public static <T extends Plugin<S>, S> PluginRegistry<T, S> create() {

        return new PluginRegistry<T, S>();
    }


    /**
     * Register plugins.
     * 
     * @param plugins the plugins to set
     */
    public void setPlugins(List<? extends T> plugins) {

        this.plugins = new ArrayList<T>();
        this.plugins.addAll(plugins);
    }


    /**
     * Adds a given plugin to the registry.
     * 
     * @param plugin
     */
    public void addPlugin(T plugin) {

        this.plugins.add(plugin);
    }


    /**
     * Returns the first plugin found for the given originating system. Thus,
     * further configured plugins are ignored.
     * 
     * @param originatingSystem
     * @return a plugin for the given originating system or {@code null} if none
     *         found
     */
    public T getPluginFor(S delimiter) {

        List<T> plugins = getPluginsFor(delimiter);

        if (0 < plugins.size()) {
            return plugins.get(0);
        }

        return null;
    }


    /**
     * Returns all plugins for the given delimiter.
     * 
     * @param delimiter
     * @return a list of plugins or an empty list if none found
     */
    public List<T> getPluginsFor(S delimiter) {

        List<T> result = new ArrayList<T>();

        for (T plugin : plugins) {
            if (plugin.supports(delimiter)) {
                result.add(plugin);
            }
        }

        return result;
    }


    /**
     * Retrieves a required plugin from the registry or throw the given
     * exception if none can be found. If more than one plugins are found the
     * first one will be returned.
     * 
     * @param <E> the exception type to be thrown in case no plugin can be found
     * @param delimiter
     * @param ex the exception to be thrown in case no plugin can be found
     * @return a single plugin for the given delimiter
     * @throws E if no plugin can be found for the given delimiter
     */
    public <E extends Exception> T getPluginFor(S delimiter, E ex) throws E {

        T plugin = getPluginFor(delimiter);

        if (null == plugin) {
            throw ex;
        }

        return plugin;
    }


    /**
     * Retrieves all plugins for the given delimiter or throws an exception if
     * no plugin can be found.
     * 
     * @param <E> the exception type to be thrown
     * @param delimiter
     * @param ex
     * @return all plugins for the given delimiter
     * @throws E if no plugin can be found
     */
    public <E extends Exception> List<T> getPluginsFor(S delimiter, E ex)
            throws E {

        List<T> plugins = getPluginsFor(delimiter);

        if (0 == plugins.size()) {
            throw ex;
        }

        return plugins;
    }


    /**
     * Returns the first {@link Plugin} supporting the given delimiter or the
     * given plugin if none can be found.
     * 
     * @param delimiter
     * @param plugin
     * @return a single {@link Plugin} supporting the given delimiter or the
     *         given {@link Plugin} if none found
     */
    public T getPluginFor(S delimiter, T plugin) {

        T candidate = getPluginFor(delimiter);

        return null == candidate ? plugin : candidate;
    }


    /**
     * Returns all {@link Plugin}s supporting the given delimiter or the given
     * plugins if none found.
     * 
     * @param delimiter
     * @param plugins
     * @return all {@link Plugin}s supporting the given delimiter or the given
     *         {@link Plugin}s if none found
     */
    public List<T> getPluginsFor(S delimiter, List<T> plugins) {

        List<T> candidates = getPluginsFor(delimiter);

        return candidates.size() == 0 ? plugins : candidates;
    }


    /**
     * Returns the number of registered plugins.
     * 
     * @return the number of plugins in the registry
     */
    public int countPlugins() {

        return plugins.size();
    }


    /**
     * Returns all registered plugins. Only use this method if you really need
     * to access all plugins. For distinguished access to certain plugins favour
     * accessor methods like {link #getPluginFor} over this one. This method
     * should only be used for testing purposes to check registry configuration.
     * <p>
     * TODO: decide whether to make this method public
     * 
     * @return all plugins of the registry
     */
    @SuppressWarnings("unused")
    private List<? extends T> getPlugins() {

        return plugins;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<T> iterator() {

        return plugins.iterator();
    }
}
