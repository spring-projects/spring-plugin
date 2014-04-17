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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.plugin.core.OrderAwarePluginRegistry;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Dynamic service activator that uses a {@link PluginRegistry} to delegate execution to one or more plugins matching a
 * delimiter.
 * 
 * @author Oliver Gierke
 */
public class PluginRegistryAwareMessageHandler extends AbstractReplyProducingMessageHandler {

	private static final Log LOG = LogFactory.getLog(PluginRegistryAwareMessageHandler.class);

	private final PluginRegistry<? extends Plugin<?>, Object> registry;
	private final Class<? extends Plugin<?>> pluginType;
	private final Class<?> delimiterType;
	private final SpelExpressionParser parser = new SpelExpressionParser();

	private Expression delimiterExpression;
	private Expression invocationArgumentsExpression;
	private String serviceMethodName;
	private PluginLookupMethod pluginLookupMethod = PluginLookupMethod.getDefault();

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
		this.delimiterType = GenericTypeResolver.resolveTypeArgument(pluginType, Plugin.class);

		verify();
	}

	private final void verify() {

		boolean methodFound = false;

		for (Method candidate : pluginType.getMethods()) {
			if (candidate.getName().equals(serviceMethodName)) {
				methodFound = true;
				break;
			}
		}

		if (!methodFound) {
			throw new IllegalArgumentException(String.format("Not method %s found for type %s!", serviceMethodName,
					pluginType));
		}
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

	/**
	 * Configures the method to be used when looking up plugins to invoke.
	 * 
	 * @see PluginLookupMethod
	 * @param pluginLookupMethod the invocationMethod to set
	 */
	public void setPluginLookupMethod(PluginLookupMethod pluginLookupMethod) {
		this.pluginLookupMethod = pluginLookupMethod == null ? PluginLookupMethod.getDefault() : pluginLookupMethod;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.integration.handler.AbstractReplyProducingMessageHandler
	 * #handleRequestMessage(org.springframework.integration.Message)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Object handleRequestMessage(Message<?> requestMessage) {

		Object delimiter = getDelimiter(requestMessage);

		switch (pluginLookupMethod) {

		case ALL:
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Looking up plugins for delimiter %s", delimiter));
			}
			return invokePlugins(registry.getPluginsFor(delimiter), requestMessage);

		case ONE:
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Looking up plugin for delimiter %s", delimiter));
			}
			List<Object> results = invokePlugins(Arrays.asList(registry.getPluginFor(delimiter)), requestMessage);
			return results.isEmpty() ? null : results.get(0);

		default:
			throw new IllegalStateException(String.format("Unsupported plugin lookup method %s!", pluginLookupMethod));
		}
	}

	private List<Object> invokePlugins(Collection<? extends Plugin<?>> plugins, Message<?> message) {

		List<Object> results = new ArrayList<Object>();
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("Invoking plugin(s) %s with message %s",
					StringUtils.collectionToCommaDelimitedString(plugins), message));
		}

		for (Plugin<?> plugin : plugins) {

			Object[] invocationArguments = getInvocationArguments(message);
			Class<?>[] types = getTypes(invocationArguments);

			Method businessMethod = ReflectionUtils.findMethod(pluginType, serviceMethodName, types);

			if (businessMethod == null) {
				throw new MessageHandlingException(message, String.format(
						"Did not find a method %s on %s taking the following parameters %s", serviceMethodName,
						pluginType.getName(), Arrays.toString(types)));
			}

			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Invoke plugin method %s using arguments %s", businessMethod,
						Arrays.toString(invocationArguments)));
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

		Assert.isInstanceOf(delimiterType, delimiter, String.format("Delimiter expression did "
				+ "not return a suitable delimiter! Make sure the expression evaluates to a suitable "
				+ "type! Got %s but need %s", delimiter.getClass(), delimiterType));

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

	/**
	 * Lookup methods for plugins.
	 * 
	 * @author Oliver Gierke
	 */
	private enum PluginLookupMethod {

		/**
		 * The first plugin supporting a given delimiter found will be invoked.
		 */
		ONE,

		/**
		 * All plugins supporting a given delimiter will be invoked. Plugin order will be considered.
		 * 
		 * @see OrderAwarePluginRegistry
		 * @see Order
		 * @see Ordered
		 */
		ALL;

		/**
		 * Returns the default {@link PluginLookupMethod}.
		 * 
		 * @return
		 */
		static PluginLookupMethod getDefault() {
			return ONE;
		}
	}
}
