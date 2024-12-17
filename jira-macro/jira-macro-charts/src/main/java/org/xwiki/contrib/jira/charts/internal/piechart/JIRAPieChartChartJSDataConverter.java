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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.charts.internal.JIRADataChartJSDataConverter;
import org.xwiki.contrib.jira.charts.internal.display.ChartJSDataSource;
import org.xwiki.contrib.jira.charts.internal.display.ChartJSDataSetSingleData;
import org.xwiki.contrib.jira.charts.internal.piechart.source.JIRAPieChartData;
import org.xwiki.contrib.jira.charts.internal.piechart.source.JIRAPieChartDataSource;
import org.xwiki.contrib.jira.charts.piechart.JIRAPieChartMacroParameters;

/**
 * Dedicated converter to transform the pie chart data information obtained from JIRA to ChartJS piechart data.
 *
 * @version $Id$
 * @since 9.1
 */
@Component
@Singleton
public class JIRAPieChartChartJSDataConverter
    implements JIRADataChartJSDataConverter<JIRAPieChartDataSource, JIRAPieChartMacroParameters>
{
    @Override
    public ChartJSDataSource convert(JIRAPieChartDataSource dataSource, JIRAPieChartMacroParameters parameters)
    {
        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        for (JIRAPieChartData result : dataSource.getResults()) {
            labels.add(result.getKey());
            values.add(result.getValue());
        }

        ChartJSDataSource result = new ChartJSDataSource();
        result.setLabels(labels);

        ChartJSDataSetSingleData singleData = new ChartJSDataSetSingleData();
        singleData.setLabel(dataSource.getFilterTitle());
        singleData.setData(values);
        result.setDatasets(Collections.singletonList(singleData));

        return result;
    }
}
