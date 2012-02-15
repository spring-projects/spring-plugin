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
package org.synyx.hera.core;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.annotation.Order;

/**
 * Unit test for {@link OrderAwarePluginRegistry} that especially concentrates on testing ordering functionality.
 * 
 * @author Oliver Gierke
 */
public class OrderAwarePluginRegistryUnitTest extends SimplePluginRegistryUnitTest {

	TestPlugin firstPlugin;
	TestPlugin secondPlugin;

	@Override
	@Before
	public void setUp() {

		super.setUp();

		firstPlugin = new FirstImplementation();
		secondPlugin = new SecondImplementation();
	}

	@Override
	protected OrderAwarePluginRegistry<SamplePlugin, String> getRegistry() {

		return OrderAwarePluginRegistry.create();
	}

	@Test
	public void honorsOrderOnAddPlugins() throws Exception {

		PluginRegistry<TestPlugin, String> registry = OrderAwarePluginRegistry.create(Arrays.asList(firstPlugin,
				secondPlugin));
		assertOrder(registry, secondPlugin, firstPlugin);
	}

	@Test
	@Ignore
	public void assertsOrderOnAddingPlugins() throws Exception {

		MutablePluginRegistry<TestPlugin, String> registry = OrderAwarePluginRegistry.create(Arrays.asList(firstPlugin));
		registry.addPlugin(secondPlugin);

		assertOrder(registry, secondPlugin, firstPlugin);
	}

	private void assertOrder(PluginRegistry<TestPlugin, String> registry, TestPlugin... plugins) {

		List<TestPlugin> result = registry.getPluginsFor(null);

		assertThat(plugins.length, is(result.size()));

		for (int i = 0; i < plugins.length; i++) {
			assertThat(result.get(i), is(result.get(i)));
		}

		assertThat(registry.getPluginFor(null), is(plugins[0]));
	}

	@Test
	public void createsRevertedRegistryCorrectly() throws Exception {

		OrderAwarePluginRegistry<TestPlugin, String> registry = OrderAwarePluginRegistry.create(Arrays.asList(firstPlugin,
				secondPlugin));
		PluginRegistry<TestPlugin, String> reverse = registry.reverse();

		assertOrder(registry, secondPlugin, firstPlugin);
		assertOrder(reverse, firstPlugin, secondPlugin);
	}

	private static interface TestPlugin extends Plugin<String> {

	}

	@Order(5)
	private static class FirstImplementation implements TestPlugin {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.synyx.hera.core.Plugin#supports(java.lang.Object)
		 */
		public boolean supports(String delimiter) {

			return true;
		}
	}

	@Order(1)
	private static class SecondImplementation implements TestPlugin {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.synyx.hera.core.Plugin#supports(java.lang.Object)
		 */
		public boolean supports(String delimiter) {

			return true;
		}
	}
}
