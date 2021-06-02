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

import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.xwiki.administration.test.po.AdministrationPage;
import org.xwiki.contrib.jira.test.po.JIRAAdministrationSectionPage;
import org.xwiki.test.docker.junit5.UITest;
import org.xwiki.test.ui.TestUtils;
import org.xwiki.test.ui.XWikiWebDriver;
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
    void verifyMacro(TestUtils setup, XWikiWebDriver webDriver, TestInfo info)
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
        waitUntilContent("\\QType Key Summary Status Created Date\n"
            + "XWIKI-1000 Improve PDF Output 19-Mar-2007\n"
            + "XWIKI-1001 On jetty, non-default skins are not usable 19-Mar-2007\n"
            + "com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClient\\E", vp, webDriver);
    }

    // TODO: Remove once the parent pom is updated to be Platform 12.10.
    private void waitUntilContent(String expectedValue, ViewPage vp, XWikiWebDriver webDriver)
    {
        // Using an array to have an effectively final variable.
        final String[] lastContent = new String[1];
        try {
            webDriver.waitUntilCondition(new ExpectedCondition<Boolean>()
            {
                private Pattern pattern = Pattern.compile(expectedValue, 32);

                public Boolean apply(WebDriver driver)
                {
                    lastContent[0] = vp.getContent();
                    return this.pattern.matcher(lastContent[0]).matches();
                }
            });
        } catch (TimeoutException e) {
            throw new TimeoutException(String.format("Got [%s]\nExpected [%s]", lastContent[0], expectedValue), e);
        }
    }
}
