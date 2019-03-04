/*
 * Copyright 2008-2019 the original author or authors.
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

/**
 * @author Oliver Gierke
 */
public class SamplePluginHost {

	private PluginRegistry<SamplePlugin, String> registry = SimplePluginRegistry.empty();

	/**
	 * @param registry the registry to set
	 */
	public void setRegistry(PluginRegistry<SamplePlugin, String> registry) {
		this.registry = registry;
	}

	/**
	 * @return the registry
	 */
	public PluginRegistry<SamplePlugin, String> getRegistry() {
		return registry;
	}
}
