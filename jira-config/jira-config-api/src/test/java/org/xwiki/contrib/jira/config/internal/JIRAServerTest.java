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
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.core5.http.HttpHost;
import org.junit.jupiter.api.Test;
import org.xwiki.contrib.jira.config.JIRAServer;

import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link org.xwiki.contrib.jira.config.JIRAServer}.
 *
 * @version $Id$
 */
class JIRAServerTest
{
    @Test
    void verifyToString()
    {
        JIRAServer server =
            new JIRAServer("https://jira.xwiki.org", "id", new BasicAuthJIRAAuthenticator("username", "password"));
        assertEquals(
            "URL = [https://jira.xwiki.org], id = [id], authenticator = [class org.xwiki.contrib.jira.config.internal.BasicAuthJIRAAuthenticator]",
            server.toString());
    }

    @Test
    void verifyAuthenticator()
    {
        JIRAServer server =
            new JIRAServer("https://jira.xwiki.org", "id", new BasicAuthJIRAAuthenticator("username", "password"));
        assertInstanceOf(BasicAuthJIRAAuthenticator.class, server.getJiraAuthenticator().orElseThrow());
        assertInstanceOf(BasicHttpAuthenticationHandler.class,
            server.getJiraAuthenticator().orElseThrow().getRestClientAuthenticationHandler());
        assertTrue(server.getJiraAuthenticator().orElseThrow().isAuthenticatingRequest());
        assertEquals("username", server.getJiraAuthenticator().orElseThrow().getId());
        ContextBuilder context = ContextBuilder.create();
        HttpGet httpGet = new HttpGet("https://example.com");
        HttpHost targetHost = new HttpHost("example.com");
        server.getJiraAuthenticator().get().authenticateInHttpClient(context, httpGet, targetHost);
        assertInstanceOf(BasicScheme.class,
            context.build().getAuthExchanges().values().stream().findAny().orElseThrow().getAuthScheme());
    }
}
