package org.jabsorb.ng.logging;

/**
 * @author ogattaz
 * 
 */
public interface ILoggerProvider {

    /**
     * @param aClass
     * @return
     */
    ILogger getLogger(Class<?> aClass);
}
