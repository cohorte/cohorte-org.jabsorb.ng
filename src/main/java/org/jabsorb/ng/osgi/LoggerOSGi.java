/**
 * 
 */
package org.jabsorb.ng.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jabsorb.ng.logging.ILogger;
import org.osgi.service.log.LogService;

/**
 * A Jabsorb logger based on the OSGi log service
 * 
 * @author Thomas Calmant
 */
public class LoggerOSGi implements ILogger {

    /** The name of the logging class */
    private final String pClassName;

    /** The OSGi log service */
    private LogService pLogger;

    /**
     * Sets up the logger
     * 
     * @param aLogger
     *            an OSGi log service
     * @param aClassName
     *            The name of the logging class
     */
    public LoggerOSGi(final String aClassName) {

        pClassName = aClassName;
        pLogger = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.ng.logging.ILogger#debug(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void debug(final String aMethod, final String aMessage) {

        log(LogService.LOG_DEBUG, aMethod, aMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.ng.logging.ILogger#error(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void error(final String aMethod, final String aMessage) {

        log(LogService.LOG_ERROR, aMethod, aMessage);
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

        log(LogService.LOG_ERROR, aMethod, aMessage, aThrowable);
    }

    /**
     * Formats a message string containing the name of the logging method
     * 
     * @param aMethod
     *            A method name
     * @param aMessage
     *            Log message
     * @return A log message
     */
    private String formatMessage(final String aMethod, final String aMessage) {

        return pClassName + "." + aMethod + "() :: " + aMessage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.ng.logging.ILogger#getName()
     */
    @Override
    public String getName() {

        return "LogService-" + pClassName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.ng.logging.ILogger#info(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void info(final String aMethod, final String aMessage) {

        log(LogService.LOG_INFO, aMethod, aMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.ng.logging.ILogger#isDebugEnabled()
     */
    @Override
    public boolean isDebugEnabled() {

        // Let the service choose what to do
        return true;
    }

    /**
     * Logs the given message. Uses the OSGi log service if possible, else uses
     * the Java logging API.
     * 
     * @param aLevel
     *            Log level
     * @param aMethod
     *            Method calling the logger
     * @param aMessage
     *            Message to log
     */
    private void log(final int aLevel, final String aMethod,
            final String aMessage) {

        log(aLevel, aMethod, aMessage, null);
    }

    /**
     * Logs the given message. Uses the OSGi log service if possible, else uses
     * the Java logging API.
     * 
     * @param aLevel
     *            Log level
     * @param aMethod
     *            Method calling the logger
     * @param aMessage
     *            Message to log
     * @param aThrowable
     *            Associated exception
     */
    private void log(final int aLevel, final String aMethod,
            final String aMessage, final Throwable aThrowable) {

        if (pLogger != null) {
            // Log service is available
            pLogger.log(aLevel, formatMessage(aMethod, aMessage), aThrowable);

        } else {
            // Use the Java logger if necessary
            Logger.getLogger(pClassName).logp(toJavaLevel(aLevel), pClassName,
                    aMethod, aMessage, aThrowable);
        }
    }

    /**
     * Sets the log service to use
     * 
     * @param aLogger
     *            the log service
     */
    void setLogService(final LogService aLogger) {

        pLogger = aLogger;
    }

    /**
     * Converts an OSGi log level to its Java logging equivalent
     * 
     * @param aLevel
     *            An OSGi log service level
     * @return A Java API logging level
     */
    private Level toJavaLevel(final int aLevel) {

        switch (aLevel) {
        case LogService.LOG_DEBUG:
            return Level.FINE;

        case LogService.LOG_ERROR:
            return Level.SEVERE;

        case LogService.LOG_INFO:
            return Level.INFO;

        case LogService.LOG_WARNING:
            return Level.WARNING;

        default:
            return Level.FINE;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.ng.logging.ILogger#warning(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void warning(final String aMethod, final String aMessage) {

        log(LogService.LOG_WARNING, aMethod, aMessage);
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

        log(LogService.LOG_WARNING, aMethod, aMessage, aThrowable);
    }
}
