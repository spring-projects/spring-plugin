package org.synyx.hera.si.sample;

/**
 *
 * @author Oliver Gierke
 */
public class FirstSamplePluginImpl implements SamplePlugin {

	/* (non-Javadoc)
	 * @see org.synyx.hera.core.Plugin#supports(java.lang.Object)
	 */
	public boolean supports(String delimiter) {
		return "FOO".equals(delimiter);
	}

	/* (non-Javadoc)
	 * @see org.synyx.hera.si.sample.SamplePlugin#myBusinessMethod()
	 */
	public void myBusinessMethod(String message) {
		System.out.println("First plugin invoked! " + message);
	}
}
