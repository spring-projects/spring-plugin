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
package org.synyx.hera.metadata;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Value object style implementation of {@code PluginMetadata}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class SimplePluginMetadata implements PluginMetadata {

    private String name;
    private String version;


    /**
     * Creates a new instance of {@code SimplePluginMetadata}.
     * 
     * @param name
     * @param version
     */
    public SimplePluginMetadata(String name, String version) {

        this.name = name;
        this.version = version;
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.minos.core.plugin.PluginMetadata#getName()
     */
    public String getName() {

        return name;
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.minos.core.plugin.PluginMetadata#getVersion()
     */
    public String getVersion() {

        return version;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return String.format("%s:%s", getName(), getVersion());
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof PluginMetadata)) {
            return false;
        }

        PluginMetadata that = (PluginMetadata) obj;

        boolean sameName = StringUtils.equals(this.getName(), that.getName());
        boolean sameVersion =
                StringUtils.equals(this.getName(), that.getName());

        return sameName && sameVersion;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        return ObjectUtils.hashCode(name) + ObjectUtils.hashCode(version);
    }
}
