package org.synyx.hera.core;

/**
 * @author Oliver Gierke - gierke@synyx.de
 */
public class SamplePluginHost {

    private PluginRegistry<SamplePlugin, String> registry =
            PluginRegistry.create();


    /**
     * @param registry the registry to set
     */
    public void setRegistry(PluginRegistry<SamplePlugin, String> registry) {

        this.registry = registry;
    }


    /**
     * @return the registry
     */
    public PluginRegistry<SamplePlugin, String> getRegistry() {

        return registry;
    }
}
