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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.charts.ByType;
import org.xwiki.contrib.jira.charts.internal.source.JIRAPieChartDataSource;
import org.xwiki.contrib.jira.charts.JIRAPieChartMacroParameters;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.internal.HTTPJIRAFetcher;
import org.xwiki.contrib.jira.macro.internal.JIRAURLHelper;
import org.xwiki.contrib.jira.macro.internal.source.JIRAServerResolver;
import org.xwiki.rendering.macro.MacroExecutionException;

@Component(roles = JIRAPieChartDataFetcher.class)
@Singleton
public class JIRAPieChartDataFetcher
{
    @Inject
    private JIRAURLHelper urlHelper;

    @Inject
    private HTTPJIRAFetcher httpjiraFetcher;

    @Inject
    private JIRAServerResolver jiraServerResolver;

    public JIRAPieChartDataSource fetch(JIRAPieChartMacroParameters parameters) throws MacroExecutionException
    {
        Map<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("jql", parameters.getQuery());
        ByType type = parameters.getType();
        String statType = type.getQueryName();
        if (type == ByType.CUSTOM) {
            statType = parameters.getCustomType();
        }
        queryParams.put("statType", statType);

        JIRAServer server = this.jiraServerResolver.resolve(parameters);
        String chartURL = this.urlHelper.getChartURL(server, JIRAURLHelper.GadgetType.PIE_CHART, queryParams);
        try {
            return this.httpjiraFetcher.fetchJSON(chartURL, server, JIRAPieChartDataSource.class);
        } catch (Exception e) {
            throw new MacroExecutionException(
                String.format("Error when trying to get data for pie chart from URL [%s].", chartURL), e);
        }
    }
}
