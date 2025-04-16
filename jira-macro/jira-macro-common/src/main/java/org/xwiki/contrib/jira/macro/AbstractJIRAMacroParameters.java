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
package org.xwiki.contrib.jira.macro;

import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.properties.annotation.PropertyDescription;
import org.xwiki.properties.annotation.PropertyDisplayType;
import org.xwiki.properties.annotation.PropertyGroup;
import org.xwiki.stability.Unstable;

/**
 * Common parameters for all JIRA macros.
 *
 * @version $Id$
 * @since 10.0
 */
@Unstable
public abstract class AbstractJIRAMacroParameters
{
    /**
     * @see #getURL()
     */
    private String url;

    /**
     * @see #getId()
     */
    private String id;

    /**
     * @param url see {@link #getURL()}
     */
    @PropertyDescription("the JIRA Server URL")
    @PropertyGroup("instance")
    public void setURL(String url)
    {
        this.url = url;
    }

    /**
     * @return the JIRA Server URL (e.g. {@code http://jira.xwiki.org})
     */
    public String getURL()
    {
        return this.url;
    }

    /**
     * @param id see {@link #getId()}
     */
    @PropertyDescription("the configuration id of the JIRA Server URL to use")
    @PropertyGroup("instance")
    @PropertyDisplayType(JIRAServer.class)
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * @return the configuration id of the JIRA Server URL to use (defined in the Macro configuration settings). Note
     *         that if a URL is specified it'll take precedence over this parameter.
     */
    public String getId()
    {
        return this.id;
    }
}
