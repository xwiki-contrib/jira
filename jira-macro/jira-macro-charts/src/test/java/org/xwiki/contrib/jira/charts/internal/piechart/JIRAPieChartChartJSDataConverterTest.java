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
package org.xwiki.contrib.jira.charts.internal.piechart;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.xwiki.contrib.jira.charts.internal.display.ChartJSDataSetSingleData;
import org.xwiki.contrib.jira.charts.internal.display.ChartJSDataSource;
import org.xwiki.contrib.jira.charts.internal.piechart.source.JIRAPieChartData;
import org.xwiki.contrib.jira.charts.internal.piechart.source.JIRAPieChartDataSource;
import org.xwiki.contrib.jira.charts.piechart.JIRAPieChartMacroParameters;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link JIRAPieChartChartJSDataConverter}.
 *
 * @version $Id$
 */
@ComponentTest
class JIRAPieChartChartJSDataConverterTest
{
    @InjectMockComponents
    private JIRAPieChartChartJSDataConverter converter;

    @Test
    void convert()
    {
        JIRAPieChartMacroParameters parameters = new JIRAPieChartMacroParameters();
        parameters.setMaxData(3);

        JIRAPieChartDataSource dataSource = new JIRAPieChartDataSource();
        dataSource.setFilterTitle("Filter title foo");
        JIRAPieChartData data1 = new JIRAPieChartData();
        JIRAPieChartData data2 = new JIRAPieChartData();
        JIRAPieChartData data3 = new JIRAPieChartData();
        JIRAPieChartData data4 = new JIRAPieChartData();
        JIRAPieChartData data5 = new JIRAPieChartData();

        data1.setKey("key1");
        data2.setKey("key2");
        data3.setKey("key3");
        data4.setKey("key4");
        data5.setKey("key5");

        data1.setValue(1);
        data2.setValue(22);
        data3.setValue(8);
        data4.setValue(4);
        data5.setValue(14);

        dataSource.setResults(List.of(data1, data2, data3, data4, data5));

        ChartJSDataSource expectedResult = new ChartJSDataSource();
        expectedResult.setLabels(List.of("key2", "key5", "key3", "Other data"));
        ChartJSDataSetSingleData singleData = new ChartJSDataSetSingleData();
        singleData.setLabel("Filter title foo");
        singleData.setData(List.of(22L, 14L, 8L, 5L));
        expectedResult.setDatasets(List.of(singleData));

        assertEquals(expectedResult, this.converter.convert(dataSource,parameters));
    }
}