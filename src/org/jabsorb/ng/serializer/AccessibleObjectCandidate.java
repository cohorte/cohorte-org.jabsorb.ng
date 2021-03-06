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

package org.jabsorb.ng.serializer;

import java.lang.reflect.AccessibleObject;
import java.util.Arrays;

/**
 * Used to determine whether two methods match
 */
public class AccessibleObjectCandidate {

    /**
     * The method/constructor
     */
    private final AccessibleObject accessibleObject;

    /**
     * The match data for each parameter of the method.
     */
    private final ObjectMatch match[];

    /**
     * The parameters of the accessibleObject
     */
    private final Class<?>[] parameterTypes;

    /**
     * Creates a new MethodCandidate
     * 
     * @param accessibleObject
     *            The method/constructor for this candidate
     * @param parameterTypes
     *            The parameters of the accessibleObject
     * @param matches
     *            How well this matches the requested method/constructor
     */
    public AccessibleObjectCandidate(final AccessibleObject accessibleObject,
            final Class<?>[] parameterTypes, final ObjectMatch[] matches) {

        if (parameterTypes.length != matches.length) {
            throw new ArrayIndexOutOfBoundsException(
                    "parameter types and matches need to be of the same size");
        }

        this.accessibleObject = accessibleObject;
        this.parameterTypes = Arrays.copyOf(parameterTypes,
                parameterTypes.length);
        this.match = Arrays.copyOf(matches, matches.length);
    }

    /**
     * Gets the method/constructor
     * 
     * @return Method or Constructor
     */
    public AccessibleObject getAccessibleObject() {

        return accessibleObject;
    }

    /**
     * Gets an object Match for the method.
     * 
     * @return An object match with the amount of mismatches
     */
    public ObjectMatch getMatch() {

        // TODO: Why this hard coded value?? Wouldn't it be better to say OKAY?
        int mismatch = ObjectMatch.OKAY.getMismatch();
        for (int i = 0; i < match.length; i++) {
            mismatch = Math.max(mismatch, match[i].getMismatch());
        }
        // TODO: Comparing like this is quite dodgy!
        if (mismatch == ObjectMatch.OKAY.getMismatch()) {
            return ObjectMatch.OKAY;
        }
        if (mismatch == ObjectMatch.SIMILAR.getMismatch()) {
            return ObjectMatch.SIMILAR;
        }
        if (mismatch == ObjectMatch.ROUGHLY_SIMILAR.getMismatch()) {
            return ObjectMatch.ROUGHLY_SIMILAR;
        }
        return new ObjectMatch(mismatch);
    }

    /**
     * Gets the parameter types for the method/constructor
     * 
     * @return The parameter types
     */
    public Class<?>[] getParameterTypes() {

        return parameterTypes;
    }
}
