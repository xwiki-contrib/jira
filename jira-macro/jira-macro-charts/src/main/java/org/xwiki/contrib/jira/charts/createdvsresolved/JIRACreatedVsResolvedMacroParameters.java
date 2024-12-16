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
package org.xwiki.contrib.jira.charts.createdvsresolved;

import org.xwiki.contrib.jira.charts.AbstractChartMacroParameters;

public class JIRACreatedVsResolvedMacroParameters extends AbstractChartMacroParameters
{
    private ChartPeriod period;
    private int daysPreviously;
    private boolean isCount;
    private boolean displayTrend;
    private DisplayVersion displayVersion;

    public ChartPeriod getPeriod()
    {
        return period;
    }

    public void setPeriod(ChartPeriod period)
    {
        this.period = period;
    }

    public int getDaysPreviously()
    {
        return daysPreviously;
    }

    public void setDaysPreviously(int daysPreviously)
    {
        this.daysPreviously = daysPreviously;
    }

    public boolean isCount()
    {
        return isCount;
    }

    public void setCount(boolean count)
    {
        isCount = count;
    }

    public boolean isDisplayTrend()
    {
        return displayTrend;
    }

    public void setDisplayTrend(boolean displayTrend)
    {
        this.displayTrend = displayTrend;
    }

    public DisplayVersion getDisplayVersion()
    {
        return displayVersion;
    }

    public void setDisplayVersion(DisplayVersion displayVersion)
    {
        this.displayVersion = displayVersion;
    }
}
