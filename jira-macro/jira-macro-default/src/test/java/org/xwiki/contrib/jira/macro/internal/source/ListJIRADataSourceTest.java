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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.jupiter.api.Test;
import org.xwiki.contrib.jira.config.JIRAConfiguration;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.config.internal.BasicAuthJIRAAuthenticator;
import org.xwiki.contrib.jira.macro.JIRAMacroParameters;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.xwiki.contrib.jira.macro.JIRAField.KEY;
import static org.xwiki.contrib.jira.macro.JIRAField.NOTE;
import static org.xwiki.contrib.jira.macro.JIRAField.SUMMARY;

/**
 * Unit tests for {@link ListJIRADataSource} and
 * {@link org.xwiki.contrib.jira.macro.internal.source.AbstractJIRADataSource}.
 *
 * @version $Id$
 * @since 4.2M1
 */
@ComponentList({
    DefaultJIRAServerResolver.class
})
@ComponentTest
class ListJIRADataSourceTest
{
    @InjectMockComponents
    private ListJIRADataSource jiraDataSource;

    @MockComponent
    private JIRAConfiguration jiraConfiguration;

    @Test
    void parseIdsWhenNull() throws Exception
    {
        assertEquals(Collections.emptyList(), this.jiraDataSource.parseIds(null));
    }

    @Test
    void parseIds() throws Exception
    {
        List<Pair<String, String>> expected = Arrays.asList(
            new ImmutablePair<>("ISSUE-1", ""),
            new ImmutablePair<>("ISSUE-2", "Whatever"),
            new ImmutablePair<>("ISSUE-3", ""));
        assertEquals(expected,
            this.jiraDataSource.parseIds("\nISSUE-1\nISSUE-2 |Whatever \n ISSUE-3\n"));
    }

    @Test
    void constructJQLQuery() throws Exception
    {
        List<Pair<String, String>> ids = Arrays.asList(
            new ImmutablePair<>("ISSUE-1", ""),
            new ImmutablePair<>("ISSUE-2", "Whatever"));
        assertEquals("issueKey in (ISSUE-1,ISSUE-2)", this.jiraDataSource.constructJQLQuery(ids));
    }

    /**
     * Verify several things:
     * <ul>
     *     <li>Issue order is preserved even though JIRA returns them in no specific order</li>
     *     <li>List fields are supported (for example the "version" field)</li>
     *     <li>Notes are taken into account</li>
     * </ul>
     */
    @Test
    void buildIssues() throws Exception
    {
        Document document = new SAXBuilder().build(getClass().getResourceAsStream("/__files/input.xml"));
        List<Pair<String, String>> ids = Arrays.asList(
            new ImmutablePair<>("XWIKI-1000", ""),
            new ImmutablePair<>("XWIKI-1001", "Note"));

        List<Element> issues = this.jiraDataSource.buildIssues(document, ids);

        assertEquals(2, issues.size());
        Element issue1 = issues.get(0);
        assertEquals("XWIKI-1000", issue1.getChildTextTrim(KEY.getId()));
        assertEquals("Improve PDF Output", issue1.getChildTextTrim(SUMMARY.getId()));
        Element issue2 = issues.get(1);
        assertEquals("XWIKI-1001", issue2.getChildTextTrim(KEY.getId()));
        assertEquals("On jetty, non-default skins are not usable", issue2.getChildTextTrim(SUMMARY.getId()));
        assertEquals("Note", issue2.getChildTextTrim(NOTE));
    }

    @Test
    void getJIRAServerWhenNoneDefined() throws Exception
    {
        try {
            this.jiraDataSource.getJIRAServer(new JIRAMacroParameters());
            fail("should have thrown an exception");
        } catch (MacroExecutionException expected) {
            assertEquals("No JIRA Server found. You must specify a JIRA server, using the \"url\" macro parameter or "
                    + "using the \"id\" macro parameter to reference a server defined in the JIRA Macro configuration.",
                expected.getMessage());
        }
    }

    @Test
    void getJIRAServerWhenIdUsedButNoneDefined() throws Exception
    {
        JIRAMacroParameters parameters = new JIRAMacroParameters();
        parameters.setId("unknownid");
        try {
            this.jiraDataSource.getJIRAServer(parameters);
            fail("should have thrown an exception");
        } catch (MacroExecutionException expected) {
            assertEquals("The JIRA Server id [unknownid] is not defined in the macro's configuration. Please fix the "
                + "id or add a new server in the JIRA Macro configuration.", expected.getMessage());
        }
    }

    @Test
    void getJIRAServerWhenIdUsedAndDefined() throws Exception
    {
        when(this.jiraConfiguration.getJIRAServers()).thenReturn(Collections.singletonMap("someid",
            new JIRAServer("http://localhost", "id")));

        JIRAMacroParameters parameters = new JIRAMacroParameters();
        parameters.setId("someid");
        assertEquals("http://localhost", this.jiraDataSource.getJIRAServer(parameters).getURL());
    }

    @Test
    void getJIRAServerWhenURLSpecifiedAndMatchingConfigurationExist() throws Exception
    {
        JIRAServer jiraServer =
            new JIRAServer("http://localhost", "whatever", new BasicAuthJIRAAuthenticator("username", "password"));
        when(this.jiraConfiguration.getJIRAServers()).thenReturn(Collections.singletonMap("whatever",
            jiraServer));

        JIRAMacroParameters parameters = new JIRAMacroParameters();
        parameters.setURL("http://localhost");

        assertEquals("http://localhost", this.jiraDataSource.getJIRAServer(parameters).getURL());
        assertEquals(
            BasicAuthJIRAAuthenticator.class,
            this.jiraDataSource.getJIRAServer(parameters).getJiraAuthenticator().orElseThrow().getClass());
    }
}
