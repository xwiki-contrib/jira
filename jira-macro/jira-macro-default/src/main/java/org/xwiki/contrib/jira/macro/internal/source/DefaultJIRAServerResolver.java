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
package org.xwiki.contrib.jira.macro.internal.source;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.config.JIRAConfiguration;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.AbstractJIRAMacroParameters;
import org.xwiki.rendering.macro.MacroExecutionException;

/**
 * Resolve the {@link org.xwiki.contrib.jira.config.JIRAServer} from the Macro parameters and configuration.
 *
 * @version $Id$
 * @since 8.6.3
 */
@Component
@Singleton
public class DefaultJIRAServerResolver implements JIRAServerResolver
{
    @Inject
    private JIRAConfiguration configuration;

    @Override
    public JIRAServer resolve(AbstractJIRAMacroParameters parameters) throws MacroExecutionException
    {
        JIRAServer jiraServer;

        // Check if the user has provided an explicit url in the macro. If so, try to find a matching URL in the
        // configured JIRA Server list in order to find if there are any credentials. If not found, return a public
        // JIRA instance.
        String url = parameters.getURL();
        if (StringUtils.isBlank(url)) {
            // If not, then check if the user has provided a server id in the macro
            String id = parameters.getId();
            if (StringUtils.isBlank(id)) {
                // Note: we could have decided that if there's a single JIRA server definition we would use that id by
                // default. However doing so would break all Macro calls not specifying an id as soon as a second jira
                // server definiton is added later on...
                throw new MacroExecutionException("No JIRA Server found. You must specify a JIRA server, using the "
                    + "\"url\" macro parameter or using the \"id\" macro parameter to reference a server defined in "
                    + "the JIRA Macro configuration.");
            }
            jiraServer = this.configuration.getJIRAServers().get(id);
            if (jiraServer == null) {
                throw new MacroExecutionException(String.format("The JIRA Server id [%s] is not defined in the macro's "
                    + "configuration. Please fix the id or add a new server in the JIRA Macro configuration.", id));
            }
        } else {
            jiraServer = null;
            for (JIRAServer server : this.configuration.getJIRAServers().values()) {
                if (server.getURL().equals(url)) {
                    jiraServer = server;
                    break;
                }
            }
            if (jiraServer == null) {
                jiraServer = new JIRAServer(url);
            }
        }

        return jiraServer;
    }
}
