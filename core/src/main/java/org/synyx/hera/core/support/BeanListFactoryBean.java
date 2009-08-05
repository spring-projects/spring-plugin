/*
 * Copyright 2002-2008 the original author or authors.
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
package org.synyx.hera.core.support;

import java.awt.List;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;


/**
 * Factory to create bean lists for a given type. Exposes all beans of the
 * configured type that can be found in the {@link ApplicationContext}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class BeanListFactoryBean<T> extends AbstractTypeAwareSupport<T>
        implements FactoryBean {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject() throws Exception {

        return getBeans();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @SuppressWarnings("unchecked")
    public Class getObjectType() {

        return List.class;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton() {

        return true;
    }
}