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
package org.xwiki.contrib.jira.config.internal;

import org.apache.hc.client5.http.ContextBuilder;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.core5.http.HttpHost;
import org.xwiki.contrib.jira.config.JIRAAuthenticator;

import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;

/**
 * JIRA authenticator for Basic Auth.
 *
 * @version $Id$
 * @since 11.0.0
 */
public class BasicAuthJIRAAuthenticator implements JIRAAuthenticator
{
    private String username;

    private String password;

    /**
     * @param username basic username.
     * @param password basic password.
     */
    public BasicAuthJIRAAuthenticator(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    @Override
    public AuthenticationHandler getRestClientAuthenticationHandler()
    {
        return new BasicHttpAuthenticationHandler(username, password);
    }

    @Override
    public void authenticateInHttpClient(ContextBuilder context, HttpUriRequest request,
        HttpHost targetHost)
    {
        // Connect to JIRA using basic authentication if username and password are defined
        // Note: Set up preemptive basic authentication since JIRA can accept both unauthenticated and authenticated
        // requests. See https://developer.atlassian.com/server/jira/platform/basic-authentication/
        context.preemptiveBasicAuth(targetHost,
            new UsernamePasswordCredentials(username, password.toCharArray()));
    }

    @Override
    public boolean isAuthenticatingRequest()
    {
        return true;
    }

    @Override
    public String getId()
    {
        return username;
    }
}
