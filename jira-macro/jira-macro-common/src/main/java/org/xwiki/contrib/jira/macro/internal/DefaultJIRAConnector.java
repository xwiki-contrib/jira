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
package org.xwiki.contrib.jira.macro.internal;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.hc.core5.http.NameValuePair;
import org.jdom2.Document;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.JIRAConnectionException;
import org.xwiki.contrib.jira.macro.JIRAConnector;
import org.xwiki.contrib.jira.macro.JIRAGadgetType;

/**
 * Get the data from a JIRA Data Center or Server instance, using its XML search view and its REST API.
 *
 * @version $Id$
 * @since 11.2.0
 */
@Component
@Named(JIRAServer.DEFAULT_INSTANCE_TYPE)
@Singleton
public class DefaultJIRAConnector implements JIRAConnector
{
    @Inject
    private JIRAURLHelper urlHelper;

    @Inject
    private HTTPJIRAFetcher jiraFetcher;

    @Override
    public Document searchIssues(JIRAServer server, String jql, int maxCount) throws JIRAConnectionException
    {
        return this.jiraFetcher.fetch(this.urlHelper.getSearchURL(server, jql, maxCount), server);
    }

    @Override
    public Integer countIssues(JIRAServer server, String jql) throws JIRAConnectionException
    {
        return this.jiraFetcher.fetchJSON(this.urlHelper.getRestSearchURL(server, jql), server,
            JIRASearchResult.class).getTotal();
    }

    @Override
    public <T> T fetchGadgetData(JIRAServer server, JIRAGadgetType type, List<NameValuePair> parameters,
        Class<T> expectedType) throws JIRAConnectionException
    {
        return this.jiraFetcher.fetchJSON(this.urlHelper.getChartURL(server, type, parameters), server,
            expectedType);
    }
}
