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
package org.xwiki.contrib.jira.charts.bidimensionalgrid;

/**
 * Representation of possible fields to select for x and y axises fields in bidimensional table charts.
 *
 * @version $Id$
 * @since 10.0
 */
// TODO: this should probably be improved to not rely on an enum, and to allow giving custom fields...
// see also JIRAField defined in jira macro
public enum JIRAField
{
    /**
     * The assignee field in jira issues.
     */
    ASSIGNEE("assignees"),

    /**
     * The reporter field in jira issues.
     */
    REPORTER("reporter"),

    /**
     * The status field in jira issues.
     */
    STATUS("statuses"),

    /**
     * The fix version field in jira issues.
     */
    FIX_VERSION("allFixfor"),

    /**
     * The component field in jira issues.
     */
    COMPONENT("components"),

    /**
     * The priority field in jira issues.
     */
    PRIORITY("priorities"),

    /**
     * The type field in jira issues.
     */
    TYPE("issuetype");

    private final String queryName;

    /**
     * Default constructor.
     * @param queryName the actual name to be used in the endpoint.
     */
    JIRAField(String queryName)
    {
        this.queryName = queryName;
    }

    /**
     * @return the actual name to be used in the endpoint.
     */
    public String getQueryName()
    {
        return queryName;
    }
}
