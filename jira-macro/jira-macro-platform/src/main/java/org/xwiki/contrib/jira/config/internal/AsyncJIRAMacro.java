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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.contrib.jira.config.JIRAConfiguration;
import org.xwiki.contrib.jira.macro.JIRAMacroParameters;
import org.xwiki.contrib.jira.macro.internal.JIRAMacro;
import org.xwiki.rendering.async.internal.AsyncRendererConfiguration;
import org.xwiki.rendering.async.internal.block.BlockAsyncRendererExecutor;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.CompositeBlock;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.rendering.transformation.MacroTransformationContext;

import com.xpn.xwiki.internal.context.XWikiContextContextStore;

/**
 * Overrides the {@link JIRAMacro} to make it work asynchronously. This is done so that the JIRA macro can be used in
 * XWiki Rendering (the non-async version) or in XWiki Platform (the async version).
 *
 * @version $Id$
 * @since 8.6
 */
@Component
@Named("jira")
@Singleton
public class AsyncJIRAMacro extends JIRAMacro
{
    @Inject
    @Named("context")
    private ComponentManager contextComponentManager;

    @Inject
    private BlockAsyncRendererExecutor executor;

    @Inject
    private JIRAConfiguration jiraConfiguration;

    @Override
    public List<Block> execute(JIRAMacroParameters parameters, String content, MacroTransformationContext context)
        throws MacroExecutionException
    {
        JIRABlockAsyncRenderer renderer;
        try {
            renderer = this.contextComponentManager.getInstance(JIRABlockAsyncRenderer.class);
        } catch (ComponentLookupException e) {
            throw new MacroExecutionException("Failed to create JIRA async renderer", e);
        }
        renderer.initialize(this, parameters, content, this.jiraConfiguration.isAsync(), context);

        AsyncRendererConfiguration configuration = new AsyncRendererConfiguration();

        // Take into account the current wiki since the jira config document can be located in each wiki (without this
        // all config document will be taken from the main wiki).
        // we need the PROP_USER and PROP_REQUEST_URL to provide a more correct context when calling
        // the JIRAMacroTransformation implementation. This is mainly needed for the transformation related to
        // the OAuth Authenticator.
        configuration.setContextEntries(Set.of(
            XWikiContextContextStore.PROP_WIKI,
            XWikiContextContextStore.PROP_USER,
            XWikiContextContextStore.PROP_REQUEST_URL
        ));

        // Execute the renderer
        Block result;
        try {
            result = this.executor.execute(renderer, configuration);
        } catch (Exception e) {
            throw new MacroExecutionException("Failed to execute JIRA macro", e);
        }

        return result instanceof CompositeBlock ? result.getChildren() : Arrays.asList(result);
    }

    List<Block> executeCodeMacro(JIRAMacroParameters parameters, String content,
        MacroTransformationContext context) throws MacroExecutionException
    {
        return super.execute(parameters, content, context);
    }
}
