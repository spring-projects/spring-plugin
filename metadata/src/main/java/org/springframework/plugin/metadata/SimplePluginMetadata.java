/*
 * Copyright 2008-2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.springframework.plugin.metadata;

import static org.springframework.util.ObjectUtils.*;

import org.springframework.util.Assert;

/**
 * Value object style implementation of {@code PluginMetadata}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class SimplePluginMetadata implements PluginMetadata {

	private final String name;
	private final String version;

	/**
	 * Creates a new instance of {@code SimplePluginMetadata}.
	 * 
	 * @param name must not be {@literal null}.
	 * @param version must not be {@literal null}.
	 */
	public SimplePluginMetadata(String name, String version) {

		Assert.hasText(name, "Name must not be null or empty!");
		Assert.hasText(version, "Version must not be null or empty!");

		this.name = name;
		this.version = version;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.plugin.metadata.PluginMetadata#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.plugin.metadata.PluginMetadata#getVersion()
	 */
	public String getVersion() {
		return version;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s:%s", getName(), getVersion());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof PluginMetadata)) {
			return false;
		}

		PluginMetadata that = (PluginMetadata) obj;

		boolean sameName = nullSafeEquals(this.getName(), that.getName());
		boolean sameVersion = nullSafeEquals(this.getName(), that.getName());

		return sameName && sameVersion;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return nullSafeHashCode(name) + nullSafeHashCode(version);
	}
}
