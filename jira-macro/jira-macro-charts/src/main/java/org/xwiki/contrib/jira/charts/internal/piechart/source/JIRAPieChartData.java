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

/**
 * POJO describing JIRA answer when calling their /gadget/ REST API for piechart charts. This aims at being
 * automatically deserialized by Jackson.
 *
 * @version $Id$
 * @since 10.0
 */
public class JIRAPieChartData implements Comparable<JIRAPieChartData>
{
    private String key;

    private long value;

    private String url;

    /**
     * @return the actual label for that data, which depends on the requested data type.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * @param key see {@link #getKey()}.
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * @return the actual value of the data.
     */
    public long getValue()
    {
        return value;
    }

    /**
     * @param value see {@link #getValue()}.
     */
    public void setValue(long value)
    {
        this.value = value;
    }

    /**
     * @return the URL of the filter query to access the tickets represented by the data.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * @param url see {@link #getUrl()}.
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    @Override
    public int compareTo(JIRAPieChartData jiraPieChartData)
    {
        return Long.compare(this.value, jiraPieChartData.getValue());
    }
}
