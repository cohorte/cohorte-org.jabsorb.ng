package org.jabsorb.ng.logging;

/**
 * @author ogattaz
 * 
 */
public interface ILogger {

    /**
     * @param aWhat
     * @param aInfos
     */
    void debug(String aWhat, Object... aInfos);

    /**
     * @param aWhat
     * @param aInfos
     */
    void error(String aWhat, Object... aInfos);

    /**
     * @return
     */
    String getName();

    /**
     * @param aWhat
     * @param aInfos
     */
    void info(String aWhat, Object... aInfos);

    /**
     * @return
     */
    boolean isDebugEnabled();

    /**
     * @return
     */
    boolean isErrorEnabled();

    /**
     * @return
     */
    boolean isInfoEnabled();

    /**
     * @return
     */
    boolean isTraceEnabled();

    /**
     * @return
     */
    boolean isWarnEnabled();

    /**
     * @param aWhat
     * @param aInfos
     */
    void trace(String aWhat, Object... aInfos);

    /**
     * @param aWhat
     * @param aInfos
     */
    void warn(String aWhat, Object... aInfos);
}
