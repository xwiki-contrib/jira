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

import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.xwiki.contrib.jira.config.internal.BasicAuthJIRAAuthenticator;
import org.xwiki.text.XWikiToStringBuilder;

/**
 * Represents all data related to a JIRA server: url and credentials to access it.
 *
 * @version $Id$
 * @since 8.2
 */
public class JIRAServer
{
    private String id;

    private String url;

    private JIRAAuthenticator jiraAuthenticator;

    /**
     * Public-access JIRA server.
     *
     * @param url see {@link #getURL()}
     * @param id see {@link #getId()}
     */
    public JIRAServer(String url, String id)
    {
        this.url = url;
        this.id = id;
    }

    /**
     * Credential-protected JIRA server.
     * @deprecated since 11.0.0. Use {@link #JIRAServer(String, String, JIRAAuthenticator)} instead.
     *
     * @param url see {@link #getURL()}
     * @param username basic username.
     * @param password basic password.
     */
    @Deprecated
    public JIRAServer(String url, String username, String password)
    {
        this(url, "", new BasicAuthJIRAAuthenticator(username, password));
    }

    /**
     * Constructor for JIRA server which will send the requests with authentication.
     * @since 11.0.0
     *
     * @param url see {@link #getURL()}
     * @param id see {@link #getId()}
     * @param jiraAuthenticator see {@link #getJiraAuthenticator()}
     */
    public JIRAServer(String url, String id, JIRAAuthenticator jiraAuthenticator)
    {
        this(url, id);
        this.jiraAuthenticator = jiraAuthenticator;
    }

    /**
     * @return the JIRA server's url prefix (e.g. {@code https://jira.xwiki.org})
     */
    public String getURL()
    {
        return this.url;
    }

    /**
     * @return the ID of the server configuration.
     */
    public String getId()
    {
        return id;
    }

    /**
     * @return the corresponding JIRA authenticator if a authenticator is configured. If no authenticator is set it mean
     *     that the request is expected to be sent without any authentication with the public rights.
     */
    public Optional<JIRAAuthenticator> getJiraAuthenticator()
    {
        return Optional.ofNullable(jiraAuthenticator);
    }

    @Override
    public String toString()
    {
        ToStringBuilder builder = new XWikiToStringBuilder(this);
        builder = builder.append("URL", getURL());
        if (jiraAuthenticator != null) {
            builder = builder.append("authenticator", jiraAuthenticator.getClass());
        }
        return builder.toString();
    }
}
