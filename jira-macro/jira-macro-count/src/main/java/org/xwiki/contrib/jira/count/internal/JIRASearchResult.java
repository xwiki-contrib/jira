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
package org.xwiki.contrib.jira.count.internal;

import java.util.List;

/**
 * Class used for Jira REST search results.
 *
 * @version $Id$
 * @since 10.2.0
 */
class JIRASearchResult
{
    private int startAt;

    private int maxResults;

    private int total;

    private List<Object> issues;

    /**
     * @return the start at info.
     */
    public int getStartAt()
    {
        return startAt;
    }

    /**
     * @param startAt see {@link #getStartAt()}.
     */
    public void setStartAt(int startAt)
    {
        this.startAt = startAt;
    }

    /**
     * @return the max results parameter.
     */
    public int getMaxResults()
    {
        return maxResults;
    }

    /**
     * @param maxResults see {@link #getMaxResults()}.
     */
    public void setMaxResults(int maxResults)
    {
        this.maxResults = maxResults;
    }

    /**
     * @return the total of results.
     */
    public int getTotal()
    {
        return total;
    }

    /**
     * @param total see {@link #getTotal()}.
     */
    public void setTotal(int total)
    {
        this.total = total;
    }

    /**
     * @return the list of issues.
     */
    public List<Object> getIssues()
    {
        return issues;
    }

    /**
     * @param issues see {@link #getIssues()}.
     */
    public void setIssues(List<Object> issues)
    {
        this.issues = issues;
    }
}
