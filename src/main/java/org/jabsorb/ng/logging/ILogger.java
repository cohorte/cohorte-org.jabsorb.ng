package org.jabsorb.ng.logging;

/**
 * @author ogattaz
 * 
 */
public interface ILogger {

    /**
     * 
     * @param aMethod
     * @param aMessage
     */
    void debug(String aMethod, String aMessage);

    /**
     * 
     * @param aMethod
     * @param aMessage
     */
    void error(String aMethod, String aMessage);

    /**
     * 
     * @param aMethod
     * @param aMessage
     * @param aThrowable
     */
    void error(String aMethod, String aMessage, Throwable aThrowable);

    /**
     * @return
     */
    String getName();

    /**
     * 
     * @param aMethod
     * @param aMessage
     */
    void info(String aMethod, String aMessage);

    /**
     * Checks if the debug logging is enabled
     * 
     * @return True if the debug logging is enabled
     */
    boolean isDebugEnabled();

    /**
     * 
     * @param aMethod
     * @param aMessage
     */
    void warning(String aMethod, String aMessage);

    /**
     * 
     * @param aMethod
     * @param aMessage
     * @param aThrowable
     */
    void warning(String aMethod, String aMessage, Throwable aThrowable);
}
