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

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.component.util.DefaultParameterizedType;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.JIRAMacroTransformation;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.transformation.MacroTransformationContext;

/**
 * Tools for handling macro transformation after macro execution.
 *
 * @version $Id$
 * @since 11.0.0
 */
@Component(roles = { JIRAMacroTransformationManager.class })
@Singleton
public class JIRAMacroTransformationManager
{
    @Inject
    @Named("context")
    private Provider<ComponentManager> componentManagerProvider;

    @Inject
    private Logger logger;

    /**
     * Apply a transformation on a {@link List<Block>}.
     *
     * @param blocks the input list of block to transform.
     * @param parameters the macro parameters.
     * @param context the macro context.
     * @param jiraServer the corresponding JIRA server.
     * @param macroName the name of the called macro.
     * @param <P> the type of the macro parameter.
     * @return the transformed list of blocks.
     */
    public <P> List<Block> transform(List<Block> blocks, P parameters, MacroTransformationContext context,
        JIRAServer jiraServer, String macroName)
    {
        List<Block> resultingBlocks = blocks;
        // Add additional transformation from others components
        try {
            Type t = new DefaultParameterizedType(null, JIRAMacroTransformation.class, parameters.getClass());
            List<JIRAMacroTransformation<P>> jiraMacroTransformations =
                componentManagerProvider.get().getInstanceList(t);
            for (JIRAMacroTransformation<P> transformation : jiraMacroTransformations) {
                resultingBlocks = transformation.transform(resultingBlocks, parameters, context, jiraServer, macroName);
            }
        } catch (ComponentLookupException e) {
            // Avoid to crash the full macro execution if the transformation fail and just show the macro without
            // the transformation.
            logger.error("Can't get components of Role JIRAMacroTransformation", e);
        }
        return resultingBlocks;
    }
}
