/*
 * Copyright 2011-2012 the original author or authors.
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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.plugin.core.OrderAwarePluginRegistry;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.integration.PluginRegistryAwareMessageHandler;
import org.springframework.plugin.integration.sample.FirstSamplePluginImpl;
import org.springframework.plugin.integration.sample.SamplePlugin;
import org.springframework.plugin.integration.sample.SecondSamplePluginImpl;

/**
 * Unit tests for {@link PluginRegistryAwareMessageHandler}.
 * 
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.class)
public class PluginRegistryAwareMessageHandlerUnitTest {

	PluginRegistry<SamplePlugin, String> registry;
	PluginRegistryAwareMessageHandler handler;

	@Mock
	MessageChannel outputChannel;

	@Before
	public void setUp() {

		registry = OrderAwarePluginRegistry
				.create(Arrays.asList(new FirstSamplePluginImpl(), new SecondSamplePluginImpl()));

		handler = new PluginRegistryAwareMessageHandler(registry, SamplePlugin.class, "myBusinessMethod");
		handler.setOutputChannel(outputChannel);
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void routesInvocationToFirstpluginIfConfiguredToDoSo() {

		handler.setDelimiterExpression("payload");
		handler.setInvocationArgumentsExpression("payload");
		handler.afterPropertiesSet();

		Message<String> message = MessageBuilder.withPayload("FOO").build();
		when(outputChannel.send(Mockito.any(Message.class))).thenReturn(true);

		handler.handleMessage(message);

		ArgumentCaptor<Message> resultMessage = ArgumentCaptor.forClass(Message.class);
		verify(outputChannel).send(resultMessage.capture());
		assertThat(resultMessage.getValue().getPayload().toString(), is("First"));
	}

	@Test(expected = MessageHandlingException.class)
	public void failsHandlingMessageIfDelimiterTypeDoesNotMatch() {

		Message<String> message = MessageBuilder.withPayload("FOO").build();
		handler.handleMessage(message);
	}

	@Test(expected = IllegalArgumentException.class)
	public void rejectsInvalidMethodName() {

		new PluginRegistryAwareMessageHandler(registry, SamplePlugin.class, "foo");
	}
}
