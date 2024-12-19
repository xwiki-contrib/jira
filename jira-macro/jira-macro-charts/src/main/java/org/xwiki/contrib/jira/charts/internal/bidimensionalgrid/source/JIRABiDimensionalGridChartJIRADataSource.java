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
package org.xwiki.contrib.jira.charts.internal.bidimensionalgrid.source;

import java.util.List;

import org.xwiki.contrib.jira.charts.internal.source.AbstractJIRADataSource;

/**
 * POJO describing JIRA answer when calling their /gadget/ REST API for bidimensional table charts.
 * This aims at being automatically deserialized by Jackson.
 *
 * @version $Id$
 * @since 10.0
 */
public class JIRABiDimensionalGridChartJIRADataSource extends AbstractJIRADataSource
{
    private String xHeading;
    private String yHeading;
    private long totalRows;
    private JIRABiDimensionalGridChartJIRARow firstRow;
    private List<JIRABiDimensionalGridChartJIRARow> rows;

    /**
     * @return the heading name for x-axis.
     */
    public String getxHeading()
    {
        return xHeading;
    }

    /**
     * @param xHeading see {@link #getxHeading()}.
     */
    public void setxHeading(String xHeading)
    {
        this.xHeading = xHeading;
    }

    /**
     * @return the heading name for y-axis.
     */
    public String getyHeading()
    {
        return yHeading;
    }

    /**
     * @param yHeading see {@link #getyHeading()}.
     */
    public void setyHeading(String yHeading)
    {
        this.yHeading = yHeading;
    }

    /**
     * @return the total number of rows.
     */
    public long getTotalRows()
    {
        return totalRows;
    }

    /**
     * @param totalRows see {@link #getTotalRows()}.
     */
    public void setTotalRows(long totalRows)
    {
        this.totalRows = totalRows;
    }

    /**
     * @return all values for first row containing headings for each column.
     */
    public JIRABiDimensionalGridChartJIRARow getFirstRow()
    {
        return firstRow;
    }

    /**
     * @param firstRow see {@link #getFirstRow()}.
     */
    public void setFirstRow(JIRABiDimensionalGridChartJIRARow firstRow)
    {
        this.firstRow = firstRow;
    }

    /**
     * @return data for all rows.
     */
    public List<JIRABiDimensionalGridChartJIRARow> getRows()
    {
        return rows;
    }

    /**
     * @param rows see {@link #getRows()}.
     */
    public void setRows(
        List<JIRABiDimensionalGridChartJIRARow> rows)
    {
        this.rows = rows;
    }
}
