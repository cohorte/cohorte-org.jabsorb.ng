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

package org.jabsorb.ng.serializer.impl;

import java.util.Iterator;

import org.jabsorb.ng.JSONSerializer;
import org.jabsorb.ng.serializer.AbstractSerializer;
import org.jabsorb.ng.serializer.MarshallException;
import org.jabsorb.ng.serializer.ObjectMatch;
import org.jabsorb.ng.serializer.SerializerState;
import org.jabsorb.ng.serializer.UnmarshallException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Formats the Java JSONObject object.
 */
public class RawJSONObjectSerializer extends AbstractSerializer {

    /**
     * Classes that this can serialise to.
     */
    private static Class<?>[] _JSONClasses = new Class<?>[] { JSONObject.class };

    /**
     * Classes that this can serialise.
     */
    private static Class<?>[] _serializableClasses = new Class<?>[] { JSONObject.class };

    /**
     * Unique serialisation id.
     */
    private static final long serialVersionUID = 2;

    @Override
    public Class<?>[] getJSONClasses() {

        return _JSONClasses;
    }

    @Override
    public Class<?>[] getSerializableClasses() {

        return _serializableClasses;
    }

    @Override
    public Object marshall(final SerializerState state, final Object p,
            final Object o) throws MarshallException {

        // reprocess the raw json in order to fixup circular references and
        // duplicates
        final JSONObject jsonIn = (JSONObject) o;
        final JSONObject jsonOut = new JSONObject();
        String key = null;
        try {
            final Iterator<?> i = jsonIn.keys();
            while (i.hasNext()) {
                key = (String) i.next();

                final Object j = ser.marshall(state, o, jsonIn.get(key), key);

                // omit the object entirely if it's a circular reference or
                // duplicate
                // it will be regenerated in the fixups phase
                if (JSONSerializer.CIRC_REF_OR_DUPLICATE != j) {
                    jsonOut.put(key, j);
                }
            }
        } catch (final MarshallException e) {
            throw new MarshallException("JSONObject key " + key + " "
                    + e.getMessage(), e);
        } catch (final JSONException e) {
            throw new MarshallException("JSONObject key " + key + " "
                    + e.getMessage(), e);
        }
        return jsonOut;
    }

    @Override
    public ObjectMatch tryUnmarshall(final SerializerState state,
            final Class<?> clazz, final Object jso) throws UnmarshallException {

        state.setSerialized(jso, ObjectMatch.OKAY);
        return ObjectMatch.OKAY;
    }

    @Override
    public Object unmarshall(final SerializerState state, final Class<?> clazz,
            final Object jso) throws UnmarshallException {

        state.setSerialized(jso, jso);
        return jso;
    }

}
