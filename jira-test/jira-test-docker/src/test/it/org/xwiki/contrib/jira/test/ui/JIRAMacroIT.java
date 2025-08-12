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

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.xwiki.administration.test.po.AdministrationPage;
import org.xwiki.contrib.jira.test.po.JIRAAdministrationSectionPage;
import org.xwiki.test.docker.junit5.UITest;
import org.xwiki.test.ui.TestUtils;
import org.xwiki.test.ui.po.ViewPage;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verify the overall JIRA Macro feature.
 *
 * @version $Id$
 * @since 8.2
 */
@UITest
class JIRAMacroIT
{
    private WireMockServer wireMockServer;

    @BeforeEach
    void proxyToWireMock()
    {
        this.wireMockServer = new WireMockServer(8889);
        this.wireMockServer.start();
    }

    @AfterEach
    void noMoreWireMock()
    {
        this.wireMockServer.stop();
        this.wireMockServer = null;
    }

    @Test
    void verifyMacro(TestUtils setup, TestInfo info)
    {
        setup.loginAsSuperAdmin();

        // Setup Wiremock to simulate a JIRA instance
        this.wireMockServer.stubFor(get(urlMatching(
            "\\/sr\\/jira.issueviews:searchrequest-xml\\/temp\\/SearchRequest\\.xml\\?jqlQuery=.*"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/xml")
                .withBodyFile("input.xml")));

        // Navigate to the JIRA Admin UI and set up a JIRA instance pointing to localhost

        // Verify that there is a jira section
        AdministrationPage wikiAdministrationPage = AdministrationPage.gotoPage();
        assertTrue(wikiAdministrationPage.hasSection("Other", "JIRA"));

        // Setup a jira instance
        wikiAdministrationPage.clickSection("Other", "JIRA");
        JIRAAdministrationSectionPage jiraPage = new JIRAAdministrationSectionPage();
        jiraPage.setId(0, "local");
        jiraPage.setURL(0, "http://localhost:8889");
        jiraPage.clickSave();
        // we need to handle the wait because the save call `preventDefault()`
        // so we need specify to selenium unit what we need to wait
        WebDriverWait wait = new WebDriverWait(setup.getDriver(), Duration.ofSeconds(10));
        wait.until(webDriver -> (((org.openqa.selenium.JavascriptExecutor) webDriver)
            .executeScript("return document.readyState")).equals("complete"));

        // Now create a new page and try using the jira macro in it + verify that the scripting jira api works too
        String velocity = "{{jira id=\"local\"}}\n"
            + "XWIKI-1000\n"
            + "XWIKI-1001\n"
            + "{{/jira}}\n\n"
            + "{{velocity}}\n"
            + "$services.jira.getJiraRestClient('local').class.name\n"
            + "{{/velocity}}";

        ViewPage vp = setup.createPage(info.getTestClass().get().getName(),
            info.getTestMethod().get().getName(), velocity, "");

        // Since the macro is Async, wait for the expected content to be available
        vp.waitUntilContent("\\QType Key Summary Status Created Date\n"
            + "XWIKI-1000 Improve PDF Output 19-Mar-2007\n"
            + "XWIKI-1001 On jetty, non-default skins are not usable 19-Mar-2007\n"
            + "com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient\\E");

        // Verify XXE protection.

        // Simulate a JIRA response containing a DOCTYPE declaration with a DTD to verify that we don't parse it, to
        // avoid an XXE attack. The returned XML will try to display the content of the /etc/passwd file into the
        // "summary" jira field of an issue, to try to have it displayed by the jira macro.
        this.wireMockServer.stubFor(get(urlMatching(
            "\\/sr\\/jira.issueviews:searchrequest-xml\\/temp\\/SearchRequest\\.xml\\?jqlQuery=.*"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/xml")
                .withBodyFile("xxe.xml")));

        // Refresh the created page and verify an error is now displayed
        setup.getDriver().navigate().refresh();

        // Since the macro is Async, wait for the expected content to be available
        // Verify that the XXE protection has prevented expanding the &xxe; entity into the summary field.
        vp.waitUntilContent("\\QType Key Summary Status Created Date\n"
            + "XWIKI-1001 19-Mar-2007\n"
            + "com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient\\E");
    }
}