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
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.contrib.jira.config.JIRAConfiguration;
import org.xwiki.contrib.jira.config.JIRAServer;

/**
 * Platform implementation of the JIRA configuration.
 *
 * @version $Id$
 */
@Component
@Singleton
public class PlatformJIRAConfiguration implements JIRAConfiguration
{
    @Inject
    @Named("jira")
    private ConfigurationSource jiraConfigurationSource;

    @Inject
    @Named("xwikiproperties")
    private ConfigurationSource xwikiPropertiesConfigurationSource;

    @Override
    public Map<String, JIRAServer> getJIRAServers()
    {
        Map<String, JIRAServer> jiraServers = this.jiraConfigurationSource.getProperty("serverMappings");
        // The returned value can be null if no xobject has been defined on the wiki config page.
        if (jiraServers == null) {
            jiraServers = Collections.emptyMap();
        }
        return jiraServers;
    }

    @Override
    public boolean isAsync()
    {
        return this.xwikiPropertiesConfigurationSource.getProperty("jira.async", Boolean.TRUE);
    }
}
