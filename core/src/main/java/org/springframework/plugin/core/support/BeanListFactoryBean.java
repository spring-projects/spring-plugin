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
package org.springframework.plugin.core.support;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

/**
 * Factory to create bean lists for a given type. Exposes all beans of the configured type that can be found in the
 * {@link org.springframework.context.ApplicationContext}.
 * 
 * @author Oliver Gierke
 */
public class BeanListFactoryBean<T> extends AbstractTypeAwareSupport<T> implements FactoryBean<List<T>> {

	private static final Comparator<Object> COMPARATOR = new AnnotationAwareOrderComparator();

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	public List<T> getObject() {

		List<T> beans = getBeans();
		Collections.sort(beans, COMPARATOR);

		return beans;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	public Class<?> getObjectType() {
		return List.class;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	public boolean isSingleton() {
		return true;
	}
}