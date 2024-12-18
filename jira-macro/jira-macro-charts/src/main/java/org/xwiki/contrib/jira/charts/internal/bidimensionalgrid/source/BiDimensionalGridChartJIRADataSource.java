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

public class BiDimensionalGridChartJIRADataSource extends AbstractJIRADataSource
{
    private String xHeading;
    private String yHeading;
    private long totalRows;
    private BiDimensionalGridChartJIRARow firstRow;
    private List<BiDimensionalGridChartJIRARow> rows;

    public String getxHeading()
    {
        return xHeading;
    }

    public void setxHeading(String xHeading)
    {
        this.xHeading = xHeading;
    }

    public String getyHeading()
    {
        return yHeading;
    }

    public void setyHeading(String yHeading)
    {
        this.yHeading = yHeading;
    }

    public long getTotalRows()
    {
        return totalRows;
    }

    public void setTotalRows(long totalRows)
    {
        this.totalRows = totalRows;
    }

    public BiDimensionalGridChartJIRARow getFirstRow()
    {
        return firstRow;
    }

    public void setFirstRow(BiDimensionalGridChartJIRARow firstRow)
    {
        this.firstRow = firstRow;
    }

    public List<BiDimensionalGridChartJIRARow> getRows()
    {
        return rows;
    }

    public void setRows(
        List<BiDimensionalGridChartJIRARow> rows)
    {
        this.rows = rows;
    }
}
