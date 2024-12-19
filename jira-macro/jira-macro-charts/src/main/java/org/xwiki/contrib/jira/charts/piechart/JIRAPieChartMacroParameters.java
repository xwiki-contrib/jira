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
package org.xwiki.contrib.jira.charts.piechart;

import org.xwiki.contrib.jira.charts.AbstractChartMacroParameters;
import org.xwiki.properties.annotation.PropertyAdvanced;
import org.xwiki.properties.annotation.PropertyDescription;
import org.xwiki.properties.annotation.PropertyMandatory;
import org.xwiki.stability.Unstable;

/**
 * Define the parameters for the JIRA Pie Chart macro.
 *
 * @version $Id$
 * @since 10.0
 */
@Unstable
public class JIRAPieChartMacroParameters extends AbstractChartMacroParameters
{
    private StatisticType type;
    private int maxData;

    /**
     * Default constructor.
     */
    public JIRAPieChartMacroParameters()
    {
        this.maxData = 10;
    }

    /**
     * @return the type of statistics to display in the pie chart.
     */
    public StatisticType getType()
    {
        return type;
    }

    /**
     * @param type see {@link #getType()}.
     */
    @PropertyDescription("The type of statistics to display in the pie chart.")
    @PropertyMandatory
    public void setType(StatisticType type)
    {
        this.type = type;
    }

    /**
     * @return the maximum number of data to display in the pie chart.
     */
    public int getMaxData()
    {
        return maxData;
    }

    /**
     * @param maxData see {@link #getMaxData()}.
     */
    @PropertyDescription("The maximum number of data to display in the pie chart: if there's more data they will be "
        + "displayed aggregated.")
    @PropertyAdvanced
    public void setMaxData(int maxData)
    {
        this.maxData = maxData;
    }
}
