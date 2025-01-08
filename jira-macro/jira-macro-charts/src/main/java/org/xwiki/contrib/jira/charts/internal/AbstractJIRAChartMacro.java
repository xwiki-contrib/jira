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

import org.xwiki.contrib.jira.charts.AbstractChartMacroParameters;
import org.xwiki.contrib.jira.charts.internal.display.ChartJSDataSource;
import org.xwiki.contrib.jira.charts.internal.source.AbstractJIRADataSource;
import org.xwiki.rendering.macro.AbstractMacro;
import org.xwiki.rendering.macro.MacroExecutionException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Abstract common operations to retrieve data and convert them to JSON for all charts.
 *
 * @param <T> the type of parameters
 * @param <U> the type of expected data source
 *
 * @version $Id$
 * @since 10.0
 */
public abstract class AbstractJIRAChartMacro<T extends AbstractChartMacroParameters, U extends AbstractJIRADataSource>
    extends AbstractMacro<T>
{
    /**
     * Default constructor.
     * @param name the name of the macro
     * @param description the description of the macro
     * @param parametersClass the type of parameters
     */
    public AbstractJIRAChartMacro(String name, String description, Class<T> parametersClass)
    {
        super(name, description, null, parametersClass);
    }

    @Override
    public boolean supportsInlineMode()
    {
        return false;
    }

    /**
     * @return the instance of data {@link JIRAChartDataFetcher}.
     */
    protected abstract JIRAChartDataFetcher<T, U> getDataFetcher();

    /**
     * @return the instance of {@link JIRADataChartJSDataConverter}.
     */
    protected abstract JIRADataChartJSDataConverter<U, T> getConverter();

    /**
     * Fetch the data, convert them, and serialize them to JSON to be used in ChartJS macro.
     * @param parameters the parameters of the macro.
     * @param dataSourceClass the actual class for the data source.
     * @return a serialized JSON string to be used in ChartJS.
     * @throws MacroExecutionException in case of problem during serialization of the JSON.
     */
    protected String getJSONData(T parameters, Class<U> dataSourceClass) throws MacroExecutionException
    {
        U dataSource = getDataFetcher().fetch(parameters, dataSourceClass);

        ChartJSDataSource convert = getConverter().convert(dataSource, parameters);
        try {
            return new ObjectMapper().writeValueAsString(convert);
        } catch (JsonProcessingException e) {
            throw new MacroExecutionException("Error when transforming data to JSON", e);
        }
    }
}
