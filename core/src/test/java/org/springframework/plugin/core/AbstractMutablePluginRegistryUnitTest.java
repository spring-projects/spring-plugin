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
package org.springframework.plugin.core;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.plugin.core.MutablePluginRegistry;

/**
 * Unit test for implementations of {@link MutablePluginRegistry}.
 * 
 * @author Oliver Gierke
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

	/**
	 * Return the {@link MutablePluginRegistry} to test.
	 * 
	 * @return
	 */
	protected abstract MutablePluginRegistry<SamplePlugin, String> getRegistry();
}
