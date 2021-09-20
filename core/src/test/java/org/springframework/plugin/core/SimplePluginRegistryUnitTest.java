/*
 * Copyright 2008-2017 the original author or authors.
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
package org.springframework.plugin.core;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link SimplePluginRegistry}.
 *
 * @author Oliver Gierke
 */
class SimplePluginRegistryUnitTest {

	SamplePlugin plugin;

	SimplePluginRegistry<SamplePlugin, String> registry;

	/**
	 * Initializes a {@code PluginRegistry} and equips it with an {@code EmailNotificationProvider}.
	 */
	@BeforeEach
	void setUp() {

		plugin = new SamplePluginImplementation();
		registry = SimplePluginRegistry.empty();
	}

	/**
	 * Asserts that the registry contains the plugin it was initialized with.
	 *
	 * @throws Exception
	 */
	@Test
	void assertRegistryInitialized() throws Exception {

		registry = SimplePluginRegistry.of(plugin);

		assertThat(registry.countPlugins()).isEqualTo(1);
		assertThat(registry.contains(plugin)).isTrue();
	}

	/**
	 * Asserts asking for a plugin with the {@code PluginMetadata} provided by the {@link EmailNotificationProvider}.
	 */
	@Test
	void assertFindsEmailNotificationProvider() {

		registry = SimplePluginRegistry.of(plugin);

		String delimiter = "FOO";

		List<SamplePlugin> plugins = registry.getPluginsFor(delimiter);
		assertThat(plugins).isNotNull();
		assertThat(plugins).hasSize(1);

		SamplePlugin provider = plugins.get(0);
		assertThat(provider).isInstanceOf(SamplePluginImplementation.class);
	}

	/**
	 * Expects the given exception to be thrown if no {@link Plugin} found.
	 */
	@Test
	void throwsExceptionIfNoPluginFound() {

		assertThatIllegalArgumentException()
				.isThrownBy(() -> registry.getPluginFor("BAR", () -> new IllegalArgumentException()));
	}

	/**
	 * Expects the given exception to be thrown if no {@link Plugin}s found.
	 */
	@Test
	void throwsExceptionIfNoPluginsFound() {

		assertThatIllegalArgumentException()
				.isThrownBy(() -> registry.getPluginsFor("BAR", () -> new IllegalArgumentException()));
	}

	/**
	 * Expect the defualt plugin to be returned if none found.
	 */
	@Test
	void returnsDefaultIfNoneFound() {

		SamplePlugin defaultPlugin = new SamplePluginImplementation();

		assertThat(registry.getPluginOrDefaultFor("BAR", defaultPlugin)).isEqualTo(defaultPlugin);
	}

	/**
	 * Expect the given default plugins to be returned if none found.
	 */
	@Test
	void returnsDefaultsIfNoneFound() {

		List<? extends SamplePlugin> defaultPlugins = Arrays.asList(new SamplePluginImplementation());

		List<SamplePlugin> result = registry.getPluginsFor("BAR", defaultPlugins);
		assertThat(result).containsAll(defaultPlugins);
	}

	@Test
	void handlesAddingNullPluginsCorrecty() throws Exception {

		List<SamplePlugin> plugins = new ArrayList<SamplePlugin>();
		plugins.add(null);

		registry = SimplePluginRegistry.of(plugins);

		assertThat(registry.countPlugins()).isEqualTo(0);
	}

	@Test // #19
	void throwsExceptionFromSupplier() throws Exception {

		registry = SimplePluginRegistry.empty();

		assertThatIllegalStateException()
				.isThrownBy(() -> registry.getPluginFor("FOO", () -> new IllegalStateException()));
	}

	@Test // #41
	void throwsExceptionIfRequiredPluginIsNotFound() {

		registry = SimplePluginRegistry.empty();

		assertThatIllegalArgumentException()
				.isThrownBy(() -> registry.getRequiredPluginFor("FOO"));
	}

	@Test // #41
	void throwsExceptionWithMessafeIfRequiredPluginIsNotFound() {

		registry = SimplePluginRegistry.of(Collections.emptyList());

		assertThatIllegalArgumentException()
				.isThrownBy(() -> registry.getRequiredPluginFor("FOO", () -> "message"))
				.withMessage("message");
	}
}
