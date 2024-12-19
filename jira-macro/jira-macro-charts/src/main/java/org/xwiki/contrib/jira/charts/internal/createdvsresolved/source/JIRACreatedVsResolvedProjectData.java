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
public class JIRACreatedVsResolvedProjectData
{
    private String id;
    private String name;
    private String key;

    /**
     * @return the internal identifier of the project.
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
     * @return the name of the project.
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
     * @return the technical key of the project.
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
}
