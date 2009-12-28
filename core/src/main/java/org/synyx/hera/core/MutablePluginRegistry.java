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

package org.synyx.hera.core;

import java.util.List;


/**
 * Extension of {@link PluginRegistry} with additional methods to modify the
 * registry.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface MutablePluginRegistry<T extends Plugin<S>, S> extends
        PluginRegistry<T, S> {

    /**
     * Register plugins.
     * 
     * @param plugins the plugins to set
     */
    void setPlugins(List<? extends T> plugins);


    /**
     * Adds a given plugin to the registry.
     * 
     * @param plugin
     */
    void addPlugin(T plugin);


    /**
     * Removes a given plugin from the registry.
     * 
     * @param plugin
     */
    boolean removePlugin(T plugin);
}
