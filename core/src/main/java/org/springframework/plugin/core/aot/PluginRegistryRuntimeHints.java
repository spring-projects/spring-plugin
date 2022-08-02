/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.plugin.core.aot;

import java.util.List;

import org.springframework.aop.SpringProxy;
import org.springframework.aop.framework.Advised;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.core.DecoratingProxy;
import org.springframework.plugin.core.support.AbstractTypeAwareSupport;

/**
 * Registers proxy runtime hints to make sure {@link AbstractTypeAwareSupport} can create a {@link List} proxy as
 * needed.
 *
 * @author Oliver Drotbohm
 * @since 3.0
 */
class PluginRegistryRuntimeHints implements RuntimeHintsRegistrar {

	/*
	 * (non-Javadoc)
	 * @see org.springframework.aot.hint.RuntimeHintsRegistrar#registerHints(org.springframework.aot.hint.RuntimeHints, java.lang.ClassLoader)
	 */
	@Override
	public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
		hints.proxies().registerJdkProxy(List.class, SpringProxy.class, Advised.class, DecoratingProxy.class);
	}
}
