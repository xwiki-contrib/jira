/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.jira.config;

import org.apache.hc.client5.http.ContextBuilder;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.core5.http.HttpHost;
import org.xwiki.component.annotation.Role;

import com.atlassian.jira.rest.client.api.AuthenticationHandler;

/**
 * JIRA authenticator, to send authenticated request to the target JIRA server.
 *
 * @version $Id$
 * @since 10.3.0
 */
@Role
public interface JIRAAuthenticator
{
    /**
     * @return the Authentication handler instance configured by the authenticator.
     */
    AuthenticationHandler getRestClientAuthenticationHandler();

    /**
     * Add the authentication in the HttpClient request if needed. Depending on the authenticator implementation
     * this might for instance add an Authorization header.
     *
     * @param context the HTTP request context.
     * @param request the HTTP request.
     * @param targetHost the HTTP request target host.
     */
    void authenticateInHttpClient(ContextBuilder context, HttpUriRequest request, HttpHost targetHost);

    /**
     * Provide the information if the {@link JIRAAuthenticator} will authenticate the request or leave the request as it
     * is. Useful in case we need to know if the request will be just with the public right or with a more specific
     * rights.
     *
     * @return true if {@link JIRAAuthenticator} will authenticate the request.
     */
    boolean isAuthenticatingRequest();

    /**
     * Used for async macro call to generate a unique ID.
     *
     * @return the generated ID part related to the authenticator.
     */
    String getId();
}
