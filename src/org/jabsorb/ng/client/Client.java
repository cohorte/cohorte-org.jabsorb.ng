/*
 * jabsorb - a Java to JavaScript Advanced Object Request Broker
 * http://www.jabsorb.org
 *
 * Copyright 2007-2008 The jabsorb team
 *
 * based on original code from
 * JSON-RPC-Client, a Java client extension to JSON-RPC-Java
 * (C) Copyright CodeBistro 2007, Sasha Ovsankin <sasha at codebistro dot com>
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
package org.jabsorb.ng.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.jabsorb.ng.JSONRPCBridge;
import org.jabsorb.ng.JSONRPCResult;
import org.jabsorb.ng.JSONSerializer;
import org.jabsorb.ng.logging.ILogger;
import org.jabsorb.ng.logging.LoggerFactory;
import org.jabsorb.ng.serializer.FixUp;
import org.jabsorb.ng.serializer.SerializerState;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A factory to create proxies for access to remote Jabsorb services.
 */
public class Client implements InvocationHandler {

    /** The logger for this class */
    private static final ILogger log = LoggerFactory.getLogger(Client.class);

    /** Maintain a unique id for each message */
    private final AtomicInteger pId = new AtomicInteger();

    /** Proxy object -&gt; remote key */
    private final Map<Object, String> pProxyMap = new HashMap<Object, String>();

    /** The Jabsorb serializer */
    private JSONSerializer pSerializer;

    /** The underlying HTTP session */
    private ISession pSession;

    /**
     * Create a client given a session
     *
     * @param aSession
     *            -- transport pSession to use for this connection
     */
    public Client(final ISession aSession) {

        try {
            pSession = aSession;
            pSerializer = new JSONSerializer();
            pSerializer.registerDefaultSerializers();

        } catch (final Exception e) {
            throw new ClientError(e);
        }
    }

    /**
     * Dispose of the proxy that is no longer needed
     *
     * @param proxy
     */
    public void closeProxy(final Object proxy) {

        pProxyMap.remove(proxy);
    }

    /**
     * Tries to get the requested constructor for the given class
     *
     * @param aClass
     *            A class
     * @param aParameters
     *            The requested constructor parameters
     * @return The found constructor, null on error
     */
    private <T> Constructor<T> getConstructor(final Class<T> aClass,
            final Class<?>... aParameters) {

        try {
            return aClass.getConstructor(aParameters);

        } catch (final SecurityException ex) {
            // Illegal access
            log.error("getConstructor", "Illegal access to a constructor of "
                    + aClass.getName(), ex);

        } catch (final NoSuchMethodException ex) {
            // Constructor not found, ignore
        }

        return null;
    }

    /**
     * Retrieves the next message ID
     *
     * @return the next message ID
     */
    private int getId() {

        return pId.getAndIncrement();
    }

    /**
     * Allow access to the serializer
     *
     * @return The serializer for this class
     */
    public JSONSerializer getSerializer() {

        return pSerializer;
    }

    /**
     * This method is public because of the inheritance from the
     * InvokationHandler -- <b>should never be called directly.</b>
     */
    @Override
    public Object invoke(final Object proxyObj, final Method method,
            final Object[] args) throws Throwable {

        final String methodName = method.getName();
        if (methodName.equals("hashCode")) {
            return new Integer(System.identityHashCode(proxyObj));
        } else if (methodName.equals("equals")) {
            return (proxyObj == args[0] ? Boolean.TRUE : Boolean.FALSE);
        } else if (methodName.equals("toString")) {
            return proxyObj.getClass().getName() + '@'
                    + Integer.toHexString(proxyObj.hashCode());
        }
        return invoke(pProxyMap.get(proxyObj), method.getName(), args,
                method.getReturnType());
    }

