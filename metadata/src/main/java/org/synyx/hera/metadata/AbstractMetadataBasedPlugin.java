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

package org.synyx.hera.metadata;

import org.synyx.hera.core.Plugin;


/**
 * Abstract base class for plugins based on {@code PluginMetadata}. Plugins
 * based on this class can be selected from the {@code PluginRegistry} via an
 * instance of {@code PluginMetadata}. Therefore you can regard this as a role
 * model implementation of a base class for certain delimiter implmentations.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public abstract class AbstractMetadataBasedPlugin implements
        Plugin<PluginMetadata>, MetadataProvider {

    private PluginMetadata metadata;


    /**
     * Creates a new instance of {@code AbstractMetadataBasedPlugin}.
     * 
     * @param name
     * @param version
     */
    public AbstractMetadataBasedPlugin(String name, String version) {

        this.metadata = new SimplePluginMetadata(name, version);
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.minos.core.plugin.Plugin#supports(java.lang.Object)
     */
    public boolean supports(PluginMetadata delimiter) {

        return getMetadata().equals(delimiter);
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.minos.core.plugin.MetadataProvider#getMetadata()
     */
    public PluginMetadata getMetadata() {

        return metadata;
    }
}
