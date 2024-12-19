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

import java.util.Date;

/**
 * POJO describing JIRA answer when calling their /gadget/ REST API for created vs resolved charts.
 * This aims at being automatically deserialized by Jackson.
 *
 * @version $Id$
 * @since 10.0
 */
public class JIRACreatedVsResolvedTimeData
{
    private Date start;
    private Date end;
    private JIRACreatedVsResolvedSingleData data;

    /**
     * @return the starting date of the period used for gathering data.
     */
    public Date getStart()
    {
        return start;
    }

    /**
     * @param start see {@link #getStart()}.
     */
    public void setStart(Date start)
    {
        this.start = start;
    }

    /**
     * @return the ending date of the period used for gathering data.
     */
    public Date getEnd()
    {
        return end;
    }

    /**
     * @param end see {@link #getEnd()}.
     */
    public void setEnd(Date end)
    {
        this.end = end;
    }

    /**
     * @return the actual data for the given period.
     */
    public JIRACreatedVsResolvedSingleData getData()
    {
        return data;
    }

    /**
     * @param data see {@link #getData()}.
     */
    public void setData(JIRACreatedVsResolvedSingleData data)
    {
        this.data = data;
    }
}
