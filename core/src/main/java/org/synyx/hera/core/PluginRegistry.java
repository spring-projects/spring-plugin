package org.synyx.hera.core;

import java.util.List;


/**
 * Registry for plugins. Allows sophisticated typesafe access to implementations
 * of interfaces extending {link Plugin}.
 * 
 * @param <T> the concrete plugin interface
 * @param <S> the delimiter type
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface PluginRegistry<T extends Plugin<S>, S> extends Iterable<T> {

    /**
     * Returns the first plugin found for the given originating system. Thus,
     * further configured plugins are ignored.
     * 
     * @param originatingSystem
     * @return a plugin for the given originating system or {@code null} if none
     *         found
     */
    T getPluginFor(S delimiter);


    /**
     * Returns all plugins for the given delimiter.
     * 
     * @param delimiter
     * @return a list of plugins or an empty list if none found
     */
    List<T> getPluginsFor(S delimiter);


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
    <E extends Exception> T getPluginFor(S delimiter, E ex) throws E;


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
    <E extends Exception> List<T> getPluginsFor(S delimiter, E ex) throws E;


    /**
     * Returns the first {@link Plugin} supporting the given delimiter or the
     * given plugin if none can be found.
     * 
     * @param delimiter
     * @param plugin
     * @return a single {@link Plugin} supporting the given delimiter or the
     *         given {@link Plugin} if none found
     */
    T getPluginFor(S delimiter, T plugin);


    /**
     * Returns all {@link Plugin}s supporting the given delimiter or the given
     * plugins if none found.
     * 
     * @param delimiter
     * @param plugins
     * @return all {@link Plugin}s supporting the given delimiter or the given
     *         {@link Plugin}s if none found
     */
    List<? extends T> getPluginsFor(S delimiter, List<? extends T> plugins);


    /**
     * Returns the number of registered plugins.
     * 
     * @return the number of plugins in the registry
     */
    int countPlugins();


    /**
     * Returns whether the registry contains a given plugin.
     * 
     * @param plugin
     * @return
     */
    boolean contains(T plugin);


    /**
     * Returns whether the registry contains a {@link Plugin} matching the given
     * delimiter.
     * 
     * @param delimiter
     * @return
     */
    boolean hasPluginFor(S delimiter);
}