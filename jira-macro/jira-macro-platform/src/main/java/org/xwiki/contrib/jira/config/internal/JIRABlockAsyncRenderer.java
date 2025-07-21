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

import javax.inject.Inject;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.config.JIRAAuthenticator;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.JIRAMacroParameters;
import org.xwiki.contrib.jira.macro.internal.source.JIRAServerResolver;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.rendering.async.AsyncContext;
import org.xwiki.rendering.async.internal.block.AbstractBlockAsyncRenderer;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.CompositeBlock;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.block.MacroMarkerBlock;
import org.xwiki.rendering.block.MetaDataBlock;
import org.xwiki.rendering.block.match.MetadataBlockMatcher;
import org.xwiki.rendering.listener.MetaData;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.rendering.syntax.Syntax;
import org.xwiki.rendering.transformation.MacroTransformationContext;
import org.xwiki.rendering.util.ErrorBlockGenerator;

/**
 * Render the JIRA macro content asynchronously.
 *
 * @since 8.6
 * @version $Id$
 */
@Component(roles = JIRABlockAsyncRenderer.class)
public class JIRABlockAsyncRenderer extends AbstractBlockAsyncRenderer
{
    @Inject
    private DocumentReferenceResolver<String> resolver;

    @Inject
    private AsyncContext asyncContext;

    @Inject
    private ErrorBlockGenerator errorBlockGenerator;

    @Inject
    private JIRAServerResolver jiraServerResolver;

    private List<String> id;

    private boolean inline;

    private Syntax targetSyntax;

    private AsyncJIRAMacro macro;

    private JIRAMacroParameters parameters;

    private String content;

    private MacroTransformationContext context;

    private DocumentReference sourceReference;

    private boolean isAsync;

    void initialize(AsyncJIRAMacro macro, JIRAMacroParameters parameters, String content, boolean isAsync,
        MacroTransformationContext context)
    {
        this.macro = macro;
        this.parameters = parameters;
        this.content = content;
        this.isAsync = isAsync;
        this.context = context;

        this.inline = context.isInline();
        this.targetSyntax = context.getTransformationContext().getTargetSyntax();

        String source = getCurrentSource(context);
        if (source != null) {
            this.sourceReference = this.resolver.resolve(source);
        }

        this.id = createId(source, context);
    }

    @Override
    protected Block execute(boolean async, boolean cached)
    {
        List<Block> resultBlocks;

        if (this.sourceReference != null) {
            // Invalidate the cache when the document containing the macro call is modified
            this.asyncContext.useEntity(this.sourceReference);
        }
        try {
            resultBlocks = this.macro.executeCodeMacro(this.parameters, this.content, this.context);
        } catch (MacroExecutionException e) {
            // Display the error in the result
            resultBlocks = this.errorBlockGenerator.generateErrorBlocks(this.inline, null,
                "Failed to execute the JIRA macro", null, e);
        }

        resultBlocks = Arrays.asList(wrapInMacroMarker(this.context.getCurrentMacroBlock(), resultBlocks));

        return new CompositeBlock(resultBlocks);
    }

    @Override
    public boolean isInline()
    {
        return this.inline;
    }

    @Override
    public Syntax getTargetSyntax()
    {
        return this.targetSyntax;
    }

    @Override
    public List<String> getId()
    {
        return this.id;
    }

    @Override
    public boolean isAsyncAllowed()
    {
        return this.isAsync;
    }

    @Override
    public boolean isCacheAllowed()
    {
        return false;
    }

    private List<String> createId(String source, MacroTransformationContext context)
    {
        // Find index of the macro in the XDOM to create a unique id.
        long index = context.getXDOM().indexOf(context.getCurrentMacroBlock());

        // Note: make sure we don't cache if a different jira url is used or if a different user is used for the same
        // jira url since different users can have different permissions on jira.
        String jiraURL;
        String authenticatorPart = null;
        try {
            JIRAServer jiraServer = this.jiraServerResolver.resolve(this.parameters);
            jiraURL = jiraServer.getURL();
            authenticatorPart = jiraServer.getJiraAuthenticator().map(JIRAAuthenticator::getId).orElse(null);
        } catch (MacroExecutionException e) {
            // The jira url is not set nor an id set (or the id points to a not-defined JIRA URL), so we cannot get the
            // value. The macro will fail to display. We cache the result based on the id if it's defined. Otherwise,
            // we don't use the jira url.
            jiraURL = this.parameters.getId();
        }

        return createId("rendering", "macro", "jira", source, index, jiraURL, authenticatorPart);
    }

    private String getCurrentSource(MacroTransformationContext context)
    {
        String currentSource = null;

        if (context != null) {
            currentSource =
                context.getTransformationContext() != null ? context.getTransformationContext().getId() : null;

            MacroBlock currentMacroBlock = context.getCurrentMacroBlock();

            if (currentMacroBlock != null) {
                MetaDataBlock metaDataBlock =
                    currentMacroBlock.getFirstBlock(new MetadataBlockMatcher(MetaData.SOURCE),
                        Block.Axes.ANCESTOR_OR_SELF);

                if (metaDataBlock != null) {
                    currentSource = (String) metaDataBlock.getMetaData().getMetaData(MetaData.SOURCE);
                }
            }
        }

        return currentSource;
    }

    private Block wrapInMacroMarker(MacroBlock macroBlockToWrap, List<Block> newBlocks)
    {
        return new MacroMarkerBlock(macroBlockToWrap.getId(), macroBlockToWrap.getParameters(),
            macroBlockToWrap.getContent(), newBlocks, macroBlockToWrap.isInline());
    }
}
