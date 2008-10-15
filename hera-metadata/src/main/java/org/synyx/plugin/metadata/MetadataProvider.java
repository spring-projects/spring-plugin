package org.synyx.plugin.metadata;

/**
 * Interface for plugins providing metadata information. Usually the plugins
 * will implement this interface themselves.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface MetadataProvider {

    /**
     * Returns the plugins metadata.
     * 
     * @return the plugins metadata
     */
    public PluginMetadata getMetadata();
}
