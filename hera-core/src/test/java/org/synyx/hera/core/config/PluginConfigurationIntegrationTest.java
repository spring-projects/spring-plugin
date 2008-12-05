package org.synyx.hera.core.config;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.synyx.hera.core.PluginRegistry;
import org.synyx.hera.core.SamplePlugin;
import org.synyx.hera.core.SamplePluginHost;


/**
 * Integration test to simply check if the configuration gets parsed correctly.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
public class PluginConfigurationIntegrationTest {

    @Autowired
    List<SamplePlugin> samplePlugins;

    @Autowired
    @Qualifier("bar")
    PluginRegistry<SamplePlugin, String> pluginRegistry;

    @Autowired
    @Qualifier("host")
    SamplePluginHost host;

    @Autowired
    @Qualifier("otherHost")
    SamplePluginHost otherHost;


    @Test
    public void test() throws Exception {

        assertSame(pluginRegistry, host.getRegistry());
        assertNotSame(pluginRegistry, otherHost.getRegistry());
    }
}
