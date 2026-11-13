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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.xwiki.contrib.jira.charts.createdvsresolved.ChartPeriod;
import org.xwiki.contrib.jira.charts.createdvsresolved.DisplayVersion;
import org.xwiki.contrib.jira.charts.createdvsresolved.JIRACreatedVsResolvedMacroParameters;
import org.xwiki.contrib.jira.charts.internal.createdvsresolved.source.JIRACreatedVsResolvedDataSource;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.JIRABadRequestException;
import org.xwiki.contrib.jira.macro.JIRAChartType;
import org.xwiki.contrib.jira.macro.JIRAConnectionException;
import org.xwiki.contrib.jira.macro.JIRAConnector;
import org.xwiki.contrib.jira.macro.internal.JIRAConnectorResolver;
import org.xwiki.contrib.jira.macro.internal.source.JIRAServerResolver;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link JIRACreatedVsResolvedDataFetcher}.
 *
 * @version $Id$
 */
@ComponentTest
class JIRACreatedVsResolvedDataFetcherTest
{
    private final JIRAServer jiraServer = new JIRAServer("http://localhost/jira", "myjira");

    @InjectMockComponents
    private JIRACreatedVsResolvedDataFetcher dataFetcher;

    @MockComponent
    private JIRAConnectorResolver connectorResolver;

    @MockComponent
    private JIRAServerResolver jiraServerResolver;

    @Mock
    private JIRAConnector connector;

    @BeforeEach
    void setUp() throws Exception
    {
        when(this.jiraServerResolver.resolve(any())).thenReturn(this.jiraServer);
        when(this.connectorResolver.resolve(this.jiraServer)).thenReturn(this.connector);
    }

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

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, List<String>>> parametersCaptor = ArgumentCaptor.forClass(Map.class);
        when(this.connector.getChartData(eq(this.jiraServer), eq(JIRAChartType.CREATED_VS_RESOLVED),
            parametersCaptor.capture())).thenReturn("{\"totals\":{\"created\":5,\"resolved\":2}}");

        JIRACreatedVsResolvedDataSource result =
            this.dataFetcher.fetch(parameters, JIRACreatedVsResolvedDataSource.class);
        assertEquals(5, result.getTotals().getCreated());
        assertEquals(2, result.getTotals().getResolved());

        // The "field" parameter has several values, and the order of the parameters is kept.
        assertEquals(List.of(
            Map.entry("jql", List.of("filter=43434")),
            Map.entry("field", List.of("created", "resolved", "unresolvedTrend")),
            Map.entry("period", List.of("hourly")),
            Map.entry("operation", List.of("count")),
            Map.entry("daysprevious", List.of("23")),
            Map.entry("includeVersions", List.of("true"))
        ), new ArrayList<>(parametersCaptor.getValue().entrySet()));
    }

    @Test
    void fetchWhenConnectionError() throws Exception
    {
        JIRAConnectionException cause = new JIRAConnectionException("error");
        when(this.connector.getChartData(eq(this.jiraServer), eq(JIRAChartType.CREATED_VS_RESOLVED), anyMap()))
            .thenThrow(cause);

        MacroExecutionException exception = assertThrows(MacroExecutionException.class,
            () -> this.dataFetcher.fetch(newParameters(), JIRACreatedVsResolvedDataSource.class));

        assertEquals("Error when trying to get data for chart [CREATED_VS_RESOLVED] from JIRA server "
            + "[http://localhost/jira].", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void fetchWhenBadRequest() throws Exception
    {
        JIRABadRequestException badRequest = new JIRABadRequestException("error", List.of("message"));
        when(this.connector.getChartData(eq(this.jiraServer), eq(JIRAChartType.CREATED_VS_RESOLVED), anyMap()))
            .thenThrow(badRequest);

        // Bad requests are not wrapped, since the chart macros display them in a specific way.
        assertSame(badRequest, assertThrows(JIRABadRequestException.class,
            () -> this.dataFetcher.fetch(newParameters(), JIRACreatedVsResolvedDataSource.class)));
    }

    @Test
    void fetchWhenInvalidJSON() throws Exception
    {
        when(this.connector.getChartData(eq(this.jiraServer), eq(JIRAChartType.CREATED_VS_RESOLVED), anyMap()))
            .thenReturn("not JSON");

        MacroExecutionException exception = assertThrows(MacroExecutionException.class,
            () -> this.dataFetcher.fetch(newParameters(), JIRACreatedVsResolvedDataSource.class));

        assertEquals("Failed to parse the data of chart [CREATED_VS_RESOLVED] from JIRA server "
            + "[http://localhost/jira]: [not JSON]", exception.getMessage());
    }

    private JIRACreatedVsResolvedMacroParameters newParameters()
    {
        JIRACreatedVsResolvedMacroParameters parameters = new JIRACreatedVsResolvedMacroParameters();
        parameters.setQuery("project = TEST");
        parameters.setPeriod(ChartPeriod.DAILY);
        return parameters;
    }
}
