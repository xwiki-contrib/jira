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

import javax.inject.Singleton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.charts.bidimensionalgrid.JIRABiDimensionalGridChartMacroParameter;
import org.xwiki.contrib.jira.charts.internal.AbstractJIRAChartDataFetcher;
import org.xwiki.contrib.jira.charts.internal.bidimensionalgrid.source.JIRABiDimensionalGridChartJIRADataSource;
import org.xwiki.contrib.jira.macro.internal.JIRAURLHelper;

/**
 * Dedicated data fetcher for the bidimensional table chart specific gadget endpoint.
 *
 * @version $Id$
 * @since 10.0
 */
@Component
@Singleton
public class JIRABiDimensionalGridChartDataFetcher
    extends AbstractJIRAChartDataFetcher<JIRABiDimensionalGridChartMacroParameter, JIRABiDimensionalGridChartJIRADataSource>
{
    @Override
    public JIRAURLHelper.GadgetType getGadgetType()
    {
        return JIRAURLHelper.GadgetType.BIDIMENSIONAL_CHARTS;
    }

    @Override
    public List<NameValuePair> getCustomQueryParameters(JIRABiDimensionalGridChartMacroParameter parameters)
    {
        List<NameValuePair> result = new ArrayList<>();
        result.add(new BasicNameValuePair("xstattype", parameters.getxAxisField().getQueryName()));
        result.add(new BasicNameValuePair("ystattype", parameters.getyAxisField().getQueryName()));
        String sortDirection = (parameters.isSortAscending()) ? "asc" : "desc";
        result.add(new BasicNameValuePair("sortDirection", sortDirection));
        String sortBy = (parameters.isSortNatural()) ? "natural" : "total";
        result.add(new BasicNameValuePair("sortBy", sortBy));
        result.add(new BasicNameValuePair("numberToShow", String.valueOf(parameters.getNumberOfResults())));
        return result;
    }
}
