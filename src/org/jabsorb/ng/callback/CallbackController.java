/*
 * jabsorb - a Java to JavaScript Advanced Object Request Broker
 * http://www.jabsorb.org
 *
 * Copyright 2007-2008 The jabsorb team
 *
 * based on original code from
 * JSON-RPC-Java - a JSON-RPC to Java Bridge with dynamic invocation
 *
 * Copyright Metaparadigm Pte. Ltd. 2004.
 * Michael Clark <michael@metaparadigm.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.jabsorb.ng.callback;

import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jabsorb.ng.logging.ILogger;
import org.jabsorb.ng.logging.LoggerFactory;

/**
 * Class that is instantiated per bridge to maintain the list of callbacks and
 * provides an interface to invoke them.
 */
public class CallbackController implements Serializable {

    /**
     * The log used for this class.
     */
    private static final ILogger log = LoggerFactory
            .getLogger(CallbackController.class);

    /**
     * Generated version id.
     */
    private static final long serialVersionUID = 3;

    /**
     * Holds all callbacks registered with this controller. Type: CallbackData
     */
    private final Set<CallbackData> callbackSet;

    /**
     * Default constructor.
     */
    public CallbackController() {

        callbackSet = new HashSet<CallbackData>();
    }

    /**
     * Calls the 'invocation Error' callback handler.
     * 
     * @param context
     *            The transport context (the HttpServletRequest object in the
     *            case of the HTTP transport).
     * @param instance
     *            The object instance or null if it is a static method.
     * @param accessibleObject
     *            Method/constructor that failed the invocation.
     * @param error
     *            Error resulting from the invocation.
     */
    public void errorCallback(final Object context, final Object instance,
            final AccessibleObject accessibleObject, final Throwable error) {

        synchronized (callbackSet) {
            final Iterator<CallbackData> i = callbackSet.iterator();
            while (i.hasNext()) {
                final CallbackData cbdata = i.next();
                if (cbdata.understands(context)
                        && (cbdata.getCallback() instanceof ErrorInvocationCallback)) {
                    final ErrorInvocationCallback ecb = (ErrorInvocationCallback) cbdata
                            .getCallback();
                    try {
                        ecb.invocationError(context, instance,
                                accessibleObject, error);
                    } catch (final Throwable th) {
                        // Ignore all errors in callback, don't want
                        // event listener to bring everything to its knees.
                    }
                }
            }
        }
    }

    /**
     * Calls the 'postInvoke' callback handler.
     * 
     * @param context
     *            The transport context (the HttpServletRequest object in the
     *            case of the HTTP transport).
     * @param instance
     *            The object instance or null if it is a static method.
     * @param accessibleObject
     *            The method/constructor that was just called.
     * @param result
     *            The object that was returned.
     * @throws Exception
     *             if postInvoke fails
     */
    public void postInvokeCallback(final Object context, final Object instance,
            final AccessibleObject accessibleObject, final Object result)
            throws Exception {

        synchronized (callbackSet) {
            final Iterator<CallbackData> i = callbackSet.iterator();
            while (i.hasNext()) {
                final CallbackData cbdata = i.next();
                if (cbdata.understands(context)) {
                    cbdata.getCallback().postInvoke(context, instance,
                            accessibleObject, result);
                }
            }
        }
    }

    /**
     * Calls the 'preInvoke' callback handler.
     * 
     * @param context
     *            The transport context (the HttpServletRequest object in the
     *            case of the HTTP transport).
     * @param instance
     *            The object instance or null if it is a static method.
     * @param accessibleObject
     *            The method/constructor that is about to be called.
     * @param arguments
     *            The argements to be passed to the method.
     * @throws Exception
     *             If preInvoke fails
     */
    public void preInvokeCallback(final Object context, final Object instance,
            final AccessibleObject accessibleObject, final Object arguments[])
            throws Exception {

        synchronized (callbackSet) {
            final Iterator<CallbackData> i = callbackSet.iterator();
            while (i.hasNext()) {
                final CallbackData cbdata = i.next();
                if (cbdata.understands(context)) {
                    cbdata.getCallback().preInvoke(context, instance,
                            accessibleObject, arguments);
                }
            }
        }
    }

    /**
     * Registers a callback to be called before and after method invocation
     * 
     * @param callback
     *            The object implementing the InvocationCallback Interface
     * @param contextInterface
     *            The type of transport Context interface the callback is
     *            interested in eg. HttpServletRequest.class for the servlet
     *            transport.
     */
    public void registerCallback(final InvocationCallback callback,
            final Class<?> contextInterface) {

        synchronized (callbackSet) {
            callbackSet.add(new CallbackData(callback, contextInterface));
        }
        if (log.isDebugEnabled()) {
            log.debug("registerCallback", "registered callback "
                    + callback.getClass().getName()
                    + " with context interface " + contextInterface.getName());
        }
    }

    /**
     * Unregisters a callback
     * 
     * @param callback
     *            The previously registered InvocationCallback object
     * @param contextInterface
     *            The previously registered transport Context interface.
     */
    public void unregisterCallback(final InvocationCallback callback,
            final Class<?> contextInterface) {

        synchronized (callbackSet) {
            callbackSet.remove(new CallbackData(callback, contextInterface));
        }
        if (log.isDebugEnabled()) {
            log.debug("unregisterCallback", "unregistered callback "
                    + callback.getClass().getName() + " with context "
                    + contextInterface.getName());
        }
    }
}
