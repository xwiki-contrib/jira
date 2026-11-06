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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.jupiter.api.Test;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.JIRAGadgetType;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DefaultJIRAConnector}. The connector is used end to end by the rendering tests of the macros;
 * these tests only check how it adapts the Data Center answers to the connector API.
 */
@ComponentTest
@ComponentList({
    JIRAURLHelper.class
})
class DefaultJIRAConnectorTest
{
    private static final String SEARCH_URL =
        "http://localhost/jira/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=";

    @InjectMockComponents
    private DefaultJIRAConnector connector;

    @MockComponent
    private HTTPJIRAFetcher jiraFetcher;

    private JIRAServer jiraServer = new JIRAServer("http://localhost/jira", "id");

    @Test
    void searchIssuesReturnsTheItemsInOrder() throws Exception
    {
        Element channel = new Element("channel");
        channel.addContent(new Element("title").setText("not an issue"));
        channel.addContent(new Element("item").addContent(new Element("key").setText("TEST-2")));
        channel.addContent(new Element("item").addContent(new Element("key").setText("TEST-1")));
        when(this.jiraFetcher.fetch(SEARCH_URL + "project+%3D+TEST&tempMax=5", this.jiraServer))
            .thenReturn(new Document(new Element("rss").addContent(channel)));

        List<Element> issues = this.connector.searchIssues(this.jiraServer, "project = TEST", 5);

        assertEquals(List.of("TEST-2", "TEST-1"), issues.stream().map(item -> item.getChildText("key"))
            .collect(Collectors.toList()));
    }

    @Test
    void searchIssuesWhenNoChannel() throws Exception
    {
        when(this.jiraFetcher.fetch(SEARCH_URL + "query", this.jiraServer))
            .thenReturn(new Document(new Element("rss")));

        assertTrue(this.connector.searchIssues(this.jiraServer, "query", -1).isEmpty());
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
    void fetchGadgetDataSendsAllTheValuesOfAParameter() throws Exception
    {
        Map<String, List<String>> parameters = new LinkedHashMap<>();
        parameters.put("jql", List.of("project = TEST"));
        parameters.put("field", List.of("created", "resolved"));
        when(this.jiraFetcher.fetchJSON("http://localhost/jira/rest/gadget/1.0/dateCountInPeriod"
            + "?jql=project+%3D+TEST&field=created&field=resolved", this.jiraServer, String.class)).thenReturn("data");

        assertEquals("data", this.connector.fetchGadgetData(this.jiraServer, JIRAGadgetType.CREATED_VS_RESOLVED,
            parameters, String.class));
    }
}
