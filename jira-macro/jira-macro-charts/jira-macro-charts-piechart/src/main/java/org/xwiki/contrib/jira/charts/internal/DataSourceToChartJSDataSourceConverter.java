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
package org.xwiki.contrib.jira.charts.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.charts.internal.display.ChartJSPieChartDataSource;
import org.xwiki.contrib.jira.charts.internal.display.ChartJSPieChartSingleData;
import org.xwiki.contrib.jira.charts.internal.source.JIRAPieChartData;
import org.xwiki.contrib.jira.charts.internal.source.JIRAPieChartDataSource;

@Component(roles = DataSourceToChartJSDataSourceConverter.class)
@Singleton
public class DataSourceToChartJSDataSourceConverter
{
    ChartJSPieChartDataSource convert(JIRAPieChartDataSource dataSource)
    {
        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        for (JIRAPieChartData result : dataSource.getResults()) {
            labels.add(result.getKey());
            values.add(result.getValue());
        }

        ChartJSPieChartDataSource result = new ChartJSPieChartDataSource();
        result.setLabels(labels);

        ChartJSPieChartSingleData singleData = new ChartJSPieChartSingleData();
        singleData.setLabel(dataSource.getFilterTitle());
        singleData.setData(values);
        result.setDatasets(Collections.singletonList(singleData));

        return result;
    }
}
