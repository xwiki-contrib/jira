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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.charts.createdvsresolved.JIRACreatedVsResolvedMacroParameters;
import org.xwiki.contrib.jira.charts.internal.JIRADataChartJSDataConverter;
import org.xwiki.contrib.jira.charts.internal.createdvsresolved.source.JIRACreatedVsResolvedDataSource;
import org.xwiki.contrib.jira.charts.internal.createdvsresolved.source.JIRACreatedVsResolvedSingleData;
import org.xwiki.contrib.jira.charts.internal.createdvsresolved.source.JIRACreatedVsResolvedTimeData;
import org.xwiki.contrib.jira.charts.internal.display.ChartJSDataSetSingleData;
import org.xwiki.contrib.jira.charts.internal.display.ChartJSDataSource;

/**
 * Converter aiming at producing data for ChartJS macro based on obtained JIRA statistics.
 *
 * @version $Id$
 * @since 10.0
 */
@Component
@Singleton
public class JIRACreatedVsResolvedChartJSDataConverter
    implements JIRADataChartJSDataConverter<JIRACreatedVsResolvedDataSource, JIRACreatedVsResolvedMacroParameters>
{
    @Override
    public ChartJSDataSource convert(JIRACreatedVsResolvedDataSource dataSource,
        JIRACreatedVsResolvedMacroParameters parameters)
    {

        ChartJSDataSource chartJSDataSource = new ChartJSDataSource();
        List<String> labels = new ArrayList<>();
        List<Long> resolvedData = new ArrayList<>();
        List<Long> createdData = new ArrayList<>();
        List<Long> trendData = new ArrayList<>();

        for (JIRACreatedVsResolvedTimeData data : dataSource.getResults()) {
            labels.add(transformDate(data.getStart(), parameters));
            JIRACreatedVsResolvedSingleData singleData = data.getData();
            resolvedData.add(singleData.getResolved().getCount());
            createdData.add(singleData.getCreated().getCount());
            if (parameters.isDisplayTrend()) {
                trendData.add(singleData.getUnresolvedTrend().getCount());
            }
        }

        // FIXME: all labels should use translations.
        chartJSDataSource.setLabels(labels);
        ChartJSDataSetSingleData createdDataSet = new ChartJSDataSetSingleData();
        createdDataSet.setData(createdData);
        createdDataSet.setLabel("Created");
        createdDataSet.setFill(false);

        ChartJSDataSetSingleData resolvedDataSet = new ChartJSDataSetSingleData();
        resolvedDataSet.setData(resolvedData);
        resolvedDataSet.setLabel("Resolved");
        resolvedDataSet.setFill(false);

        List<ChartJSDataSetSingleData> dataSets = new ArrayList<>(List.of(createdDataSet, resolvedDataSet));
        if (parameters.isDisplayTrend()) {
            ChartJSDataSetSingleData trendDataSet = new ChartJSDataSetSingleData();
            trendDataSet.setData(trendData);
            trendDataSet.setLabel("Trend");
            trendDataSet.setFill(false);
            dataSets.add(trendDataSet);
        }

        chartJSDataSource.setDatasets(dataSets);

        return chartJSDataSource;
    }

    private String transformDate(Date date, JIRACreatedVsResolvedMacroParameters parameters)
    {

        SimpleDateFormat dateFormat = new SimpleDateFormat();

        switch (parameters.getPeriod()) {
            case QUARTERLY:
            case MONTHLY:
                dateFormat.applyPattern("yyyy-MM");
                break;

            case YEARLY:
                dateFormat.applyPattern("yyyy");
                break;

            case HOURLY:
                dateFormat.applyPattern("yyyy-MM-dd hh:mm");
                break;

            case WEEKLY:
            case DAILY:
            default:
                // FIXME: we should use date format from XWiki preferences
                dateFormat.applyPattern("yyyy-MM-dd");
                break;
        }

        return dateFormat.format(date);
    }
}
