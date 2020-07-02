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
package org.xwiki.contrib.jira.config.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.xwiki.contrib.jira.config.JIRAConfiguration;
import org.xwiki.contrib.jira.config.JIRAServer;

/**
 * Default in-memory implementation for the JIRA Configuration.
 *
 * @version $Id$
 * @since 8.2
 */
public class DefaultJIRAConfiguration implements JIRAConfiguration
{
    private Map<String, JIRAServer> jiraServers = Collections.EMPTY_MAP;

    private boolean isAsync;

    @Override
    public Map<String, JIRAServer> getJIRAServers()
    {
        return this.jiraServers;
    }

    /**
     * @param jiraServers see {@link #getJIRAServers()}
     */
    public void setJIRAServers(Map<String, JIRAServer> jiraServers)
    {
        this.jiraServers = new HashMap<>(jiraServers);
    }

    @Override
    public boolean isAsync()
    {
        return this.isAsync;
    }

    /**
      * @param async see {@link #isAsync()}
     */
    public void setAsync(boolean async)
    {
        this.isAsync = async;
    }
}
