package org.synyx.hera.core;

/**
 * @author Oliver Gierke - gierke@synyx.de
 */
public class SamplePluginImplementation implements SamplePlugin {

    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.plugin.core.Plugin#supports(java.lang.Object)
     */
    public boolean supports(String delimiter) {

        return "FOO".equals(delimiter);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.plugin.core.ISamplePlugin#pluginMethod()
     */
    public void pluginMethod() {

    }
}
