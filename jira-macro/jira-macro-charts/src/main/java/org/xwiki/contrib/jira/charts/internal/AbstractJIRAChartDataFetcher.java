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
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xwiki.contrib.jira.charts.AbstractChartMacroParameters;
import org.xwiki.contrib.jira.charts.internal.source.AbstractJIRADataSource;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.internal.HTTPJIRAFetcher;
import org.xwiki.contrib.jira.macro.internal.JIRAURLHelper;
import org.xwiki.contrib.jira.macro.internal.source.JIRAServerResolver;
import org.xwiki.rendering.macro.MacroExecutionException;

/**
 * Abstract implementation of components allowing to request information from JIRA REST gadget endpoint.
 *
 * @param <T> the type of the macro parameters for which to request information.
 * @param <U> the type of the JIRA data source to obtain.
 *
 * @version $Id$
 * @since 10.0
 */
public abstract class AbstractJIRAChartDataFetcher<T extends AbstractChartMacroParameters,
    U extends AbstractJIRADataSource> implements JIRAChartDataFetcher<T, U>
{
    private static final String FILTER_ID_PREFIX = "filter-";

    @Inject
    private JIRAURLHelper urlHelper;

    @Inject
    private HTTPJIRAFetcher httpjiraFetcher;

    @Inject
    private JIRAServerResolver jiraServerResolver;

    @Override
    public U fetch(T parameters, Class<U> expectedType) throws MacroExecutionException
    {
        List<NameValuePair> parametersList = new ArrayList<>();

        if (!StringUtils.isEmpty(parameters.getFilterId())) {
            parametersList.add(handleFilterIdParameter(parameters.getFilterId()));
        } else {
            parametersList.add(handleQueryParameter(parameters.getQuery()));
        }
        parametersList.addAll(getCustomQueryParameters(parameters));

        JIRAServer server = this.jiraServerResolver.resolve(parameters);
        String chartURL = this.urlHelper.getChartURL(server, getGadgetType(), parametersList);
        try {
            return this.httpjiraFetcher.fetchJSON(chartURL, server, expectedType);
        } catch (Exception e) {
            throw new MacroExecutionException(
                String.format("Error when trying to get data for pie chart from URL [%s].", chartURL), e);
        }
    }

    /**
     * Define the format of  the filter ID value to be given to JIRA REST API. It accepts sometimes {@code filter
     * -XXX} and sometimes only {@code XXX}.
     *
     * @param providedFilterId the provided value
     * @param shoudUsePrefix {@code true} if {@code filter-} prefix is needed and should be added if missing, {@code
     * false} to remove that prefix if provided
     * @return the filter value to use in calls.
     */
    public String computeFilterIdFormat(String providedFilterId, boolean shoudUsePrefix)
    {
        if (shoudUsePrefix && !providedFilterId.startsWith(FILTER_ID_PREFIX)) {
            return FILTER_ID_PREFIX + providedFilterId;
        } else if (!shoudUsePrefix && providedFilterId.startsWith(FILTER_ID_PREFIX)) {
            return providedFilterId.substring(FILTER_ID_PREFIX.length());
        } else {
            return providedFilterId;
        }
    }

    /**
     * Define how to handle the filterId value.
     * @param filterIdValue the value given by the user
     * @return the {@link NameValuePair} used to perform the REST call.
     */
    public NameValuePair handleFilterIdParameter(String filterIdValue)
    {
        return new BasicNameValuePair("filterId", computeFilterIdFormat(filterIdValue, false));
    }

    /**
     * Define how to handle the query parameter.
     * @param queryValue the value given by the user
     * @return the {@link NameValuePair} used to perform the REST call.
     */
    public NameValuePair handleQueryParameter(String queryValue)
    {
        return new BasicNameValuePair("jql", queryValue);
    }

    /**
     * @return the type of requested gadget.
     */
    public abstract JIRAURLHelper.GadgetType getGadgetType();

    /**
     * @param parameters the macro parameters to use for building the request.
     * @return the list of parameters that should be used for creating the URL query string.
     */
    public abstract List<NameValuePair> getCustomQueryParameters(T parameters);
}
