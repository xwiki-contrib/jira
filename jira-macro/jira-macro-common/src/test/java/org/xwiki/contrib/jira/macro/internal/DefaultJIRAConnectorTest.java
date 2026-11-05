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

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.jdom2.Document;
import org.junit.jupiter.api.Test;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.JIRAGadgetType;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DefaultJIRAConnector}.
 */
@ComponentTest
@ComponentList({
    JIRAURLHelper.class
})
class DefaultJIRAConnectorTest
{
    @InjectMockComponents
    private DefaultJIRAConnector connector;

    @MockComponent
    private HTTPJIRAFetcher jiraFetcher;

    private JIRAServer jiraServer = new JIRAServer("http://localhost/jira", "id");

    @Test
    void searchIssues() throws Exception
    {
        Document document = new Document();
        when(this.jiraFetcher.fetch("http://localhost/jira/sr/"
            + "jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=project+%3D+TEST&tempMax=5",
            this.jiraServer)).thenReturn(document);

        assertSame(document, this.connector.searchIssues(this.jiraServer, "project = TEST", 5));
    }

    @Test
    void searchIssuesWhenNoLimit() throws Exception
    {
        Document document = new Document();
        when(this.jiraFetcher.fetch("http://localhost/jira/sr/"
            + "jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=query", this.jiraServer))
            .thenReturn(document);

        assertSame(document, this.connector.searchIssues(this.jiraServer, "query", -1));
    }

    @Test
    void countIssues() throws Exception
    {
        JIRASearchResult searchResult = new JIRASearchResult();
        searchResult.setTotal(42);
        when(this.jiraFetcher.fetchJSON("http://localhost/jira/rest/api/2/search?maxResults=0&jql=project+%3D+TEST",
            this.jiraServer, JIRASearchResult.class)).thenReturn(searchResult);

        assertEquals(42, this.connector.countIssues(this.jiraServer, "project = TEST"));
    }

    @Test
    void fetchGadgetData() throws Exception
    {
        List<NameValuePair> parameters = List.of(
            new BasicNameValuePair("jql", "project = TEST"),
            new BasicNameValuePair("statType", "statuses"));
        when(this.jiraFetcher.fetchJSON(
            "http://localhost/jira/rest/gadget/1.0/statistics?jql=project+%3D+TEST&statType=statuses",
            this.jiraServer, String.class)).thenReturn("data");

        assertEquals("data",
            this.connector.fetchGadgetData(this.jiraServer, JIRAGadgetType.PIE_CHART, parameters, String.class));
    }
}
