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

import org.xwiki.contrib.jira.charts.AbstractChartMacroParameters;
import org.xwiki.properties.annotation.PropertyDescription;
import org.xwiki.properties.annotation.PropertyMandatory;

/**
 * Macro parameters for the bidimensional grid chart macro.
 *
 * @version $Id$
 * @since 9.1
 */
public class BiDimensionGridChartMacroParameter extends AbstractChartMacroParameters
{
    // FIXME: right now the JIRAField is limiting to a predefined set of field?
    private JIRAField xAxisField;
    private JIRAField yAxisField;
    private int numberOfResults;
    private boolean isSortAscending;
    private boolean isSortNatural;

    /**
     * Default constructor.
     */
    public BiDimensionGridChartMacroParameter()
    {
        this.numberOfResults = 5;
        this.isSortAscending = true;
        this.isSortNatural = true;
    }

    /**
     * @return represents the field information to be displayed in each column.
     */
    public JIRAField getxAxisField()
    {
        return xAxisField;
    }

    /**
     * @param xAxisField see {@link #getxAxisField()}.
     */
    @PropertyDescription("The field information to be displayed in each column.")
    @PropertyMandatory
    public void setxAxisField(JIRAField xAxisField)
    {
        this.xAxisField = xAxisField;
    }

    /**
     * @return represents the field information to be displayed in each row.
     */
    public JIRAField getyAxisField()
    {
        return yAxisField;
    }

    /**
     * @param yAxisField see {@link #getyAxisField()}.
     */
    @PropertyDescription("The field information to be displayed in each row.")
    @PropertyMandatory
    public void setyAxisField(JIRAField yAxisField)
    {
        this.yAxisField = yAxisField;
    }

    /**
     * @return the number of results to return.
     */
    public int getNumberOfResults()
    {
        return numberOfResults;
    }

    /**
     * @param numberOfResults see {@link #getNumberOfResults()}.
     */
    @PropertyDescription("The number of results to return.")
    public void setNumberOfResults(int numberOfResults)
    {
        this.numberOfResults = numberOfResults;
    }

    /**
     * @return {@code true} if the sorting should be ascending, {@code false} if it should be descending.
     */
    public boolean isSortAscending()
    {
        return isSortAscending;
    }

    /**
     * @param sortAscending see {@link #isSortAscending()}.
     */
    @PropertyDescription("Check if the sorting should be ascending, else it will be descending.")
    public void setSortAscending(boolean sortAscending)
    {
        isSortAscending = sortAscending;
    }

    /**
     * @return {@code true} to sort by natural field order, {@code false} to sort using the totals.
     */
    public boolean isSortNatural()
    {
        return isSortNatural;
    }

    /**
     * @param sortNatural see {@link #isSortNatural()}.
     */
    @PropertyDescription("Check to sort by natural field order, else it will sort using the totals.")
    public void setSortNatural(boolean sortNatural)
    {
        isSortNatural = sortNatural;
    }
}
