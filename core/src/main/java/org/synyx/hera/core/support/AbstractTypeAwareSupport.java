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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Abstract base class to implement types that need access to all beans of a
 * given type from the {@link ApplicationContext}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public abstract class AbstractTypeAwareSupport<T> implements
        ApplicationContextAware {

    private ApplicationContext context;
    private Class<T> type;


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.context.ApplicationContextAware#setApplicationContext
     * (org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext context)
            throws BeansException {

        this.context = context;
    }


    /**
     * @param type the type to set
     */
    public void setType(Class<T> type) {

        this.type = type;
    }


    /**
     * Returns all beans from the {@link ApplicationContext} that match the
     * given type.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List<T> getBeans() {

        Map<String, T> pluginMap = context.getBeansOfType(type);

        return new ArrayList<T>(pluginMap.values());
    }
}
