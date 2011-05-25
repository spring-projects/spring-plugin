package org.synyx.hera.si.sample;

import org.synyx.hera.core.Plugin;

/**
 *
 * @author Oliver Gierke
 */
public interface SamplePlugin extends Plugin<String> {

	void myBusinessMethod(String message);
}
