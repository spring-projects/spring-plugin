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
package org.springframework.plugin.core;

/**
 * Central interface for plugins for the system. This interface is meant to be extended by concrete plugin interfaces.
 * Its core responsibility is to define a delimiter type and a selection callback with the delimiter as parameter. The
 * delimiter is some kind of decision object concrete plugin implementations can use to decide if they are capable to be
 * executed.
 * 
 * @author Oliver Gierke
 */
public interface Plugin<S> {

	/**
	 * Returns if a plugin should be invoked according to the given delimiter.
	 * 
	 * @param delimiter
	 * @return if the plugin should be invoked
	 */
	boolean supports(S delimiter);
}
