/*
 * Copyright 2012 the original author or authors.
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
package org.springframework.plugin.metadata;

import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.core.PluginRegistry;

/**
 * Abstract base class for plugins based on {@link PluginMetadata}. Plugins based on this class can be selected from the
 * {@link PluginRegistry} via an instance of {@link PluginMetadata}. Therefore you can regard this as a role model
 * implementation of a base class for certain delimiter implmentations.
 * 
 * @author Oliver Gierke
 */
public abstract class AbstractMetadataBasedPlugin implements Plugin<PluginMetadata>, MetadataProvider {

	private final PluginMetadata metadata;

	/**
	 * Creates a new instance of {@code AbstractMetadataBasedPlugin}.
	 * 
	 * @param name must not be {@literal null}.
	 * @param version must not be {@literal null}.
	 */
	public AbstractMetadataBasedPlugin(String name, String version) {
		this.metadata = new SimplePluginMetadata(name, version);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.plugin.core.Plugin#supports(java.lang.Object)
	 */
	public boolean supports(PluginMetadata delimiter) {
		return getMetadata().equals(delimiter);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.plugin.metadata.MetadataProvider#getMetadata()
	 */
	public PluginMetadata getMetadata() {
		return metadata;
	}
}
