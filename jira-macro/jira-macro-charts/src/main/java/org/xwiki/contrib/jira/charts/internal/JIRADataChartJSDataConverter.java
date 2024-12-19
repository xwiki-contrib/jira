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

import org.xwiki.component.annotation.Role;
import org.xwiki.contrib.jira.charts.AbstractChartMacroParameters;
import org.xwiki.contrib.jira.charts.internal.display.ChartJSDataSource;
import org.xwiki.contrib.jira.charts.internal.source.AbstractJIRADataSource;

/**
 * Allow to convert JIRA representation of data in the format supported by ChartJS.
 *
 * @param <T> the type of the JIRA data representation.
 * @param <U> the type of macro parameters.
 *
 * @version $Id$
 * @since 10.0
 */
@Role
public interface JIRADataChartJSDataConverter<T extends AbstractJIRADataSource, U extends AbstractChartMacroParameters>
{
    /**
     * Convert the given data using the provided macro parameters, to obtain a ChartJS representation.
     * @param dataSource the data to convert.
     * @param macroParameters the parameters that have been used to obtain the data.
     * @return a ChartJS representation of the data.
     */
    ChartJSDataSource convert(T dataSource, U macroParameters);
}
