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
package org.xwiki.contrib.jira.charts.internal.source;

/**
 * Common abstract class for all POJOs describing the answers coming from JIRA /gadget/ REST API.
 * Note that those POJOs aims at being automatically deserialized by Jackson and never instantiated manually.
 *
 * @version $Id$
 * @since 10.0
 */
public abstract class AbstractJIRADataSource
{
    private String filterTitle;
    private String filterUrl;

    /**
     * @return the title of the requested filter.
     */
    public String getFilterTitle()
    {
        return filterTitle;
    }

    /**
     * @param filterTitle see {@link #getFilterTitle()}.
     */
    public void setFilterTitle(String filterTitle)
    {
        this.filterTitle = filterTitle;
    }

    /**
     * @return the url to access the filter query.
     */
    public String getFilterUrl()
    {
        return filterUrl;
    }

    /**
     * @param filterUrl see {@link #getFilterUrl()}.
     */
    public void setFilterUrl(String filterUrl)
    {
        this.filterUrl = filterUrl;
    }
}
