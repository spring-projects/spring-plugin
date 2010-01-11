/*
 * Copyright 2008-2010 the original author or authors.
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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;


/**
 * Unit test for implementations of {@link MutablePluginRegistry}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public abstract class AbstractMutablePluginRegistryUnitTest {

    private SamplePlugin plugin = new SamplePluginImplementation();


    @Test
    public void allowsAddingPluginsAfterCreation() throws Exception {

        MutablePluginRegistry<SamplePlugin, String> registry = getRegistry();
        registry.addPlugin(plugin);

        assertTrue(registry.contains(plugin));
        assertThat(registry.getPlugins().size(), is(1));
    }


    @Test
    public void settingPluginsRemovesOldOnes() throws Exception {

        MutablePluginRegistry<SamplePlugin, String> registry = getRegistry();
        registry.addPlugin(plugin);

        SamplePlugin anotherPlugin = new SamplePluginImplementation();

        registry.setPlugins(Arrays.asList(anotherPlugin));
        assertTrue(registry.contains(anotherPlugin));
        assertFalse(registry.contains(plugin));
    }


    /**
     * Return the {@link MutablePluginRegistry} to test.
     * 
     * @return
     */
    protected abstract MutablePluginRegistry<SamplePlugin, String> getRegistry();
}
