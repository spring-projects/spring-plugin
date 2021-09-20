/*
 * Copyright 2015-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.plugin.metadata;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link SimplePluginMetadata}.
 *
 * @author Oliver Gierke
 */
class SimplePluginMetadataUnitTest {

	/**
	 * @see #11
	 */
	@Test
	void equalsIsWorkingCorrectly() {

		SimplePluginMetadata nameOneOh = new SimplePluginMetadata("Name", "1.0");
		SimplePluginMetadata sameNameOneOh = new SimplePluginMetadata("Name", "1.0");
		SimplePluginMetadata nameTwoOh = new SimplePluginMetadata("Name", "2.0");
		SimplePluginMetadata anotherNameOneOh = new SimplePluginMetadata("AnotherName", "1.0");

		assertThat(nameOneOh).isEqualTo(nameOneOh);
		assertThat(nameOneOh).isEqualTo(sameNameOneOh);
		assertThat(sameNameOneOh).isEqualTo(nameOneOh);

		assertThat(nameOneOh).isNotEqualTo(nameTwoOh);
		assertThat(nameTwoOh).isNotEqualTo(nameOneOh);

		assertThat(nameOneOh).isNotEqualTo(anotherNameOneOh);
		assertThat(anotherNameOneOh).isNotEqualTo(nameOneOh);
	}
}
