/*
 * Copyright 2011 the original author or authors.
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
package org.synyx.hera.si;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.GenericTypeResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.integration.Message;
import org.springframework.integration.MessageHandlingException;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.synyx.hera.core.Plugin;
import org.synyx.hera.core.PluginRegistry;

/**
 * Dynamic service activator that uses a {@link PluginRegistry} to delegate execution to one or more plugins matching a
 * delimiter.
 * 
 * @author Oliver Gierke
 */
public class PluginRegistryAwareMessageHandler extends AbstractReplyProducingMessageHandler {

	private enum InvocationMethod {
		ONE, ALL;
	}

	private final PluginRegistry<? extends Plugin<?>, Object> registry;
	private final Class<? extends Plugin<?>> pluginType;
	private final Class<?> delimitzerType;
	private final SpelExpressionParser parser = new SpelExpressionParser();

	private Expression delimiterExpression;
	private Expression invocationArgumentsExpression;
	private String serviceMethodName;
	private InvocationMethod invocationMethod = InvocationMethod.ONE;

	/**
	 * Creates a new {@link PluginRegistryAwareMessageHandler} for the given {@link PluginRegistry}, pluginType and a
	 * method name to call.
	 * 
	 * @param registry
	 * @param pluginType
	 * @param serviceMethodName
	 */
	@SuppressWarnings("unchecked")
	public PluginRegistryAwareMessageHandler(PluginRegistry<? extends Plugin<?>, ?> registry,
			Class<? extends Plugin<?>> pluginType, String serviceMethodName) {

		Assert.notNull(registry);
		Assert.notNull(pluginType);
		Assert.hasText(serviceMethodName);

		this.registry = (PluginRegistry<? extends Plugin<?>, Object>) registry;
		this.serviceMethodName = serviceMethodName;
		this.pluginType = pluginType;
		this.delimitzerType = GenericTypeResolver.resolveTypeArgument(pluginType, Plugin.class);
	}

	/**
	 * Sets the SpEL expression to extract the delimiter from the {@link Message}.
	 * 
	 * @param delimiterExpression the delimiterExpression to set
	 */
	public void setDelimiterExpression(String expression) {
		Assert.hasText(expression);
		this.delimiterExpression = parser.parseExpression(expression);
	}

	/**
	 * Sets the SpEL expression to extract the method arguments for the actual plugin method invocation from the
	 * {@link Message}.
	 * 
	 * @param invocationArgumentsExpression the invocationArgumentsExpression to set
	 */
	public void setInvocationArgumentsExpression(String expression) {
		Assert.hasText(expression);
		this.invocationArgumentsExpression = parser.parseExpression(expression);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.integration.handler.AbstractReplyProducingMessageHandler#handleRequestMessage(org.springframework.integration.Message)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Object handleRequestMessage(Message<?> requestMessage) {

		Object delimiter = getDelimiter(requestMessage);

		switch (invocationMethod) {
		case ALL:
			return invokePlugins(registry.getPluginsFor(delimiter), requestMessage);
		case ONE:
		default:
			List<Object> results = invokePlugins(Arrays.asList(registry.getPluginFor(delimiter)), requestMessage);
			return results.isEmpty() ? null : results.get(0);
		}
	}

	private List<Object> invokePlugins(Iterable<? extends Plugin<?>> plugins, Message<?> message) {
		List<Object> results = new ArrayList<Object>();

		for (Plugin<?> plugin : plugins) {

			Object[] invocationArguments = getInvocationArguments(message);
			Class<?>[] types = getTypes(invocationArguments);

			Method businessMethod = ReflectionUtils.findMethod(pluginType, serviceMethodName, types);

			if (businessMethod == null) {
				throw new MessageHandlingException(message, String.format(
						"Did not find a method %s on %s taking the following parameters %s", serviceMethodName,
						pluginType.getName(), Arrays.toString(types)));
			}

			Object result = ReflectionUtils.invokeMethod(businessMethod, plugin, invocationArguments);

			if (!businessMethod.getReturnType().equals(void.class)) {
				results.add(result);
			}
		}

		return results;
	}

	/**
	 * Returns the delimiter object to be used for the given {@link Message}. Will use the configured delimiter expression
	 * if configured.
	 * 
	 * @param message
	 * @return
	 */
	private Object getDelimiter(Message<?> message) {

		Object delimiter = message;

		if (delimiterExpression != null) {
			StandardEvaluationContext context = new StandardEvaluationContext(message);
			delimiter = delimiterExpression.getValue(context);
		}

		Assert.isInstanceOf(delimitzerType, delimiter, String.format("Delimiter expression did "
				+ "not return a suitable delimiter! Make sure the expression evaluates to a suitable "
				+ "type! Got %s but need %s", delimiter.getClass(), delimitzerType));

		return delimiter;
	}

	/**
	 * Returns the actual arguments to be used for the plugin method invocation. Will apply the configured invocation
	 * argument expression to the given {@link Message}.
	 * 
	 * @param message
	 * @return
	 */
	private Object[] getInvocationArguments(Message<?> message) {

		if (invocationArgumentsExpression == null) {
			return new Object[] { message };
		}

		StandardEvaluationContext context = new StandardEvaluationContext(message);
		Object result = delimiterExpression.getValue(context);

		return ObjectUtils.isArray(result) ? ObjectUtils.toObjectArray(result) : new Object[] { result };
	}

	/**
	 * Returns an array of types for the given objects. Inspects each element of the array for its type. will return
	 * {@literal null} for {@literal null} source values.
	 * 
	 * @param source
	 * @return
	 */
	private Class<?>[] getTypes(Object[] source) {
		Class<?>[] result = new Class<?>[source.length];
		for (int i = 0; i < source.length; i++) {
			Object sourceElement = source[i];
			result[i] = sourceElement == null ? null : sourceElement.getClass();
		}
		return result;
	}
}
