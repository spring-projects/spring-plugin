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
