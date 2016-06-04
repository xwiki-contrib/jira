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
import org.junit.Rule;
import org.junit.Test;
import org.xwiki.contrib.jira.config.JIRAConfiguration;
import org.xwiki.contrib.jira.macro.JIRAMacroParameters;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import static org.xwiki.contrib.jira.macro.JIRAField.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ListJIRADataSource} and
 * {@link org.xwiki.contrib.jira.macro.internal.source.AbstractJIRADataSource}.
 *
 * @version $Id$
 * @since 4.2M1
 */
public class ListJIRADataSourceTest
{
    @Rule
    public MockitoComponentMockingRule<ListJIRADataSource> mocker =
        new MockitoComponentMockingRule<>(ListJIRADataSource.class);

    @Test
    public void parseIdsWhenNull() throws Exception
    {
        assertEquals(Collections.emptyList(), this.mocker.getComponentUnderTest().parseIds(null));
    }

    @Test
    public void parseIds() throws Exception
    {
        List<Pair<String, String>> expected = Arrays.<Pair<String, String>>asList(
            new ImmutablePair<String, String>("ISSUE-1", ""),
            new ImmutablePair<String, String>("ISSUE-2", "Whatever"),
            new ImmutablePair<String, String>("ISSUE-3", ""));
        assertEquals(expected,
            this.mocker.getComponentUnderTest().parseIds("\nISSUE-1\nISSUE-2 |Whatever \n ISSUE-3\n"));
    }

    @Test
    public void constructJQLQuery() throws Exception
    {
        List<Pair<String, String>> ids = Arrays.<Pair<String, String>>asList(
            new ImmutablePair<String, String>("ISSUE-1", ""),
            new ImmutablePair<String, String>("ISSUE-2", "Whatever"));
        assertEquals("issueKey in (ISSUE-1,ISSUE-2)", this.mocker.getComponentUnderTest().constructJQLQuery(ids));
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
    public void buildIssues() throws Exception
    {
        Document document = new SAXBuilder().build(getClass().getResourceAsStream("/input.xml"));
        List<Pair<String, String>> ids = Arrays.<Pair<String, String>>asList(
            new ImmutablePair<String, String>("XWIKI-1000", ""),
            new ImmutablePair<String, String>("XWIKI-1001", "Note"));

        List<Element> issues = this.mocker.getComponentUnderTest().buildIssues(document, ids);

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
    public void getJIRAServerWhenNoneDefined() throws Exception
    {
        try {
            this.mocker.getComponentUnderTest().getJIRAServer(new JIRAMacroParameters());
            fail("should have thrown an exception");
        } catch (MacroExecutionException expected) {
            assertEquals("No JIRA Server found. You must specify a JIRA server, using the \"url\" macro parameter or "
                + "using the \"id\" macro parameter to reference a server defined in the JIRA Macro configuration.",
                expected.getMessage());
        }
    }

    @Test
    public void getJIRAServerWhenIdUsedButNoneDefined() throws Exception
    {
        JIRAMacroParameters parameters = new JIRAMacroParameters();
        parameters.setId("unknownid");
        try {
            this.mocker.getComponentUnderTest().getJIRAServer(parameters);
            fail("should have thrown an exception");
        } catch (MacroExecutionException expected) {
            assertEquals("The JIRA Server id [unknownid] is not defined in the macro's configuration. Please fix the "
                + "id or add a new server in the JIRA Macro configuration.", expected.getMessage());
        }
    }

    @Test
    public void getJIRAServerWhenIdUsedAndDefined() throws Exception
    {
        JIRAConfiguration configuration = this.mocker.getInstance(JIRAConfiguration.class);
        when(configuration.getJIRAServers()).thenReturn(Collections.singletonMap("someid",
            new JIRAServer("http://localhost")));

        JIRAMacroParameters parameters = new JIRAMacroParameters();
        parameters.setId("someid");
        assertEquals("http://localhost", this.mocker.getComponentUnderTest().getJIRAServer(parameters).getURL());
    }

    @Test
    public void getJIRAServerWhenURLSpecifiedAndMatchingConfigurationExist() throws Exception
    {
        JIRAConfiguration configuration = this.mocker.getInstance(JIRAConfiguration.class);
        when(configuration.getJIRAServers()).thenReturn(Collections.singletonMap("whatever",
            new JIRAServer("http://localhost", "username", "password")));

        JIRAMacroParameters parameters = new JIRAMacroParameters();
        parameters.setURL("http://localhost");

        assertEquals("http://localhost", this.mocker.getComponentUnderTest().getJIRAServer(parameters).getURL());
        assertEquals("username", this.mocker.getComponentUnderTest().getJIRAServer(parameters).getUsername());
    }

    @Test
    public void computeFullURL() throws Exception
    {
        // No credentials passed
        JIRAServer jiraServer = new JIRAServer("http://localhost/jira");
        assertEquals("http://localhost/jira/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=query",
            this.mocker.getComponentUnderTest().computeFullURL(jiraServer, "query", -1));

        // Just username defined but no password (or empty password)
        jiraServer = new JIRAServer("http://localhost/jira", "username", "");
        assertEquals("http://localhost/jira/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=query",
            this.mocker.getComponentUnderTest().computeFullURL(jiraServer, "query", -1));

        // With credentials
        jiraServer = new JIRAServer("http://localhost/jira", "username", "password");
        assertEquals("http://localhost/jira/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?"
            + "jqlQuery=query&os_username=username&os_password=password&os_authType=basic",
            this.mocker.getComponentUnderTest().computeFullURL(jiraServer, "query", -1));

        // With Max Count + credentials
        jiraServer = new JIRAServer("http://localhost/jira", "username", "password");
        assertEquals("http://localhost/jira/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?"
                + "jqlQuery=query&os_username=username&os_password=password&os_authType=basic&tempMax=5",
            this.mocker.getComponentUnderTest().computeFullURL(jiraServer, "query", 5));

        // With Max Count and no credentials
        jiraServer = new JIRAServer("http://localhost/jira");
        assertEquals("http://localhost/jira/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?"
                + "jqlQuery=query&tempMax=5",
            this.mocker.getComponentUnderTest().computeFullURL(jiraServer, "query", 5));
    }
}
