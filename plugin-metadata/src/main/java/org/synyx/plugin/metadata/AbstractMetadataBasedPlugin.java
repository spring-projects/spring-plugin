package org.synyx.plugin.metadata;

import org.synyx.plugin.core.Plugin;


/**
 * Abstract base class for plugins based on {@code PluginMetadata}. Plugins
 * based on this class can be selected from the {@code PluginRegistry} via an
 * instance of {@code PluginMetadata}. Therefore you can regard this as a role
 * model implementation of a base class for certain delimiter implmentations.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public abstract class AbstractMetadataBasedPlugin implements
        Plugin<PluginMetadata>, MetadataProvider {

    private PluginMetadata metadata;


    /**
     * Creates a new instance of {@code AbstractMetadataBasedPlugin}.
     * 
     * @param name
     * @param version
     */
    public AbstractMetadataBasedPlugin(String name, String version) {

        this.metadata = new SimplePluginMetadata(name, version);
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.minos.core.plugin.Plugin#supports(java.lang.Object)
     */
    public boolean supports(PluginMetadata delimiter) {

        return getMetadata().equals(delimiter);
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.minos.core.plugin.MetadataProvider#getMetadata()
     */
    public PluginMetadata getMetadata() {

        return metadata;
    }
}
