/*
 * Copyright 2012-2021 the original author or authors.
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.SamplePlugin;
import org.springframework.plugin.core.SamplePluginImplementation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Integration tests for {@link EnablePluginRegistries}.
 *
 * @author Oliver Gierke
 */
@ContextConfiguration
@ExtendWith(SpringExtension.class)
class EnablePluginRegistriesIntegrationTest {

	@Configuration
	@EnablePluginRegistries({ SamplePlugin.class, AnotherPlugin.class })
	static class Config {

		@Bean
		public SamplePluginImplementation pluginImpl() {
			return new SamplePluginImplementation();
		}
	}

	@Autowired PluginRegistry<SamplePlugin, String> registry;

	@Test
	void registersPluginRegistries() {
		assertThat(registry).isNotNull();
	}

	@Qualifier("myQualifier")
	interface AnotherPlugin extends Plugin<String> {}

	static class AnotherSamplePluginImplementation implements AnotherPlugin {

		@Override
		public boolean supports(String delimiter) {
			return true;
		}
	}
}
