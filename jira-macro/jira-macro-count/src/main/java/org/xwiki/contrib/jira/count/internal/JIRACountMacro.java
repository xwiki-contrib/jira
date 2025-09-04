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
package org.xwiki.contrib.jira.count.internal;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.count.JIRACountMacroParameters;
import org.xwiki.contrib.jira.macro.internal.HTTPJIRAFetcher;
import org.xwiki.contrib.jira.macro.JIRABadRequestException;
import org.xwiki.contrib.jira.macro.JIRAConnectionException;
import org.xwiki.contrib.jira.macro.internal.JIRAErrorGenerator;
import org.xwiki.contrib.jira.macro.internal.JIRAMacroTransformationManager;
import org.xwiki.contrib.jira.macro.internal.JIRAURLHelper;
import org.xwiki.contrib.jira.macro.internal.source.JIRAServerResolver;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.ParagraphBlock;
import org.xwiki.rendering.block.WordBlock;
import org.xwiki.rendering.macro.AbstractMacro;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.rendering.macro.descriptor.DefaultContentDescriptor;
import org.xwiki.rendering.transformation.MacroTransformationContext;

/**
 * Get the number of results of a JQL from a JIRA server and display it.
 *
 * @version $Id$
 * @since 10.2.0
 */
@Component
@Named("jiraCount")
@Singleton
public class JIRACountMacro extends AbstractMacro<JIRACountMacroParameters>
{
    private static final String FAILED_TO_RETRIEVE_JIRA_DATA =
        "Failed to retrieve JIRA data from [%s] for JQL [%s]";

    /**
     * The description of the macro.
     */
    private static final String DESCRIPTION =
        "Get the number of result of a JQL from a JIRA server and display it.";

    /**
     * The description of the macro content.
     */
    private static final String CONTENT_DESCRIPTION = "The JQL query";

    @Inject
    private JIRAServerResolver jiraServerResolver;

    @Inject
    private HTTPJIRAFetcher jiraFetcher;

    @Inject
    private JIRAURLHelper urlHelper;

    @Inject
    private JIRAMacroTransformationManager jiraMacroTransformationManager;

    @Inject
    private JIRAErrorGenerator jiraErrorGenerator;

    /**
     * Create and initialize the descriptor of the macro.
     */
    public JIRACountMacro()
    {
        super("JIRACount", DESCRIPTION, new DefaultContentDescriptor(CONTENT_DESCRIPTION),
            JIRACountMacroParameters.class);
        setDefaultCategories(Collections.singleton(DEFAULT_CATEGORY_CONTENT));
    }

    @Override
    public boolean supportsInlineMode()
    {
        return true;
    }

    @Override
    public List<Block> execute(JIRACountMacroParameters parameters, String content, MacroTransformationContext context)
        throws MacroExecutionException
    {
        JIRAServer jiraServer = jiraServerResolver.resolve(parameters);

        if (StringUtils.isBlank(content)) {
            throw new MacroExecutionException("Missing JQL query!");
        }
        JIRASearchResult searchResult = null;
        List<Block> result = List.of();
        try {
            String urlString = this.urlHelper.getRestSearchURL(jiraServer, content);
            searchResult = this.jiraFetcher.fetchJSON(urlString, jiraServer, JIRASearchResult.class);
        } catch (JIRABadRequestException e) {
            // We avoid to raise an exception here to give the possibility to the JIRA macro transformation to be
            // executed even if we have this error.
            result = jiraErrorGenerator.getBadRequestErrorBlock(e, context.isInline());
        } catch (JIRAConnectionException e) {
            throw new MacroExecutionException(String.format(FAILED_TO_RETRIEVE_JIRA_DATA,
                jiraServer.getURL(), content), e);
        }
        if (searchResult != null) {
            result = List.of(new WordBlock(Integer.toString(searchResult.getTotal())));
        }
        if (!context.isInline()) {
            result = Collections.singletonList(new ParagraphBlock(result));
        }
        return jiraMacroTransformationManager.transform(result, parameters, context, jiraServer, "jiraCount");
    }
}
