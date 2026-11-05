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

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.count.JIRACountMacroParameters;
import org.xwiki.contrib.jira.macro.JIRABadRequestException;
import org.xwiki.contrib.jira.macro.JIRAConnectionException;
import org.xwiki.contrib.jira.macro.JIRAConnector;
import org.xwiki.contrib.jira.macro.internal.JIRAConnectorManager;
import org.xwiki.contrib.jira.macro.internal.JIRAErrorGenerator;
import org.xwiki.contrib.jira.macro.internal.JIRAMacroTransformationManager;
import org.xwiki.contrib.jira.macro.internal.source.JIRAServerResolver;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.ParagraphBlock;
import org.xwiki.rendering.block.WordBlock;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.rendering.transformation.MacroTransformationContext;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link JIRACountMacro}.
 */
@ComponentTest
class JIRACountMacroTest
{
    @InjectMockComponents
    private JIRACountMacro macro;

    @MockComponent
    private JIRAServerResolver jiraServerResolver;

    @MockComponent
    private JIRAConnectorManager connectorManager;

    @MockComponent
    private JIRAMacroTransformationManager jiraMacroTransformationManager;

    @MockComponent
    private JIRAErrorGenerator jiraErrorGenerator;

    @Mock
    private JIRAConnector connector;

    private JIRAServer jiraServer = new JIRAServer("http://localhost/jira", "id");

    private JIRACountMacroParameters parameters = new JIRACountMacroParameters();

    private MacroTransformationContext context = new MacroTransformationContext();

    @BeforeEach
    void setUp() throws Exception
    {
        when(this.jiraServerResolver.resolve(this.parameters)).thenReturn(this.jiraServer);
        when(this.connectorManager.getConnector(this.jiraServer)).thenReturn(this.connector);
        // Return the blocks without transforming them.
        when(this.jiraMacroTransformationManager.transform(any(), eq(this.parameters), eq(this.context),
            eq(this.jiraServer), eq("jiraCount"))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void executeWhenInline() throws Exception
    {
        this.context.setInline(true);
        when(this.connector.countIssues(this.jiraServer, "project = TEST")).thenReturn(1234);

        assertEquals(List.of(new WordBlock("1234")),
            this.macro.execute(this.parameters, "project = TEST", this.context));
    }

    @Test
    void executeWhenStandalone() throws Exception
    {
        when(this.connector.countIssues(this.jiraServer, "project = TEST")).thenReturn(2);

        assertEquals(List.of(new ParagraphBlock(List.of(new WordBlock("2")))),
            this.macro.execute(this.parameters, "project = TEST", this.context));
    }

    @Test
    void executeWhenNoQuery()
    {
        MacroExecutionException exception = assertThrows(MacroExecutionException.class,
            () -> this.macro.execute(this.parameters, " ", this.context));

        assertEquals("Missing JQL query!", exception.getMessage());
    }

    @Test
    void executeWhenBadRequest() throws Exception
    {
        this.context.setInline(true);
        JIRABadRequestException badRequest = new JIRABadRequestException("error", List.of("message"));
        when(this.connector.countIssues(this.jiraServer, "project = TEST")).thenThrow(badRequest);
        List<Block> errorBlocks = List.of(new WordBlock("error"));
        when(this.jiraErrorGenerator.getBadRequestErrorBlock(badRequest, true)).thenReturn(errorBlocks);

        // The error is displayed instead of being thrown, so that the JIRA macro transformations are still executed.
        assertEquals(errorBlocks, this.macro.execute(this.parameters, "project = TEST", this.context));
    }

    @Test
    void executeWhenConnectionError() throws Exception
    {
        JIRAConnectionException cause = new JIRAConnectionException("error");
        when(this.connector.countIssues(this.jiraServer, "project = TEST")).thenThrow(cause);

        MacroExecutionException exception = assertThrows(MacroExecutionException.class,
            () -> this.macro.execute(this.parameters, "project = TEST", this.context));

        assertEquals("Failed to retrieve JIRA data from [http://localhost/jira] for JQL [project = TEST]",
            exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
