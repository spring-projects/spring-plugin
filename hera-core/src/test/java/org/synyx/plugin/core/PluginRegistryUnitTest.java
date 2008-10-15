package org.synyx.plugin.core;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for {@link PluginRegistry}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class PluginRegistryUnitTest {

    private SamplePlugin provider;

    private PluginRegistry<SamplePlugin, String> registry;


    /**
     * Initializes a {@code PluginRegistry} and equips it with an {@code
     * EmailNotificationProvider}.
     */
    @Before
    public void setUp() {

        provider = new SamplePluginImplementation();

        registry = PluginRegistry.create();
        registry.setPlugins(Arrays.asList(provider));
    }


    /**
     * Asserts asking for a plugin with the {@code PluginMetadata} provided by
     * the {@link EmailNotificationProvider}.
     */
    @Test
    public void assertFindsEmailNotificationProvider() {

        String metadata = "FOO";

        List<SamplePlugin> plugins = registry.getPluginsFor(metadata);
        Assert.assertNotNull(plugins);
        Assert.assertEquals(1, plugins.size());

        SamplePlugin provider = plugins.get(0);
        Assert.assertTrue(provider instanceof SamplePluginImplementation);
    }
}
