/*
 * Copyright 2026 the original author or authors.
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
package org.springframework.plugin.autoconfigure;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Component;

/**
 * Tests for {@link PluginRegistriesAutoConfiguration}.
 *
 * @author Gabriel Hall
 */
class PluginRegistriesAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withConfiguration(AutoConfigurations.of(PluginRegistriesAutoConfiguration.class));

	@Test
	void registersRegistryForPluginBean() {

		this.contextRunner.withUserConfiguration(ComponentScanConfiguration.class).run(context -> {

			PluginRegistry<SamplePlugin, String> registry = context.getBean("samplePluginRegistry", PluginRegistry.class);

			assertThat(registry).contains(context.getBean(SamplePlugin.class));
		});
	}

	@Test
	void registersRegistryForDeclaredPluginType() {

		this.contextRunner.withUserConfiguration(DeclaredPluginConfiguration.class).run(context -> {

			PluginRegistry<DeclaredPlugin, String> registry = context.getBean("declaredPluginRegistry",
					PluginRegistry.class);

			assertThat(registry).contains(context.getBean(DeclaredPlugin.class));
		});
	}

	@Test
	void preservesExplicitRegistry() {

		this.contextRunner.withUserConfiguration(ExplicitRegistryConfiguration.class).run(context -> {

			PluginRegistry<SamplePlugin, String> registry = context.getBean("customRegistry", PluginRegistry.class);

			assertThat(registry.countPlugins()).isZero();
			assertThat(context).doesNotHaveBean("samplePluginRegistry");
		});
	}

	@Test
	void usesPluginInterfaceQualifier() {

		this.contextRunner.withUserConfiguration(QualifiedPluginConfiguration.class).run(context -> {

			PluginRegistry<QualifiedPlugin, String> registry = context.getBean("qualifiedRegistry", PluginRegistry.class);

			assertThat(registry).contains(context.getBean(QualifiedPlugin.class));
		});
	}

	@Configuration(proxyBeanMethods = false)
	@ComponentScan(basePackageClasses = SamplePluginImplementation.class, useDefaultFilters = false, includeFilters = @ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE, classes = SamplePluginImplementation.class))
	static class ComponentScanConfiguration {}

	@Configuration(proxyBeanMethods = false)
	static class ExplicitRegistryConfiguration {

		@Bean
		SamplePluginImplementation samplePlugin() {
			return new SamplePluginImplementation();
		}

		@Bean
		PluginRegistry<SamplePlugin, String> customRegistry() {
			return PluginRegistry.empty();
		}
	}

	@Configuration(proxyBeanMethods = false)
	static class QualifiedPluginConfiguration {

		@Bean
		QualifiedPluginImplementation qualifiedPlugin() {
			return new QualifiedPluginImplementation();
		}
	}

	interface SamplePlugin extends Plugin<String> {}

	@Qualifier("qualifiedRegistry")
	interface QualifiedPlugin extends Plugin<String> {}

	interface DeclaredPlugin extends Plugin<String> {}

	@Configuration(proxyBeanMethods = false)
	static class DeclaredPluginConfiguration {

		@Bean
		DeclaredPlugin declaredPlugin() {
			return delimiter -> true;
		}
	}

	@Component
	static class SamplePluginImplementation implements SamplePlugin {

		@Override
		public boolean supports(String delimiter) {
			return true;
		}
	}

	static class QualifiedPluginImplementation implements QualifiedPlugin {

		@Override
		public boolean supports(String delimiter) {
			return true;
		}
	}
}
