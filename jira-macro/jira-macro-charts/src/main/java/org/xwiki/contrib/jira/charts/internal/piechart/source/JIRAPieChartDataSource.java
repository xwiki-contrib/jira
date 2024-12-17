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
package org.xwiki.contrib.jira.charts.internal.piechart.source;

import java.util.List;

import org.xwiki.contrib.jira.charts.internal.source.AbstractJIRADataSource;

/**
 * POJO describing JIRA answer when calling their /gadget/ REST API for piechart charts.
 * This aims at being automatically deserialized by Jackson.
 *
 * @version $Id$
 * @since 9.1
 */
public class JIRAPieChartDataSource extends AbstractJIRADataSource
{
    private String statType;
    private long issueCount;
    private List<JIRAPieChartData> results;

    /**
     * @return the type of data requested.
     */
    public String getStatType()
    {
        return statType;
    }

    /**
     * @param statType see {@link #getStatType()}.
     */
    public void setStatType(String statType)
    {
        this.statType = statType;
    }

    /**
     * @return the total number of tickets of the query.
     */
    public long getIssueCount()
    {
        return issueCount;
    }

    /**
     * @param issueCount see {@link #getIssueCount()}.
     */
    public void setIssueCount(long issueCount)
    {
        this.issueCount = issueCount;
    }

    /**
     * @return the actual data results.
     */
    public List<JIRAPieChartData> getResults()
    {
        return results;
    }

    /**
     * @param results see {@link #getResults()}.
     */
    public void setResults(List<JIRAPieChartData> results)
    {
        this.results = results;
    }
}
