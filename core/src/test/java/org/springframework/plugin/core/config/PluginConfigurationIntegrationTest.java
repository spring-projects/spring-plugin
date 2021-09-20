/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.plugin.core.config;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.SamplePlugin;
import org.springframework.plugin.core.SamplePluginHost;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Integration test to simply check if the configuration gets parsed correctly.
 *
 * @author Oliver Gierke
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
class PluginConfigurationIntegrationTest {

	@Autowired List<SamplePlugin> samplePlugins;

	@Autowired @Qualifier("bar") PluginRegistry<SamplePlugin, String> pluginRegistry;

	@Autowired @Qualifier("host") SamplePluginHost host;

	@Autowired @Qualifier("otherHost") SamplePluginHost otherHost;

	@Autowired SamplePlugin plugin;

	@Test
	void test() throws Exception {

		assertThat(samplePlugins).isNotNull();

		assertThat(pluginRegistry).isSameAs(host.getRegistry());
		assertThat(pluginRegistry).isNotSameAs(otherHost.getRegistry());

		assertThat(samplePlugins).contains(plugin);
		assertThat(pluginRegistry).contains(plugin);
	}
}
