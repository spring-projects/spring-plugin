package org.synyx.plugin.metadata;

/**
 * Basic interface to define a set of metadata information for plugins.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface PluginMetadata {

    /**
     * Returns a unique plugin name. Plugins return a metadata implementation
     * have to ensure uniqueness of this name.
     * 
     * @return the name of the plugin
     */
    String getName();


    /**
     * Returns the plugin version. This allows rudimentary versioning
     * possibilities.
     * 
     * @return the version of the plugin
     */
    String getVersion();
}
