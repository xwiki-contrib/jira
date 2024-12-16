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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.charts.createdvsresolved.DisplayVersion;
import org.xwiki.contrib.jira.charts.createdvsresolved.JIRACreatedVsResolvedMacroParameters;
import org.xwiki.contrib.jira.charts.internal.AbstractJIRAChartDataFetcher;
import org.xwiki.contrib.jira.charts.internal.createdvsresolved.source.JIRACreatedVsResolvedDataSource;
import org.xwiki.contrib.jira.charts.internal.piechart.source.JIRAPieChartDataSource;
import org.xwiki.contrib.jira.charts.piechart.ByType;
import org.xwiki.contrib.jira.charts.piechart.JIRAPieChartMacroParameters;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.internal.HTTPJIRAFetcher;
import org.xwiki.contrib.jira.macro.internal.JIRAURLHelper;
import org.xwiki.contrib.jira.macro.internal.source.JIRAServerResolver;
import org.xwiki.rendering.macro.MacroExecutionException;

@Component
@Singleton
public class JIRACreatedVsResolvedDataFetcher
    extends AbstractJIRAChartDataFetcher<JIRACreatedVsResolvedMacroParameters, JIRACreatedVsResolvedDataSource>
{
    @Override
    public JIRAURLHelper.GadgetType getGadgetType()
    {
        return JIRAURLHelper.GadgetType.CREATED_VS_RESOLVED;
    }

    @Override
    public List<NameValuePair> getCustomQueryParameters(JIRACreatedVsResolvedMacroParameters parameters)
    {
        List<NameValuePair> result = new ArrayList<>();
        result.add(new BasicNameValuePair("field", "created"));
        result.add(new BasicNameValuePair("field", "resolved"));
        if (parameters.isDisplayTrend()) {
            result.add(new BasicNameValuePair("field", "unresolvedTrend"));
        }
        result.add(new BasicNameValuePair("period", parameters.getPeriod().name().toLowerCase(Locale.ROOT)));
        String operation = (parameters.isCount()) ? "count" : "cumulative";
        result.add(new BasicNameValuePair("operation", operation));
        result.add(new BasicNameValuePair("daysprevious", String.valueOf(parameters.getDaysPreviously())));
        if (parameters.getDisplayVersion() != DisplayVersion.NONE) {
            result.add(new BasicNameValuePair("includeVersions", "true"));
        }
        return result;
    }
}
