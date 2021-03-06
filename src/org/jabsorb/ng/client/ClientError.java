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

/**
 * A class to wrap client exception. Derives from RuntimeException to allow
 * InvokationHandler handle it.
 */
public class ClientError extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ClientError(final String aMessage) {

        super(aMessage);
    }

    public ClientError(final String aMessage, final Throwable aThrowable) {

        super(aMessage, aThrowable);
    }

    public ClientError(final Throwable aThrowable) {

        super(aThrowable);
    }
}
