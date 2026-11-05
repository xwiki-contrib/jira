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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.JIRAConnector;
import org.xwiki.rendering.macro.MacroExecutionException;

/**
 * Find the {@link JIRAConnector} to use to get data from a JIRA server, based on its instance type.
 *
 * @version $Id$
 * @since 11.2.0
 */
@Component(roles = JIRAConnectorManager.class)
@Singleton
public class JIRAConnectorManager
{
    /**
     * Use the context component manager since the connectors can be provided by extensions installed on a wiki.
     */
    @Inject
    @Named("context")
    private Provider<ComponentManager> componentManagerProvider;

    /**
     * @param server the JIRA server to get data from
     * @return the connector handling the instance type of the passed server
     * @throws MacroExecutionException if there is no connector for the instance type of the passed server. We don't
     *     fall back on the default connector since the requests it sends are not understood by other kinds of JIRA
     *     instances.
     */
    public JIRAConnector getConnector(JIRAServer server) throws MacroExecutionException
    {
        String instanceType = server.getInstanceType();
        ComponentManager componentManager = this.componentManagerProvider.get();
        if (!componentManager.hasComponent(JIRAConnector.class, instanceType)) {
            throw new MacroExecutionException(String.format("The JIRA instance type [%s] of the JIRA server [%s] is "
                + "not supported. The extension providing it may be missing or not installed on this wiki.",
                instanceType, server.getURL()));
        }
        try {
            return componentManager.getInstance(JIRAConnector.class, instanceType);
        } catch (ComponentLookupException e) {
            throw new MacroExecutionException(String.format(
                "Failed to initialize the support of the JIRA instance type [%s] of the JIRA server [%s].",
                instanceType, server.getURL()), e);
        }
    }
}
