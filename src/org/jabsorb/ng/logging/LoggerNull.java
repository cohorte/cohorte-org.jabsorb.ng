package org.jabsorb.ng.logging;

/**
 * @author ogattaz
 * 
 */
public class LoggerNull implements ILogger {

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.ng.logging.ILogger#debug(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void debug(final String aMethod, final String aMessage) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.ng.logging.ILogger#error(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void error(final String aMethod, final String aMessage) {

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

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.logging.Logger#getName()
     */
    @Override
    public String getName() {

        return LoggerNull.class.getSimpleName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.ng.logging.ILogger#info(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void info(final String aMethod, final String aMessage) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.ng.logging.ILogger#isDebugEnabled()
     */
    @Override
    public boolean isDebugEnabled() {

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jabsorb.ng.logging.ILogger#warning(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void warning(final String aMethod, final String aMessage) {

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

    }
}
