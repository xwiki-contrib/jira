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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.jdom2.Document;
import org.jdom2.Element;
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
    public List<Element> searchIssues(JIRAServer server, String jql, int maxCount) throws JIRAConnectionException
    {
        Document document = this.jiraFetcher.fetch(this.urlHelper.getSearchURL(server, jql, maxCount), server);

        Element channel = document.getRootElement().getChild("channel");
        return channel != null ? new ArrayList<>(channel.getChildren("item")) : new ArrayList<>();
    }

    @Override
    public long countIssues(JIRAServer server, String jql) throws JIRAConnectionException
    {
        return this.jiraFetcher.fetchJSON(this.urlHelper.getRestSearchURL(server, jql), server,
            JIRASearchResult.class).getTotal();
    }

    @Override
    public <T> T fetchGadgetData(JIRAServer server, JIRAGadgetType type, Map<String, List<String>> parameters,
        Class<T> expectedType) throws JIRAConnectionException
    {
        List<NameValuePair> queryParameters = new ArrayList<>();
        parameters.forEach((name, values) -> values.forEach(value ->
            queryParameters.add(new BasicNameValuePair(name, value))));
        return this.jiraFetcher.fetchJSON(this.urlHelper.getChartURL(server, type, queryParameters), server,
            expectedType);
    }
}
