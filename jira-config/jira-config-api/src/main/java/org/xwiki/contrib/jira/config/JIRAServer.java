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

import org.apache.commons.lang3.StringUtils;
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
    /**
     * The instance type used when none is configured, i.e. the type handled by this extension itself.
     *
     * @since 11.2.0
     */
    public static final String DEFAULT_INSTANCE_TYPE = "default";

    private String id;

    private String url;

    private JIRAAuthenticator jiraAuthenticator;

    private String instanceType = DEFAULT_INSTANCE_TYPE;

    /**
     * Public-access JIRA server.
     *
     * @param url see {@link #getURL()}
     * @deprecated since 11.0.0. Use {@link #JIRAServer(String, String)} instead.
     */
    @Deprecated
    public JIRAServer(String url)
    {
        this(url, "");
    }

    /**
     * Public-access JIRA server.
     *
     * @param url see {@link #getURL()}
     * @param id see {@link #getId()}
     */
    public JIRAServer(String url, String id)
    {
        this.url = StringUtils.removeEnd(url, "/");
        this.id = id;
    }

    /**
     * Credential-protected JIRA server.
     *
     * @param url see {@link #getURL()}
     * @param username basic username.
     * @param password basic password.
     * @deprecated since 11.0.0. Use {@link #JIRAServer(String, String, JIRAAuthenticator)} instead.
     */
    @Deprecated
    public JIRAServer(String url, String username, String password)
    {
        this(url, "", new BasicAuthJIRAAuthenticator(username, password));
    }

    /**
     * Constructor for JIRA server which will send the requests with authentication.
     *
     * @param url see {@link #getURL()}
     * @param id see {@link #getId()}
     * @param jiraAuthenticator see {@link #getJiraAuthenticator()}
     * @since 11.0.0
     */
    public JIRAServer(String url, String id, JIRAAuthenticator jiraAuthenticator)
    {
        this(url, id);
        this.jiraAuthenticator = jiraAuthenticator;
    }

    /**
     * Constructor for a JIRA server of a given instance type (e.g. a type contributed by another extension through the
     * {@code org.xwiki.contrib.jira.instancetype} UI extension point).
     *
     * @param url see {@link #getURL()}
     * @param id see {@link #getId()}
     * @param jiraAuthenticator see {@link #getJiraAuthenticator()}, can be {@code null} for a public-access server
     * @param instanceType see {@link #getInstanceType()}, {@link #DEFAULT_INSTANCE_TYPE} is used when empty
     * @since 11.2.0
     */
    public JIRAServer(String url, String id, JIRAAuthenticator jiraAuthenticator, String instanceType)
    {
        this(url, id, jiraAuthenticator);

        if (StringUtils.isNotEmpty(instanceType)) {
            this.instanceType = instanceType;
        }
    }

    /**
     * @return the JIRA server's url prefix (e.g. {@code https://jira.xwiki.org})
     */
    public String getURL()
    {
        return url;
    }

    /**
     * @return the ID of the server configuration.
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * @return the corresponding JIRA authenticator if a authenticator is configured. If no authenticator is set it mean
     *     that the request is expected to be sent without any authentication with the public rights.
     */
    public Optional<JIRAAuthenticator> getJiraAuthenticator()
    {
        return Optional.ofNullable(this.jiraAuthenticator);
    }

    /**
     * @return the type of the JIRA instance (e.g. {@link #DEFAULT_INSTANCE_TYPE}), used to find the components and the
     *     UI handling this kind of instance
     * @since 11.2.0
     */
    public String getInstanceType()
    {
        return this.instanceType;
    }

    @Override
    public String toString()
    {
        ToStringBuilder builder = new XWikiToStringBuilder(this);
        builder = builder.append("URL", getURL());
        if (this.id != null) {
            builder = builder.append("id", this.id);
        }
        if (this.jiraAuthenticator != null) {
            builder = builder.append("authenticator", this.jiraAuthenticator.getClass());
        }
        if (!DEFAULT_INSTANCE_TYPE.equals(this.instanceType)) {
            builder = builder.append("instanceType", this.instanceType);
        }
        return builder.toString();
    }
}
