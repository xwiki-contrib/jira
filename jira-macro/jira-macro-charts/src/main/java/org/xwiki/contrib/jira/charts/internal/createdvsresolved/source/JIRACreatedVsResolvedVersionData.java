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
public class JIRACreatedVsResolvedVersionData
{
    private String id;
    private String name;
    private String url;
    private boolean released;
    private boolean archived;
    private Date releaseDate;
    private JIRACreatedVsResolvedProjectData project;

    /**
     * @return internal identifier of the version.
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id see {@link #getId()}.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * @return name of the version.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name see {@link #getName()}.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the URL of the query to access all tickets for that version.
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

    /**
     * @return {@code true} if that version has been released.
     */
    public boolean isReleased()
    {
        return released;
    }

    /**
     * @param released see {@link #isReleased()}.
     */
    public void setReleased(boolean released)
    {
        this.released = released;
    }

    /**
     * @return {@code true} if that version has been archived.
     */
    public boolean isArchived()
    {
        return archived;
    }

    /**
     * @param archived see {@link #isArchived()}.
     */
    public void setArchived(boolean archived)
    {
        this.archived = archived;
    }

    /**
     * @return when that version was released or {@code null} if it has not been released.
     */
    public Date getReleaseDate()
    {
        return releaseDate;
    }

    /**
     * @param releaseDate see {@link #getReleaseDate()}.
     */
    public void setReleaseDate(Date releaseDate)
    {
        this.releaseDate = releaseDate;
    }

    /**
     * @return the information about the project.
     */
    public JIRACreatedVsResolvedProjectData getProject()
    {
        return project;
    }

    /***
     * @param project see {@link #getProject()}.
     */
    public void setProject(JIRACreatedVsResolvedProjectData project)
    {
        this.project = project;
    }
}
