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
package org.synyx.hera.si.sample;

import org.springframework.core.annotation.Order;

/**
 * Sample implementation of {@link SamplePlugin} supporting {@code BAR} delimiter.
 * 
 * @author Oliver Gierke
 */
@Order(10)
public class SecondSamplePluginImpl implements SamplePlugin {

	/* 
	 * (non-Javadoc)
	 * @see org.synyx.hera.core.Plugin#supports(java.lang.Object)
	 */
	public boolean supports(String delimiter) {
		return "BAR".equals(delimiter);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.synyx.hera.si.sample.SamplePlugin#myBusinessMethod()
	 */
	public String myBusinessMethod(String message) {
		System.out.println("Second plugin invoked! " + message);
		return "Second";
	}
}
