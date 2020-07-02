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
import org.xwiki.contrib.jira.macro.JIRAMacroParameters;
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

@Component(roles = JIRABlockAsyncRenderer.class)
public class JIRABlockAsyncRenderer extends AbstractBlockAsyncRenderer
{
    @Inject
    private DocumentReferenceResolver<String> resolver;

    @Inject
    private AsyncContext asyncContext;

    @Inject
    private ErrorBlockGenerator errorBlockGenerator;

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

        // Find index of the macro in the XDOM
        long index = context.getXDOM().indexOf(context.getCurrentMacroBlock());
        this.id = createId("rendering", "macro", "jira", source, index);
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
            resultBlocks = this.errorBlockGenerator.generateErrorBlocks("Failed to execute the JIRA macro", e,
                this.inline);
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
