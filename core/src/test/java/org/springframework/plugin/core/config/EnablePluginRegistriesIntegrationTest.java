/*
 * Copyright 2012-2016 the original author or authors.
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
package org.springframework.plugin.core.config;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.SamplePlugin;
import org.springframework.plugin.core.SamplePluginImplementation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration tests for {@link EnablePluginRegistries}.
 * 
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class EnablePluginRegistriesIntegrationTest {

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
	public void registersPluginRegistries() {
		assertThat(registry, is(notNullValue()));
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
