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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.GenericTypeResolver;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.expression.IntegrationEvaluationContextAware;
import org.springframework.plugin.core.OrderAwarePluginRegistry;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Dynamic service activator that uses a {@link PluginRegistry} to delegate execution
 * to one or more plugins matching a delimiter.
 *
 * @author Oliver Gierke
 * @author Artem Bilan
 */
public class PluginMethodInvocationService implements IntegrationEvaluationContextAware {

	private static final Log LOG = LogFactory.getLog(PluginMethodInvocationService.class);

	private static final SpelExpressionParser parser = new SpelExpressionParser();

	private final PluginRegistry<? extends Plugin<?>, Object> registry;

	private final Class<? extends Plugin<?>> pluginType;

	private final Class<?> delimiterType;

	private final String serviceMethodName;

	private final Map<Class<?>[], Method> methodsForArguments = new HashMap<Class<?>[], Method>();

	private Expression delimiterExpression;

	private Expression invocationArgumentsExpression;

	private PluginLookupMethod pluginLookupMethod = PluginLookupMethod.getDefault();

	private EvaluationContext evaluationContext;

	/**
	 * Create a new {@link PluginMethodInvocationService} for the given
	 * {@link PluginRegistry}, pluginType and a method name to call.
	 * @param registry the {@link PluginRegistry} to server {@link Plugin}s.
	 * @param pluginType the {@link Plugin} type.
	 * @param serviceMethodName the {@link Plugin} method name.
	 */
	@SuppressWarnings("unchecked")
	public PluginMethodInvocationService(PluginRegistry<? extends Plugin<?>, ?> registry,
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

	private void verify() {

		boolean methodFound = false;

		for (Method candidate : this.pluginType.getMethods()) {
			if (candidate.getName().equals(this.serviceMethodName)) {
				methodFound = true;
				break;
			}
		}

		if (!methodFound) {
			throw new IllegalArgumentException(String.format("Not method %s found for type %s!", this.serviceMethodName,
					this.pluginType));
		}
	}

	/**
	 * Set the SpEL expression to extract the delimiter from the provided argument
	 * as root object of expression evaluation context.
	 * @param expression the delimiterExpression to set
	 */
	public void setDelimiterExpression(String expression) {
		Assert.hasText(expression);
		this.delimiterExpression = parser.parseExpression(expression);
	}

	/**
	 * Set the SpEL expression to extract the method arguments for the actual plugin method
	 * invocation from the root object of expression evaluation context.
	 * @param expression the {@link #invocationArgumentsExpression} to set.
	 */
	public void setInvocationArgumentsExpression(String expression) {

		Assert.hasText(expression);
		this.invocationArgumentsExpression = parser.parseExpression(expression);
	}

	/**
	 * Configure the method to be used when looking up plugins to invoke.
	 * @param pluginLookupMethod the {@link #pluginLookupMethod} to set.
	 * @see PluginLookupMethod
	 */
	public void setPluginLookupMethod(PluginLookupMethod pluginLookupMethod) {
		this.pluginLookupMethod = pluginLookupMethod == null ? PluginLookupMethod.getDefault() : pluginLookupMethod;
	}

	@Override
	public void setIntegrationEvaluationContext(EvaluationContext evaluationContext) {
		this.evaluationContext = evaluationContext;
	}

	public Object invoke(Object request) {

		Object delimiter = getDelimiter(request);

		switch (this.pluginLookupMethod) {

		case ALL:
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Looking up plugins for delimiter %s", delimiter));
			}
			return invokePlugins(this.registry.getPluginsFor(delimiter), request);

		case ONE:
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Looking up plugin for delimiter %s", delimiter));
			}
			List<Object> results = invokePlugins(Collections.singletonList(this.registry.getPluginFor(delimiter)),
					request);
			return results.isEmpty() ? null : results.get(0);

		default:
			throw new IllegalStateException(String.format("Unsupported plugin lookup method %s!", this.pluginLookupMethod));
		}
	}

	private List<Object> invokePlugins(Collection<? extends Plugin<?>> plugins, Object request) {

		List<Object> results = new ArrayList<Object>();

		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("Invoking plugin(s) %s with message %s",
					StringUtils.collectionToCommaDelimitedString(plugins), request));
		}

		for (Plugin<?> plugin : plugins) {

			Object[] invocationArguments = getInvocationArguments(request);
			Class<?>[] types = getTypes(invocationArguments);

			Method businessMethod = this.methodsForArguments.get(types);

			if (businessMethod == null) {
				businessMethod = ReflectionUtils.findMethod(this.pluginType, this.serviceMethodName, types);

				if (businessMethod == null) {
					throw new IllegalArgumentException(String.format(
							"Did not find a method %s on %s taking the following parameters %s", this.serviceMethodName,
							this.pluginType.getName(), Arrays.toString(types)));
				}

				this.methodsForArguments.put(types, businessMethod);
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
	 * Return the delimiter object to be used for the given {@code request}.
	 * Will use the configured delimiter expression if configured.
	 * @param request the root object of expression evaluation context to determine the delimiter.
	 * @return the delimiter.
	 */
	private Object getDelimiter(Object request) {

		Object delimiter = request;

		if (this.delimiterExpression != null) {
			delimiter = this.delimiterExpression.getValue(this.evaluationContext, request);
		}

		Assert.isInstanceOf(this.delimiterType, delimiter, String.format("Delimiter expression did "
				+ "not return a suitable delimiter! Make sure the expression evaluates to a suitable "
				+ "type! Got %s but need %s", delimiter.getClass(), this.delimiterType));

		return delimiter;
	}

	/**
	 * Return the actual arguments to be used for the plugin method invocation.
	 * Will apply the configured invocation argument expression to the given {@code request}.
	 * @param request the root object of expression evaluation context to determine the invocation arguments.
	 * @return the invocation arguments.
	 */
	private Object[] getInvocationArguments(Object request) {

		if (this.invocationArgumentsExpression == null) {
			return new Object[] { request };
		}

		Object result = this.invocationArgumentsExpression.getValue(this.evaluationContext, request);

		return ObjectUtils.isArray(result) ? ObjectUtils.toObjectArray(result) : new Object[] { result };
	}

	/**
	 * Return an array of types for the given objects. Inspects each element
	 * of the array for its type. will return {@literal null} for {@literal null} source values.
	 * @param source the args of argument to determine their types.
	 * @return the array of types for the given objects.
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
		 * @see OrderAwarePluginRegistry
		 * @see Order
		 * @see Ordered
		 */
		ALL;

		/**
		 * @return the default {@link PluginLookupMethod}.
		 */
		static PluginLookupMethod getDefault() {
			return ONE;
		}

	}

}
