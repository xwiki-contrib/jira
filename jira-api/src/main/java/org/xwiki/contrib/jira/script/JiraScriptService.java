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
package org.xwiki.contrib.jira.script;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.script.service.ScriptService;

import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

/**
 * Expose Atlassian's JIRA REST service to XWiki scripts.
 *
 * @version $Id$
 * @since 3.2M3
 */
@Component
@Named("jira")
@Singleton
public class JiraScriptService implements ScriptService
{
    /**
     * The logger to log.
     */
    @Inject
    private Logger logger;

    /**
     * Note that when connecting with credentials the password will be passed in clear over the network to the remote
     * JIRA instance. Thus, only use this method when connecting over HTTPS.
     *
     * @param jiraServer the JIRA server definition for the instance to use (url, credentials)
     * @return the client to interact with the remote JIRA instance
     * @since 8.2
     */
    public JiraRestClient getJiraRestClient(JIRAServer jiraServer)
    {
        return getJiraRestClient(jiraServer.getURL(), getAuthenticationHandler(jiraServer));
    }

    /**
     * Releases and clean the JIRA Rest client. Must be called to release resources.
     *
     * @param restClient the client obtained through {@link #getJiraRestClient(JIRAServer)}
     * @since 8.2.2
     */
    public void closeJiraRestClient(JiraRestClient restClient)
    {
        try {
            restClient.close();
        } catch (IOException e) {
            this.logger.warn("Failed to clean JIRA Rest Client [{}]", ExceptionUtils.getRootCause(e));
        }
    }

    private AuthenticationHandler getAuthenticationHandler(JIRAServer jiraServer)
    {
        AuthenticationHandler handler;
        if (StringUtils.isBlank(jiraServer.getUsername()) || StringUtils.isBlank(jiraServer.getPassword())) {
            handler = new AnonymousAuthenticationHandler();
        } else {
            handler = new BasicHttpAuthenticationHandler(jiraServer.getUsername(), jiraServer.getPassword());
        }
        return handler;
    }

    /**
     * @param jiraURL the URL to the remote JIRA instance to connect to
     * @param authenticationHandler the authentication to use (anonymous, basic, etc)
     * @return the client to interact with the remote JIRA instance
     */
    private JiraRestClient getJiraRestClient(String jiraURL, AuthenticationHandler authenticationHandler)
    {
        JiraRestClient restClient;
        try {
            AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
            URI jiraServerUri = new URI(jiraURL);
            restClient = factory.create(jiraServerUri, authenticationHandler);
        } catch (URISyntaxException e) {
            this.logger.warn("Invalid JIRA URL [{}]. Root cause [{}]", jiraURL, ExceptionUtils.getRootCause(e));
            restClient = null;
        }
        return restClient;
    }
}
