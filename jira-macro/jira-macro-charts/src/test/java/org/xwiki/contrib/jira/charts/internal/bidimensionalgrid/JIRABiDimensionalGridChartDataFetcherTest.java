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
package org.xwiki.contrib.jira.charts.internal.bidimensionalgrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.xwiki.contrib.jira.charts.bidimensionalgrid.JIRABiDimensionalGridChartMacroParameter;
import org.xwiki.contrib.jira.charts.bidimensionalgrid.JIRAField;
import org.xwiki.contrib.jira.charts.internal.bidimensionalgrid.source.JIRABiDimensionalGridChartJIRADataSource;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.JIRAConnector;
import org.xwiki.contrib.jira.macro.JIRAGadgetType;
import org.xwiki.contrib.jira.macro.internal.JIRAConnectorManager;
import org.xwiki.contrib.jira.macro.internal.source.JIRAServerResolver;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link JIRABiDimensionalGridChartDataFetcher}.
 *
 * @version $Id$
 */
@ComponentTest
class JIRABiDimensionalGridChartDataFetcherTest
{
    private final JIRAServer jiraServer = new JIRAServer("http://localhost/jira", "myjira");

    @InjectMockComponents
    private JIRABiDimensionalGridChartDataFetcher dataFetcher;

    @MockComponent
    private JIRAConnectorManager connectorManager;

    @MockComponent
    private JIRAServerResolver jiraServerResolver;

    @Mock
    private JIRAConnector connector;

    @Test
    void fetch() throws Exception
    {
        JIRABiDimensionalGridChartMacroParameter parameters = new JIRABiDimensionalGridChartMacroParameter();
        parameters.setId("myjira");
        parameters.setFilterId("43434");
        parameters.setxAxisField(JIRAField.STATUS);
        parameters.setyAxisField(JIRAField.ASSIGNEE);
        parameters.setSortNatural(true);
        parameters.setNumberOfResults(21);
        parameters.setSortAscending(false);

        when(this.jiraServerResolver.resolve(any())).thenReturn(this.jiraServer);
        when(this.connectorManager.getConnector(this.jiraServer)).thenReturn(this.connector);
        JIRABiDimensionalGridChartJIRADataSource expectedResult = mock(JIRABiDimensionalGridChartJIRADataSource.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, List<String>>> parametersCaptor = ArgumentCaptor.forClass(Map.class);
        when(this.connector.fetchGadgetData(eq(this.jiraServer), eq(JIRAGadgetType.BIDIMENSIONAL_CHARTS),
            parametersCaptor.capture(), eq(JIRABiDimensionalGridChartJIRADataSource.class))).thenReturn(expectedResult);

        assertSame(expectedResult, this.dataFetcher.fetch(parameters, JIRABiDimensionalGridChartJIRADataSource.class));

        assertEquals(List.of(
            Map.entry("filterId", List.of("filter-43434")),
            Map.entry("xstattype", List.of("statuses")),
            Map.entry("ystattype", List.of("assignees")),
            Map.entry("sortDirection", List.of("desc")),
            Map.entry("sortBy", List.of("natural")),
            Map.entry("numberToShow", List.of("21"))
        ), new ArrayList<>(parametersCaptor.getValue().entrySet()));
    }
}
