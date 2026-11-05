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
package org.xwiki.contrib.jira.charts.internal.createdvsresolved;

import java.util.List;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.xwiki.contrib.jira.charts.createdvsresolved.ChartPeriod;
import org.xwiki.contrib.jira.charts.createdvsresolved.DisplayVersion;
import org.xwiki.contrib.jira.charts.createdvsresolved.JIRACreatedVsResolvedMacroParameters;
import org.xwiki.contrib.jira.charts.internal.createdvsresolved.source.JIRACreatedVsResolvedDataSource;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.JIRAConnector;
import org.xwiki.contrib.jira.macro.JIRAGadgetType;
import org.xwiki.contrib.jira.macro.internal.JIRAConnectorManager;
import org.xwiki.contrib.jira.macro.internal.source.JIRAServerResolver;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link JIRACreatedVsResolvedDataFetcher}.
 *
 * @version $Id$
 */
@ComponentTest
class JIRACreatedVsResolvedDataFetcherTest
{
    @InjectMockComponents
    private JIRACreatedVsResolvedDataFetcher dataFetcher;

    @MockComponent
    private JIRAConnectorManager connectorManager;

    @MockComponent
    private JIRAServerResolver jiraServerResolver;

    @Mock
    JIRAServer jiraServer;

    @Mock
    JIRAConnector connector;

    @Test
    void fetch() throws Exception
    {
        JIRACreatedVsResolvedMacroParameters parameters = new JIRACreatedVsResolvedMacroParameters();
        parameters.setId("myjira");
        parameters.setFilterId("filter-43434");
        parameters.setCount(true);
        parameters.setDaysPreviously(23);
        parameters.setPeriod(ChartPeriod.HOURLY);
        parameters.setDisplayTrend(true);
        parameters.setDisplayVersion(DisplayVersion.ONLY_MAJOR);

        when(this.jiraServerResolver.resolve(any())).thenReturn(this.jiraServer);
        List<NameValuePair> expectedParameters = List.of(
            new BasicNameValuePair("jql", "filter=43434"),
            new BasicNameValuePair("field", "created"),
            new BasicNameValuePair("field", "resolved"),
            new BasicNameValuePair("field", "unresolvedTrend"),
            new BasicNameValuePair("period", "hourly"),
            new BasicNameValuePair("operation", "count"),
            new BasicNameValuePair("daysprevious", "23"),
            new BasicNameValuePair("includeVersions", "true")
        );

        when(this.connectorManager.getConnector(this.jiraServer)).thenReturn(this.connector);

        JIRACreatedVsResolvedDataSource expectedResult = mock(JIRACreatedVsResolvedDataSource.class);
        when(this.connector.fetchGadgetData(this.jiraServer, JIRAGadgetType.CREATED_VS_RESOLVED, expectedParameters,
            JIRACreatedVsResolvedDataSource.class)).thenReturn(expectedResult);

        assertSame(expectedResult, this.dataFetcher.fetch(parameters, JIRACreatedVsResolvedDataSource.class));
    }
}