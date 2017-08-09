/**
 * 
 */
package org.jabsorb.ng.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements a logger using the Java logging API
 * 
 * @author Thomas Calmant
 */
public class LoggerJava implements ILogger {

    /** The class name */
    private final String pClassName;

    /** The Java logger */
    private final Logger pLogger;

    /**
     * Creates the logger
     * 
     * @param aName
     *            the logger name
     */
    public LoggerJava(final String aClassName) {

        pClassName = aClassName;
        pLogger = Logger.getLogger(aClassName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.ng.logging.ILogger#debug(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void debug(final String aMethod, final String aMessage) {

        pLogger.logp(Level.FINE, pClassName, aMethod, aMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.ng.logging.ILogger#error(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void error(final String aMethod, final String aMessage) {

        pLogger.logp(Level.SEVERE, pClassName, aMethod, aMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.ng.logging.ILogger#error(java.lang.String,
     * java.lang.String, java.lang.Throwable)
     */
    @Override
    public void error(final String aMethod, final String aMessage,
            final Throwable aThrowable) {

        pLogger.logp(Level.SEVERE, pClassName, aMethod, aMessage, aThrowable);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.ng.logging.ILogger#getName()
     */
    @Override
    public String getName() {

        return pLogger.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.ng.logging.ILogger#info(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void info(final String aMethod, final String aMessage) {

        pLogger.logp(Level.INFO, pClassName, aMethod, aMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.ng.logging.ILogger#isDebugEnabled()
     */
    @Override
    public boolean isDebugEnabled() {

        return pLogger.isLoggable(Level.FINE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.ng.logging.ILogger#warning(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void warning(final String aMethod, final String aMessage) {

        pLogger.logp(Level.WARNING, pClassName, aMethod, aMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.ng.logging.ILogger#warning(java.lang.String,
     * java.lang.String, java.lang.Throwable)
     */
    @Override
    public void warning(final String aMethod, final String aMessage,
            final Throwable aThrowable) {

        pLogger.logp(Level.WARNING, pClassName, aMethod, aMessage, aThrowable);
    }
}
