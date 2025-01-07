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
package org.xwiki.contrib.jira.charts.internal.display;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.xwiki.text.XWikiToStringBuilder;

/**
 * POJO defining the format of data requested by ChartJS.
 * This class aims at being automatically serialized by Jackson.
 *
 * @version $Id$
 * @since 10.0
 */
public class ChartJSDataSource
{
    private List<String> labels;
    private List<ChartJSDataSetSingleData> datasets;

    /**
     * @return the list of labels corresponding to each data.
     */
    public List<String> getLabels()
    {
        return labels;
    }

    /**
     * @param labels see {@link #getLabels()}.
     */
    public void setLabels(List<String> labels)
    {
        this.labels = labels;
    }

    /**
     * @return the list of datasets to display.
     */
    public List<ChartJSDataSetSingleData> getDatasets()
    {
        return datasets;
    }

    /**
     * @param datasets see {@link #getDatasets()}.
     */
    public void setDatasets(List<ChartJSDataSetSingleData> datasets)
    {
        this.datasets = datasets;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChartJSDataSource that = (ChartJSDataSource) o;

        return new EqualsBuilder()
            .append(labels, that.labels)
            .append(datasets, that.datasets)
            .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(13, 37).append(labels).append(datasets).toHashCode();
    }

    @Override
    public String toString()
    {
        return new XWikiToStringBuilder(this)
            .append("labels", labels)
            .append("datasets", datasets)
            .toString();
    }
}
