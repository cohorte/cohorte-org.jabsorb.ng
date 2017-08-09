/**
 *
 */
package org.jabsorb.ng.osgi;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jabsorb.ng.logging.ILogger;
import org.jabsorb.ng.logging.ILoggerProvider;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

/**
 * A Jabsorb logger based on the OSGi log service
 *
 * @author Thomas Calmant
 */
public class LoggerOSGiProvider implements ILoggerProvider {

    /** The bundle context */
    private final BundleContext pContext;

    /** Class name -&gt; logger, handled by the provider */
    private final Map<String, LoggerOSGi> pLoggers = new LinkedHashMap<String, LoggerOSGi>();

    /** The log service currently in use */
    private LogService pLogService;

    /**
     * Sets up the logger
     *
     * @param aContext
     *            The bundle context
     */
    public LoggerOSGiProvider(final BundleContext aContext) {

        pContext = aContext;
    }

    /**
     * Cleans up all references in the provider
     */
    public void clear() {

        // Clear the reference to the service
        pLogService = null;

        // Clean up all loggers
        for (final LoggerOSGi logger : pLoggers.values()) {
            logger.setLogService(null);
        }

        // Clear the list
        pLoggers.clear();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jabsorb.ng.logging.ILoggerProvider#getLogger(java.lang.Class)
     */
    @Override
    public ILogger getLogger(final Class<?> aClass) {

        final String name = aClass.getName();
        LoggerOSGi logger = pLoggers.get(name);
        if (logger == null) {
            logger = new LoggerOSGi(aClass.getName());
            logger.setLogService(pLogService);
            pLoggers.put(name, logger);
        }

        return logger;
    }

    /**
     * Sets the log service for all loggers
     *
     * @param aLogService
     *            the log service, or null
     */
    public void setLogService(final LogService aLogService) {

        // Store the reference
        pLogService = aLogService;

        // Check debug mode
        final String debugFlag = pContext.getProperty("jabsorb.debug");
        final boolean debugEnabled = (debugFlag != null && debugFlag
                .equalsIgnoreCase("true"));

        // Update all loggers
        for (final LoggerOSGi logger : pLoggers.values()) {
            logger.setDebugEnabled(debugEnabled);
            logger.setLogService(aLogService);
        }
    }
}
