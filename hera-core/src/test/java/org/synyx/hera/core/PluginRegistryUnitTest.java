/*
 * Copyright 2002-2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.synyx.hera.core;

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

        registry = new PluginRegistry<SamplePlugin, String>();
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
