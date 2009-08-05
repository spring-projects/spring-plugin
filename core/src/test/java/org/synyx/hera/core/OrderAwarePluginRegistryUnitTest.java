package org.synyx.hera.core;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.annotation.Order;


/**
 * Unit test for {@link OrderAwarePluginRegistry} that especially concentrates
 * on testing ordering functionality.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class OrderAwarePluginRegistryUnitTest extends
        SimplePluginRegistryUnitTest {

    private PluginRegistry<TestPlugin, String> registry;

    private TestPlugin firstPlugin;
    private TestPlugin secondPlugin;


    @Before
    public void setUp() {

        super.setUp();

        registry = OrderAwarePluginRegistry.create();

        firstPlugin = new FirstImplementation();
        secondPlugin = new SecondImplementation();
    }


    /**
     * Adds the plugin implementations in order of their names, expecting the
     * registry to order them correctly.
     * 
     * @throws Exception
     */
    @Test
    public void honorsOrderOnAddPlugins() throws Exception {

        registry.setPlugins(Arrays.asList(firstPlugin, secondPlugin));

        assertOrder();
    }


    @Test
    public void assertsOrderOnAddingPlugins() throws Exception {

        registry.setPlugins(Arrays.asList(firstPlugin));
        registry.addPlugin(secondPlugin);

        assertOrder();
    }


    private void assertOrder() {

        List<TestPlugin> plugins = registry.getPluginsFor(null);

        assertEquals(2, plugins.size());
        assertEquals(secondPlugin, plugins.get(0));
        assertEquals(firstPlugin, plugins.get(1));

        assertEquals(secondPlugin, registry.getPluginFor(null));
    }

    /**
     * Simple test interface.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    private static interface TestPlugin extends Plugin<String> {

    }

    /**
     * Plugin implementation, that is orderd right AFTER the second one.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    @Order(5)
    private static class FirstImplementation implements TestPlugin {

        /*
         * (non-Javadoc)
         * 
         * @see org.synyx.hera.core.Plugin#supports(java.lang.Object)
         */
        public boolean supports(String delimiter) {

            return true;
        }
    }

    /**
     * Plugin implementation that is ordered BEFORE the first one.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    @Order(1)
    private static class SecondImplementation implements TestPlugin {

        /*
         * (non-Javadoc)
         * 
         * @see org.synyx.hera.core.Plugin#supports(java.lang.Object)
         */
        public boolean supports(String delimiter) {

            return true;
        }
    }
}
