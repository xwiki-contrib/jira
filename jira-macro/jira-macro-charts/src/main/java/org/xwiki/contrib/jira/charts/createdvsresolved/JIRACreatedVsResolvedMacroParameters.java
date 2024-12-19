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
import org.xwiki.properties.annotation.PropertyDescription;
import org.xwiki.properties.annotation.PropertyMandatory;

/**
 * Define the parameters for the JIRA Created Vs Resolved macro.
 *
 * @version $Id$
 * @since 10.0
 */
public class JIRACreatedVsResolvedMacroParameters extends AbstractChartMacroParameters
{
    private ChartPeriod period;
    private int daysPreviously;
    private boolean isCount;
    private boolean displayTrend;
    private DisplayVersion displayVersion;

    /**
     * Default constructor.
     */
    public JIRACreatedVsResolvedMacroParameters()
    {
        this.isCount = true;
        this.displayVersion = DisplayVersion.NONE;
    }

    /**
     * @return the period of time that should be used for each data in the graph.
     */
    public ChartPeriod getPeriod()
    {
        return period;
    }

    /**
     * @param period see {@link #getPeriod()}
     */
    @PropertyMandatory
    @PropertyDescription("The period of time that should be used for each data in the graph.")
    public void setPeriod(ChartPeriod period)
    {
        this.period = period;
    }

    /**
     * @return the number of days in the past that should be used for gathering data.
     */
    public int getDaysPreviously()
    {
        return daysPreviously;
    }

    /**
     * @param daysPreviously see {@link #getDaysPreviously()}.
     */
    @PropertyMandatory
    @PropertyDescription("The number of days in the past that should be used for gathering data.")
    public void setDaysPreviously(int daysPreviously)
    {
        this.daysPreviously = daysPreviously;
    }

    /**
     * @return {@code true} if the data should be counted once, {@code false} if they should be accumulated.
     */
    public boolean isCount()
    {
        return isCount;
    }

    /**
     * @param count see {@link #isCount()}.
     */
    @PropertyDescription("Check if the data should be counted once, uncheck to accumulate the values.")
    public void setCount(boolean count)
    {
        isCount = count;
    }

    /**
     * @return {@code true} if a graph should be produced with the trend.
     */
    public boolean isDisplayTrend()
    {
        return displayTrend;
    }

    /**
     * @param displayTrend see {@link #isDisplayTrend()}.
     */
    @PropertyDescription("Check if a graph should be produced with the trend.")
    public void setDisplayTrend(boolean displayTrend)
    {
        this.displayTrend = displayTrend;
    }

    /**
     * @return the type of versions that should be displayed in the graph.
     */
    public DisplayVersion getDisplayVersion()
    {
        return displayVersion;
    }

    /**
     * @param displayVersion see {@link #getDisplayVersion()}.
     */
    @PropertyDescription("The type of versions that should be displayed in the graph.")
    public void setDisplayVersion(DisplayVersion displayVersion)
    {
        this.displayVersion = displayVersion;
    }
}
