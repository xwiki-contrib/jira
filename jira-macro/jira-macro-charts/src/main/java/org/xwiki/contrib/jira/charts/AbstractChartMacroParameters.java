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
package org.xwiki.contrib.jira.charts;

import org.xwiki.contrib.jira.macro.AbstractJIRAMacroParameters;
import org.xwiki.properties.annotation.PropertyDescription;
import org.xwiki.properties.annotation.PropertyGroup;

/**
 * Define the parameters common to all chart macros.
 *
 * @version $Id$
 * @since 9.1
 */
public abstract class AbstractChartMacroParameters extends AbstractJIRAMacroParameters
{
    private String query;
    private String filterId;

    /**
     * @return the JQL query to perform for getting issues.
     */
    public String getQuery()
    {
        return query;
    }

    /**
     * @param query see {@link #getQuery()}.
     */
    @PropertyDescription("the JQL query for getting issues.")
    @PropertyGroup("query")
    public void setQuery(String query)
    {
        this.query = query;
    }

    /**
     * @return the name of the saved JIRA filter to be used for the query.
     */
    public String getFilterId()
    {
        return filterId;
    }

    /**
     * @param filterId see {@link #getFilterId()}.
     */
    @PropertyDescription("the name of the saved JIRA filter to be used for the query.")
    @PropertyGroup("query")
    public void setFilterId(String filterId)
    {
        this.filterId = filterId;
    }
}
