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
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.JIRAMacroParameters;
import org.xwiki.contrib.jira.macro.internal.source.JIRAServerResolver;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.block.XDOM;
import org.xwiki.rendering.syntax.Syntax;
import org.xwiki.rendering.transformation.MacroTransformationContext;
import org.xwiki.rendering.transformation.TransformationContext;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link JIRABlockAsyncRenderer}.
 *
 * @version $Id$
 */
@ComponentTest
class JIRABlockAsyncRendererTest
{
    private static final String SOURCE = "wiki:Space.Page";

    private static final String JIRA_URL = "http://localhost/jira";

    @InjectMockComponents
    private JIRABlockAsyncRenderer renderer;

    @MockComponent
    private JIRAServerResolver jiraServerResolver;

    private final JIRAMacroParameters parameters = new JIRAMacroParameters();

    private MacroTransformationContext context;

    private long macroIndex;

    @BeforeEach
    void setUp()
    {
        MacroBlock macroBlock = new MacroBlock("jira", Collections.emptyMap(), "ISSUE-1", false);
        XDOM xdom = new XDOM(List.of(macroBlock));
        TransformationContext transformationContext = new TransformationContext(xdom, Syntax.XWIKI_2_1);
        transformationContext.setId(SOURCE);
        transformationContext.setTargetSyntax(Syntax.XHTML_1_0);
        this.context = new MacroTransformationContext(transformationContext);
        this.context.setXDOM(xdom);
        this.context.setCurrentMacroBlock(macroBlock);
        this.macroIndex = xdom.indexOf(macroBlock);
    }

    @Test
    void idWithDefaultDeploymentType() throws Exception
    {
        when(this.jiraServerResolver.resolve(this.parameters)).thenReturn(new JIRAServer(JIRA_URL, "id"));

        this.renderer.initialize(mock(AsyncJIRAMacro.class), this.parameters, "ISSUE-1", true, this.context);

        // Same id as the one computed before the introduction of the deployment types.
        List<String> previousId = new JIRABlockAsyncRenderer()
        {
            List<String> getPreviousId()
            {
                return createId("rendering", "macro", "jira", SOURCE, macroIndex, JIRA_URL, null);
            }
        }.getPreviousId();
        assertEquals(previousId, this.renderer.getId());
    }

    @Test
    void idWithOtherDeploymentType() throws Exception
    {
        when(this.jiraServerResolver.resolve(this.parameters))
            .thenReturn(new JIRAServer(JIRA_URL, "id", new BasicAuthJIRAAuthenticator("user", "pass"), "cloud"));

        this.renderer.initialize(mock(AsyncJIRAMacro.class), this.parameters, "ISSUE-1", true, this.context);

        assertEquals(List.of("rendering", "macro", "jira", SOURCE, String.valueOf(this.macroIndex), JIRA_URL,
            new BasicAuthJIRAAuthenticator("user", "pass").getId(), "cloud"), this.renderer.getId());
    }
}
