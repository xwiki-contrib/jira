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
package org.xwiki.contrib.jira.macro.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.xwiki.contrib.jira.macro.JIRAConfiguration;

/**
 * Default in-memory implementation for the JIRA Configuration.
 *
 * @version $Id$
 * @since 8.2
 */
public class DefaultJIRAConfiguration implements JIRAConfiguration
{
    private Map<String, String> urlMappings = Collections.EMPTY_MAP;

    private String defaultURLId;

    private String username;

    private String password;

    @Override
    public Map<String, String> getURLMappings()
    {
        return this.urlMappings;
    }

    /**
     * @param urlMappings see {@link #getURLMappings()}
     */
    public void setURLMappings(Map<String, String> urlMappings)
    {
        this.urlMappings = new HashMap<>(urlMappings);
    }

    @Override
    public String getDefaultURLId()
    {
        return this.defaultURLId;
    }

    /**
     * @param defaultURLId see {@link #getDefaultURLId()}
     */
    public void setDefaultURLId(String defaultURLId)
    {
        this.defaultURLId = defaultURLId;
    }

    @Override
    public String getUsername()
    {
        return this.username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    @Override
    public String getPassword()
    {
        return this.password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
