package org.synyx.hera.si.sample;

/**
 * 
 * @author Oliver Gierke
 */
public class SecondSamplePluginImpl implements SamplePlugin {

	/* (non-Javadoc)
	 * @see org.synyx.hera.core.Plugin#supports(java.lang.Object)
	 */
	public boolean supports(String delimiter) {
		return "BAR".equals(delimiter);
	}

	/* (non-Javadoc)
	 * @see org.synyx.hera.si.sample.SamplePlugin#myBusinessMethod()
	 */
	public void myBusinessMethod(String message) {
		System.out.println("Second plugin invoked! " + message);
	}
}
