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
package org.xwiki.contrib.jira.test.ui;

import org.junit.*;
import org.xwiki.administration.test.po.AdministrationPage;
import org.xwiki.contrib.jira.test.po.JIRAAdministrationSectionPage;
import org.xwiki.test.ui.AbstractTest;
import org.xwiki.test.ui.SuperAdminAuthenticationRule;
import org.xwiki.test.ui.po.ViewPage;

import static org.junit.Assert.*;

/**
 * Verify the overall JIRA Macro feature.
 *
 * @version $Id$
 * @since 8.2
 */
public class JIRAMacroTest extends AbstractTest
{
    @Rule
    public SuperAdminAuthenticationRule authenticationRule = new SuperAdminAuthenticationRule(getUtil(), getDriver());

    @Test
    public void verifyMacro() throws Exception
    {
        // Navigate to the JIRA Admin UI and set up a JIRA instance pointing to localhost

        // Verify that there is a jira section
        AdministrationPage wikiAdministrationPage = AdministrationPage.gotoPage();
        assertTrue(wikiAdministrationPage.hasSection("Applications", "JIRA"));

        // Setup a jira instance
        wikiAdministrationPage.clickSection("Applications", "JIRA");
        JIRAAdministrationSectionPage jiraPage = new JIRAAdministrationSectionPage();
        jiraPage.setId(0, "local");
        jiraPage.setURL(0, "http://localhost");
        jiraPage.clickSave();

        // Now create a new page and try using the jira macro in it + verify that the scripting jira api works too
        String velocity = "{{jira id=\"local\"}}\n"
            + "XWIKI-1000\n"
            + "XWIKI-1001\n"
            + "{{/jira}}\n\n"
            + "{{velocity}}\n"
            + "$services.jira.getJiraRestClient('local').class.name\n"
            + "{{/velocity}}";

        ViewPage vp = getUtil().createPage(getTestClassName(), getTestMethodName(), velocity, "");

        assertEquals("Type Key Summary Status Created Date\n"
            + "XWIKI-1000 Improve PDF Output 19-Mar-2007\n"
            + "XWIKI-1001 On jetty, non-default skins are not usable 19-Mar-2007\n"
            + "com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClient", vp.getContent());
    }
}