    /**
     * Invokes a method for the client.
     *
     * @param objectTag
     *            (optional) the name of the object to invoke the method on. May
     *            be null.
     * @param methodName
     *            The name of the method to call.
     * @param args
     *            The arguments to the method.
     * @param returnType
     *            What should be returned
     * @return The result of the call.
     * @throws Exception
     *             JSONObject, UnmarshallExceptions or Exceptions from invoking
     *             the method may be thrown.
     */
    private Object invoke(final String objectTag, final String methodName,
            final Object[] args, final Class<?> returnType) throws Throwable {

        final int id = getId();
        final JSONObject message = new JSONObject();
        String methodTag = objectTag == null ? "" : objectTag + ".";
        methodTag += methodName;
        message.put("method", methodTag);

        if (args != null) {
            final SerializerState state = new SerializerState();
            final JSONArray params = (JSONArray) pSerializer.marshall(state,
                    null /* parent */, args, "params");

            if ((state.getFixUps() != null) && (state.getFixUps().size() > 0)) {
                final JSONArray fixups = new JSONArray();
                for (final Iterator<FixUp> i = state.getFixUps().iterator(); i
                        .hasNext();) {
                    final FixUp fixup = i.next();
                    fixups.put(fixup.toJSONArray());
                }
                message.put("fixups", fixups);
            }
            message.put("params", params);
        } else {
            message.put("params", new JSONArray());
        }

        message.put("id", id);

        final JSONObject responseMessage = pSession.sendAndReceive(message);

        if (!responseMessage.has("result")) {
            processException(responseMessage);
        }
        final Object rawResult = responseMessage.get("result");
        if (returnType.equals(Void.TYPE)) {
            return null;
        } else if (rawResult == null) {
            processException(responseMessage);
        }
        {
            final JSONArray fixups = responseMessage.optJSONArray("fixups");

            if (fixups != null) {
                for (int i = 0; i < fixups.length(); i++) {
                    final JSONArray assignment = fixups.getJSONArray(i);
                    final JSONArray fixup = assignment.getJSONArray(0);
                    final JSONArray original = assignment.getJSONArray(1);
                    JSONRPCBridge.applyFixup(rawResult, fixup, original);
                }
            }
        }
        return pSerializer.unmarshall(new SerializerState(), returnType,
                rawResult);
    }

    /**
     * Tries to make a Throwable object from the given class name and message
     *
     * @param aJavaClass
     *            A Throwable class name
     * @param aMessage
     *            An error message
     * @return The Throwable instance, or null
     */
    private Throwable makeThrowable(final String aJavaClass,
            final String aMessage) {

        try {
            // Try to find the class
            @SuppressWarnings("unchecked")
            final Class<? extends Throwable> clazz = (Class<? extends Throwable>) Class
                    .forName(aJavaClass);
            if (!Throwable.class.isAssignableFrom(clazz)) {
                // Not an exception class
                return null;
            }

            // 'message' constructor
            Constructor<? extends Throwable> ctor = getConstructor(clazz,
                    String.class);
            if (ctor != null) {
                return ctor.newInstance(aMessage);
            }

            // default constructor
            ctor = getConstructor(clazz, (Class<?>[]) null);
            if (ctor != null) {
                return ctor.newInstance((Object[]) null);
            }

        } catch (final ClassNotFoundException ex) {
            // Class not found...
            log.error("makeThrowable", "Exception class not found: "
                    + aJavaClass);

        } catch (final IllegalArgumentException ex) {
            // Invalid message
            log.error("makeThrowable", "Invalid argument for the exception", ex);

        } catch (final InstantiationException ex) {
            // Error instantiating the exception
            log.error("makeThrowable", "Error instantiating the exception", ex);

        } catch (final IllegalAccessException ex) {
            // Can't access the class
            log.error("makeThrowable", "Can't instantiate the exception", ex);

        } catch (final InvocationTargetException ex) {
            // Error calling the exception constructor
            log.error("makeThrowable",
                    "Error calling the exception constructor", ex);
        }

        return null;
    }

    /**
     * Create a proxy for communicating with the remote service.
     *
     * @param aKey
     *            the remote object key
     * @param aClass
     *            the class of the interface the remote object should adhere to
     * @return created proxy
     */
    public Object openProxy(final String aKey, final ClassLoader aClassLoader,
            final Class<?>[] aClasses) {

        final Object proxy = java.lang.reflect.Proxy.newProxyInstance(
                aClassLoader, aClasses, this);
        pProxyMap.put(proxy, aKey);
        return proxy;
    }

    /**
     * Generate and throw exception based on the data in the 'responseMessage'
     *
     * @throws Throwable
     *             Throws the correct exception object, or an
     *             {@link ErrorResponse}.
     */
    protected void processException(final JSONObject responseMessage)
            throws Throwable {

        final JSONObject error = (JSONObject) responseMessage.get("error");
        if (error != null) {
            final Integer code = new Integer(
                    error.has("code") ? error.getInt("code") : 0);

            final String trace = error.has("trace") ? error.getString("trace")
                    : null;

            String msg;
            if (error.has("message")) {
                msg = error.getString("message");
            } else if (error.has("msg")) {
                msg = error.getString("msg");
            } else {
                msg = null;
            }

            Throwable throwable = null;
            if (error.has("javaClass")) {
                // Throw an exception according to the javaClass entry
                final String exceptionClass = error.getString("javaClass");

                // Make a complete message
                final StringBuilder traceMessage = new StringBuilder(msg);
                if (trace != null) {
                    traceMessage.append("\nTrace:\n").append(trace);
                }

                // Instantiate the exception object
                throwable = makeThrowable(exceptionClass,
                        traceMessage.toString());
            }

            if (throwable == null) {
                // Default Jabsorb exception
                throwable = new ErrorResponse(code, msg, trace);
            }

            throw throwable;

        } else {
            throw new ErrorResponse(JSONRPCResult.CODE_ERR_PARSE,
                    "Unknown response: " + responseMessage.toString(2), null);
        }
    }
}
