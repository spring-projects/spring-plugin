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

package org.springframework.plugin.metadata;

/**
 * Basic interface to define a set of metadata information for plugins.
 * 
 * @author Oliver Gierke
 */
public interface PluginMetadata {

	/**
	 * Returns a unique plugin name. Plugins return a metadata implementation have to ensure uniqueness of this name.
	 * 
	 * @return the name of the plugin
	 */
	String getName();

	/**
	 * Returns the plugin version. This allows rudimentary versioning possibilities.
	 * 
	 * @return the version of the plugin
	 */
	String getVersion();
}
