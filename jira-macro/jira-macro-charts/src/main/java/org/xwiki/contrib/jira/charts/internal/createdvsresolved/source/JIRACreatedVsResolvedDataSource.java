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
package org.xwiki.contrib.jira.charts.internal.createdvsresolved.source;

import java.util.List;

import org.xwiki.contrib.jira.charts.internal.AbstractJIRADataSource;

public class JIRACreatedVsResolvedDataSource extends AbstractJIRADataSource
{
    private JIRACreatedVsResolvedTotalsData totals;
    private List<JIRACreatedVsResolvedTimeData> results;
    private List<JIRACreatedVsResolvedVersionData> versions;

    public JIRACreatedVsResolvedTotalsData getTotals()
    {
        return totals;
    }

    public void setTotals(JIRACreatedVsResolvedTotalsData totals)
    {
        this.totals = totals;
    }

    public List<JIRACreatedVsResolvedTimeData> getResults()
    {
        return results;
    }

    public void setResults(
        List<JIRACreatedVsResolvedTimeData> results)
    {
        this.results = results;
    }

    public List<JIRACreatedVsResolvedVersionData> getVersions()
    {
        return versions;
    }

    public void setVersions(
        List<JIRACreatedVsResolvedVersionData> versions)
    {
        this.versions = versions;
    }
}
