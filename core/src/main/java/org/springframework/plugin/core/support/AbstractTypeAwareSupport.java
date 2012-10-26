/*
 * Copyright 2008-2012 the original author or authors.
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
package org.springframework.plugin.core.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.Assert;

/**
 * Abstract base class to implement types that need access to all beans of a given type from the
 * {@link ApplicationContext}.
 * 
 * @author Oliver Gierke
 */
public abstract class AbstractTypeAwareSupport<T> implements ApplicationContextAware,
		ApplicationListener<ContextRefreshedEvent>, InitializingBean {

	private ApplicationContext context;
	private Class<T> type;
	private BeansOfTypeTargetSource targetSource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationContextAware#setApplicationContext
	 * (org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext context) {

		this.context = context;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Class<T> type) {

		this.type = type;
	}

	/**
	 * Returns all beans from the {@link ApplicationContext} that match the given type.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<T> getBeans() {

		ProxyFactory factory = new ProxyFactory(List.class, targetSource);
		return (List<T>) factory.getProxy();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() {
		this.targetSource = new BeansOfTypeTargetSource(context, type, false);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	public void onApplicationEvent(ContextRefreshedEvent event) {

		if (context.equals(event.getApplicationContext())) {
			targetSource.freeze();
		}
	}

	/**
	 * {@link TargetSource} implementation that returns all beans of the configured type from the
	 * {@link ListableBeanFactory} the instance was set up with. Allows freezing the lookup as calls to
	 * {@link ListableBeanFactory#getBeansOfType(Class, boolean, boolean)} are potentially expensive as the entire factory
	 * has to be scanned for type information.
	 * 
	 * @author Oliver Gierke
	 */
	static class BeansOfTypeTargetSource implements TargetSource {

		private final ListableBeanFactory context;
		private final Class<?> type;
		private final boolean eagerInit;

		private boolean frozen = false;
		private Collection<?> components;

		/**
		 * Creates a new {@link BeansOfTypeTargetSource} using the given {@link ListableBeanFactory} to lookup beans of the
		 * given type.
		 * 
		 * @param context must not be {@literal null}.
		 * @param type must not be {@literal null}.
		 * @param eagerInit whether to eagerly init {@link FactoryBean}s, defaults to {@literal false}.
		 */
		public BeansOfTypeTargetSource(ListableBeanFactory context, Class<?> type, boolean eagerInit) {

			Assert.notNull(context);
			Assert.notNull(type);

			this.context = context;
			this.type = type;
			this.eagerInit = eagerInit;
		}

		/**
		 * Freezes the {@link TargetSource} so that the next access to {@link #getTarget()} will get the results cached and
		 * reused.
		 */
		public void freeze() {
			this.frozen = true;
		}

		/*
		 * (non-Javadoc)
		 * @see org.springframework.aop.TargetSource#getTargetClass()
		 */
		public Class<?> getTargetClass() {
			return List.class;
		}

		/*
		 * (non-Javadoc)
		 * @see org.springframework.aop.TargetSource#isStatic()
		 */
		public boolean isStatic() {
			return frozen;
		}

		/*
		 * (non-Javadoc)
		 * @see org.springframework.aop.TargetSource#getTarget()
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public synchronized Object getTarget() throws Exception {

			Collection<?> components = this.components == null ? context.getBeansOfType(type, false, eagerInit).values()
					: this.components;

			if (frozen && this.components == null) {
				this.components = components;
			}

			return new ArrayList(components);
		}

		/*
		 * (non-Javadoc)
		 * @see org.springframework.aop.TargetSource#releaseTarget(java.lang.Object)
		 */
		public void releaseTarget(Object target) throws Exception {

		}
	}
}
