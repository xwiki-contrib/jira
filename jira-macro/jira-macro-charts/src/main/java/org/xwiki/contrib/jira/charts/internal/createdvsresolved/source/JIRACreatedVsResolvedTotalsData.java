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

/**
 * POJO describing JIRA answer when calling their /gadget/ REST API for created vs resolved charts.
 * This aims at being automatically deserialized by Jackson.
 *
 * @version $Id$
 * @since 10.0
 */
public class JIRACreatedVsResolvedTotalsData
{
    private long created;
    private long resolved;

    /**
     * @return total number of created tickets.
     */
    public long getCreated()
    {
        return created;
    }

    /**
     * @param created see {@link #getCreated()}.
     */
    public void setCreated(long created)
    {
        this.created = created;
    }

    /**
     * @return total number of resolved tickets.
     */
    public long getResolved()
    {
        return resolved;
    }

    /**
     * @param resolved see {@link #getResolved()}.
     */
    public void setResolved(long resolved)
    {
        this.resolved = resolved;
    }
}
