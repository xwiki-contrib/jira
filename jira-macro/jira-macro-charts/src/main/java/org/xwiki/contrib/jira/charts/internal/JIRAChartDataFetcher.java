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
import org.xwiki.contrib.jira.charts.internal.source.AbstractJIRADataSource;
import org.xwiki.contrib.jira.macro.JIRABadRequestException;
import org.xwiki.rendering.macro.MacroExecutionException;

/**
 * Allow to request JIRA for retrieving data for a specific macro.
 *
 * @param <T> the type of macro parameters to be used for retrieving data.
 * @param <U> the type of JIRA data source to obtain.
 *
 * @version $Id$
 * @since 10.0
 */
@Role
public interface JIRAChartDataFetcher<T extends AbstractChartMacroParameters, U extends AbstractJIRADataSource>
{
    /**
     * Fetch the data from JIRA REST gadget endpoint based on the given macro parameters and returns them parsed in
     * the given type format.
     * @param parameters the macro parameters used to perform the request.
     * @param expectedType the type of the data that should be returned.
     * @return an instance of the data parsed to the expected type.
     * @throws MacroExecutionException in case of problem to perform the query or to parse the data.
     */
    U fetch(T parameters, Class<U> expectedType) throws MacroExecutionException, JIRABadRequestException;
}
