/*
 * Copyright 2011-2014 the original author or authors.
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
package org.springframework.plugin.integration;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.plugin.core.OrderAwarePluginRegistry;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.integration.sample.FirstSamplePluginImpl;
import org.springframework.plugin.integration.sample.SamplePlugin;
import org.springframework.plugin.integration.sample.SecondSamplePluginImpl;

/**
 * Unit tests for {@link PluginMethodInvocationService}.
 *
 * @author Oliver Gierke
 * @author Artem Bilan
 */
public class PluginRegistryAwareMessageHandlerUnitTest {

	PluginRegistry<SamplePlugin, String> registry;

	PluginMethodInvocationService handler;

	@Before
	public void setUp() {

		registry = OrderAwarePluginRegistry
				.create(Arrays.asList(new FirstSamplePluginImpl(), new SecondSamplePluginImpl()));

		handler = new PluginMethodInvocationService(registry, SamplePlugin.class, "myBusinessMethod");
		handler.setIntegrationEvaluationContext(new StandardEvaluationContext());
	}

	@Test
	public void routesInvocationToFirstPluginIfConfiguredToDoSo() {

		handler.setDelimiterExpression("payload");
		handler.setInvocationArgumentsExpression("payload");

		Object result = handler.invoke(MessageBuilder.withPayload("FOO").build());
		assertEquals("First", result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsHandlingMessageIfDelimiterTypeDoesNotMatch() {
		handler.invoke(MessageBuilder.withPayload("FOO").build());
	}

	@Test(expected = IllegalArgumentException.class)
	public void rejectsInvalidMethodName() {
		new PluginMethodInvocationService(registry, SamplePlugin.class, "foo");
	}

}
