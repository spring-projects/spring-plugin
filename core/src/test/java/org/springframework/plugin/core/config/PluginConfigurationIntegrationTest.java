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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.SamplePlugin;
import org.springframework.plugin.core.SamplePluginHost;
import org.springframework.plugin.core.SamplePluginImplementation;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Integration test to simply check if the configuration gets parsed correctly.
 *
 * @author Oliver Gierke
 */
@ExtendWith(SpringExtension.class)
class PluginConfigurationIntegrationTest {

	@Configuration
	@EnablePluginRegistries(SamplePlugin.class)
	static class Config {

		@Bean
		SamplePluginHost samplePluginHost(PluginRegistry<SamplePlugin, String> registry) {

			var host = new SamplePluginHost();
			host.setRegistry(registry);

			return host;
		}

		@Bean
		SamplePluginImplementation samplePluginImplementation() {
			return new SamplePluginImplementation();
		}
	}

	@Autowired PluginRegistry<SamplePlugin, String> pluginRegistry;
	@Autowired SamplePluginHost host;
	@Autowired SamplePlugin plugin;

	@Test
	void test() throws Exception {

		assertThat(pluginRegistry).isSameAs(host.getRegistry());
		assertThat(pluginRegistry).contains(plugin);
	}
}
