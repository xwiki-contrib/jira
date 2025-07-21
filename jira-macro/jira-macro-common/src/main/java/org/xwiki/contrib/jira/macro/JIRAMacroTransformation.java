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

import java.util.List;

import org.xwiki.component.annotation.Role;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.transformation.MacroTransformationContext;

/**
 * Interface used to apply a transformation after the rendering of the jira, jiraCount an jiraChart macros.
 * @param <P> the type of Macro parameter.
 *
 * @version $Id$
 * @since 10.3.0
 */
@Role
public interface JIRAMacroTransformation<P>
{
    /**
     * Apply some transformation on the jira macro after the macro execution.
     *
     * @param blocks the macro block resulted of the macro execution.
     * @param parameters the macro parameters.
     * @param context the macro context.
     * @param jiraServer the resolved jiraServer instance at macro execution time.
     * @param macroName the name of the called macro.
     * @return the macro block resulted after the post macro execution.
     */
    default List<Block> transform(List<Block> blocks, P parameters,
        MacroTransformationContext context, JIRAServer jiraServer, String macroName)
    {
        return blocks;
    }
}
