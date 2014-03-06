/**
 * 
 */
package org.jabsorb.ng.osgi;

import org.jabsorb.ng.logging.LoggerFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * Jabsorb bundle activator: activates the log service when possible
 * 
 * @author Thomas Calmant
 */
public class Activator implements BundleActivator, ServiceListener {

    /** The bundle context */
    private BundleContext pContext;

    /** Reference to the log service */
    private ServiceReference<LogService> pLogReference;

    /** The logger provider */
    private LoggerOSGiProvider pProvider;

    /**
     * Looks for a log service in the framework. This must not be called if a
     * log service is already bound
     * 
     * @return the log service
     */
    private LogService getService() {

        pLogReference = pContext.getServiceReference(LogService.class);
        if (pLogReference != null) {
            return pContext.getService(pLogReference);
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.
     * ServiceEvent)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void serviceChanged(final ServiceEvent aEvent) {

        switch (aEvent.getType()) {
        case ServiceEvent.REGISTERED:
            if (pLogReference == null) {
                // New log service to use
                pLogReference = (ServiceReference<LogService>) aEvent
                        .getServiceReference();
                pProvider.setLogService(pContext.getService(pLogReference));
            }
            break;

        case ServiceEvent.UNREGISTERING:
            if (aEvent.getServiceReference().equals(pLogReference)) {
                // Clear current usage
                pContext.ungetService(pLogReference);
                pLogReference = null;

                // Look for a new service
                pProvider.setLogService(getService());
            }
            break;

        default:
            break;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void start(final BundleContext aContext) {

        // Store the bundle context
        pContext = aContext;

        // Set the log provider
        pProvider = new LoggerOSGiProvider();
        LoggerFactory.setLoggerProvider(pProvider);

        // Look for the log service
        pProvider.setLogService(getService());

        // Setup the log service tracking
        try {
            aContext.addServiceListener(this, "(" + Constants.OBJECTCLASS + "="
                    + LogService.class.getName() + ")");

        } catch (InvalidSyntaxException ex) {
            // Won't happen... but who knows
            pProvider.getLogger(getClass()).error("start",
                    "Error creating the log service listener", ex);
        }

        pProvider.getLogger(getClass()).info("start",
                "Jabsorb OSGi bundle ready");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext aContext) {

        // Stop the service listener
        aContext.removeServiceListener(this);

        pProvider.getLogger(getClass()).info("stop",
                "Jabsorb OSGi bundle going away");

        // Reset the log provider
        LoggerFactory.setLoggerProvider(null);
        pProvider.clear();

        // Release the bound service
        if (pLogReference != null) {
            aContext.ungetService(pLogReference);
        }

        // Clear references
        pContext = null;
        pLogReference = null;
        pProvider = null;
    }
}
