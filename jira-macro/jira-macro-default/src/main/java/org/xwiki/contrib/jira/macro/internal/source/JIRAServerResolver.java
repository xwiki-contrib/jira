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

import org.xwiki.component.annotation.Role;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.JIRAMacroParameters;
import org.xwiki.rendering.macro.MacroExecutionException;

/**
 * Resolve the {@link org.xwiki.contrib.jira.config.JIRAServer} from the Macro parameters and configuration.
 *
 * @version $Id$
 * @since 8.6.3
 */
@Role
public interface JIRAServerResolver
{
    /**
     * @param parameters the parameters of the called jira macro
     * @return the resolved {@link JIRAServer} taken from the macro parameters if defined or from the configuration
     * @throws MacroExecutionException if it cannot be resolved (not defined in either places)
     */
    JIRAServer resolve(JIRAMacroParameters parameters) throws MacroExecutionException;
}
