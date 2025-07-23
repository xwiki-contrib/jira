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

import java.util.List;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.xwiki.contrib.jira.charts.bidimensionalgrid.JIRABiDimensionalGridChartMacroParameter;
import org.xwiki.contrib.jira.charts.bidimensionalgrid.JIRAField;
import org.xwiki.contrib.jira.charts.internal.bidimensionalgrid.source.JIRABiDimensionalGridChartJIRADataSource;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.internal.HTTPJIRAFetcher;
import org.xwiki.contrib.jira.macro.internal.JIRAURLHelper;
import org.xwiki.contrib.jira.macro.internal.source.JIRAServerResolver;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link JIRABiDimensionalGridChartDataFetcher}.
 *
 * @version $Id$
 */
@ComponentTest
class JIRABiDimensionalGridChartDataFetcherTest
{
    @InjectMockComponents
    private JIRABiDimensionalGridChartDataFetcher dataFetcher;

    @MockComponent
    private JIRAURLHelper urlHelper;

    @MockComponent
    private HTTPJIRAFetcher httpjiraFetcher;

    @MockComponent
    private JIRAServerResolver jiraServerResolver;

    @Mock
    JIRAServer jiraServer;

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
        List<NameValuePair> expectedParameters = List.of(
            new BasicNameValuePair("filterId", "filter-43434"),
            new BasicNameValuePair("xstattype", "statuses"),
            new BasicNameValuePair("ystattype", "assignees"),
            new BasicNameValuePair("sortDirection", "desc"),
            new BasicNameValuePair("sortBy", "natural"),
            new BasicNameValuePair("numberToShow", "21")
        );

        when(this.urlHelper.getChartURL(any(), any(), any())).thenReturn("myurl");

        JIRABiDimensionalGridChartJIRADataSource expectedResult = mock(JIRABiDimensionalGridChartJIRADataSource.class);
        when(this.httpjiraFetcher.fetchJSON("myurl", this.jiraServer, JIRABiDimensionalGridChartJIRADataSource.class))
            .thenReturn(expectedResult);

        assertSame(expectedResult, this.dataFetcher.fetch(parameters, JIRABiDimensionalGridChartJIRADataSource.class));
        verify(this.urlHelper).getChartURL(jiraServer, JIRAURLHelper.GadgetType.BIDIMENSIONAL_CHARTS,
            expectedParameters);

    }
}