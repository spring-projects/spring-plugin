/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * @author Oliver Gierke
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

	@Autowired
	SamplePlugin plugin;

	@Test
	public void test() throws Exception {

		assertNotNull(samplePlugins);

		assertSame(pluginRegistry, host.getRegistry());
		assertNotSame(pluginRegistry, otherHost.getRegistry());

		assertTrue(samplePlugins.contains(plugin));
		assertTrue(pluginRegistry.contains(plugin));
	}
}
