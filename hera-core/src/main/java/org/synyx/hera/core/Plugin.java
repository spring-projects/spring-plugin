package org.synyx.hera.core;

/**
 * Central interface for plugins for the system. This interface is meant to be
 * extended by concrete plugin interfaces. Its core responsibility is to define
 * a delimiter type and a selection callback with the delimiter as parameter.
 * The delimiter is some kind of decision object concrete plugin implementations
 * can use to decide if they are capable to be executed.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface Plugin<S> {

    /**
     * Returns if a plugin should be invoked according to the given delimiter.
     * 
     * @param delimiter
     * @return if the plugin should be invoked
     */
    boolean supports(S delimiter);
}
