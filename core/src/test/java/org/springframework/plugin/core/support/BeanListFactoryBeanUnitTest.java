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

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;

/**
 * Unit test for {@link BeanListFactoryBean}.
 * 
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.class)
public class BeanListFactoryBeanUnitTest {

	BeanListFactoryBean<Ordered> factory;

	@Mock ApplicationContext context;

	@Before
	public void setUp() {

		factory = new BeanListFactoryBean<Ordered>();
		factory.setApplicationContext(context);
		factory.setType(Ordered.class);
		factory.afterPropertiesSet();
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void regardsOrderOfBeans() throws Exception {

		// They shall be switched in the result.
		Ordered first = () -> 5;
		Ordered second = () -> 0;

		when(context.getBeanNamesForType(Ordered.class, false, false)).thenReturn(new String[] { "first", "second" });
		when(context.getType(any(String.class))).thenReturn((Class) Ordered.class);
		when(context.getBean("first")).thenReturn(first);
		when(context.getBean("second")).thenReturn(second);

		Object result = factory.getObject();
		assertTrue(result instanceof List<?>);

		List<Ordered> members = type(result);

		assertEquals(0, members.indexOf(second));
		assertEquals(1, members.indexOf(first));
	}

	@Test
	public void returnsEmptyListIfNoBeansFound() throws Exception {

		when(context.getBeanNamesForType(Ordered.class, false, false)).thenReturn(new String[0]);

		Object result = factory.getObject();
		assertTrue(result instanceof List<?>);

		List<Ordered> members = type(result);
		assertTrue(members.isEmpty());
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> type(Object list) {
		return (List<T>) list;
	}
}
