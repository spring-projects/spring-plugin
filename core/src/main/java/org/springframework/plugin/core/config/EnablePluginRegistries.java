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
package org.springframework.plugin.core.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.core.PluginRegistry;

/**
 * Enables exposure of {@link PluginRegistry} instances for the configured {@link Plugin} types
 * 
 * @see #value()
 * @author Oliver Gierke
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(PluginRegistriesBeanDefinitionRegistrar.class)
public @interface EnablePluginRegistries {

	/**
	 * The {@link Plugin} types to register {@link PluginRegistry} instances for. The registries will be named after the
	 * uncapitalized plugin type extended with {@code Registry}. So for a plugin interface {@code SamplePlugin} the
	 * exposed bean name will be {@code samplePluginRegistry}. This can be used on the client side to make sure you get
	 * the right {@link PluginRegistry} injected by using the {@link Qualifier} annotation and referring to that bean
	 * name. If the auto-generated bean name collides with one already in your application you can use the
	 * {@link Qualifier} annotation right at the plugin interface to define a custom name.
	 * 
	 * @return
	 */
	Class<? extends Plugin<?>>[] value();
}
