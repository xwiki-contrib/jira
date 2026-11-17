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

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.xwiki.administration.test.po.AdministrationPage;
import org.xwiki.contrib.jira.test.po.JIRAAdministrationSectionPage;
import org.xwiki.test.docker.junit5.UITest;
import org.xwiki.test.ui.TestUtils;
import org.xwiki.test.ui.po.ViewPage;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    private static final String TEST_SPACE = "JIRATest";

    private static final String BASIC_AUTH = "basicAuth";

    private static final String DEPLOYMENT_TYPE = "Deployment type";

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
    @Order(1)
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
        jiraPage.clickAddInstance();
        jiraPage.setId("local");
        jiraPage.setURL("http://localhost:8889");
        jiraPage.saveInstance();

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

    /**
     * An extension can contribute a deployment type, with its own labels for the basic authentication configuration.
     */
    @Test
    @Order(2)
    void deploymentTypes(TestUtils setup)
    {
        setup.loginAsSuperAdmin();

        // Contribute the "testcloud" deployment type, as an extension would.
        setup.createPage(TEST_SPACE, "Translations", "jira.test.deploymentType.testcloud=Test Cloud\n"
            + "jira.config.auth.basicAuth.username.testcloud=Email\n"
            + "jira.config.auth.basicAuth.password.testcloud=API token", "");
        setup.addObject(TEST_SPACE, "Translations", "XWiki.TranslationDocumentClass", "scope", "WIKI");
        setup.createPage(TEST_SPACE, "TestCloudDeploymentType", "", "");
        setup.addObject(TEST_SPACE, "TestCloudDeploymentType", "XWiki.UIExtensionClass",
            "extensionPointId", "org.xwiki.contrib.jira.deploymenttype",
            "name", "org.xwiki.contrib.jira.deploymenttype.testcloud",
            "parameters", "deployment_type=testcloud\ntranslation_key=jira.test.deploymentType.testcloud\norder=10",
            "scope", "wiki");

        JIRAAdministrationSectionPage jiraPage = gotoJIRASection();
        assertTrue(jiraPage.hasDeploymentTypeColumn());
        jiraPage.clickAddInstance();
        assertEquals(List.of("Data Center / Server", "Test Cloud"), jiraPage.getDeploymentTypeLabels());
        jiraPage.setId("cloudjira");
        jiraPage.setURL("http://localhost:8889/cloudjira");
        jiraPage.setAuthenticationType(BASIC_AUTH);
        jiraPage.waitForBasicAuthConfiguration("Username");
        assertEquals("Password", jiraPage.getBasicAuthPasswordLabel());

        // Changing the deployment type reloads the authentication settings, with the labels of that type, keeping the
        // values already typed.
        jiraPage.setBasicAuthCredentials("user@example.com", "token");
        jiraPage.setDeploymentType("testcloud");
        jiraPage.waitForBasicAuthConfiguration("Email");
        assertEquals("API token", jiraPage.getBasicAuthPasswordLabel());
        assertEquals("user@example.com", jiraPage.getBasicAuthUsername());
        jiraPage.saveInstance();
        assertEquals("Test Cloud", jiraPage.getInstanceValue("cloudjira", DEPLOYMENT_TYPE));
        assertEquals("Basic authentication", jiraPage.getInstanceValue("cloudjira", "Authentication type"));

        jiraPage = gotoJIRASection();
        jiraPage.clickInstanceId("cloudjira");
        assertEquals("Test Cloud", jiraPage.getSelectedDeploymentTypeLabel());
        jiraPage.waitForBasicAuthConfiguration("Email");
        assertEquals("user@example.com", jiraPage.getBasicAuthUsername());
        jiraPage.cancelInstance();

        // When the extension providing the deployment type is uninstalled, the stored type is still displayed, so that
        // saving the instance doesn't silently change it.
        setup.deletePage(TEST_SPACE, "TestCloudDeploymentType");
        jiraPage = gotoJIRASection();
        assertTrue(jiraPage.hasDeploymentTypeColumn());
        assertEquals("Unavailable deployment type", jiraPage.getInstanceValue("cloudjira", DEPLOYMENT_TYPE));
        jiraPage.clickEditInstance("cloudjira");
        assertEquals("Unavailable deployment type", jiraPage.getSelectedDeploymentTypeLabel());
        assertFalse(jiraPage.getDeploymentTypeLabels().contains("Test Cloud"));
        jiraPage.cancelInstance();
    }

    /**
     * The JIRA instances are added, edited and deleted from the administration, one at a time.
     */
    @Test
    @Order(3)
    void manageInstances(TestUtils setup)
    {
        setup.loginAsSuperAdmin();

        JIRAAdministrationSectionPage jiraPage = gotoJIRASection();
        jiraPage.clickAddInstance();
        assertEquals("Add JIRA instance", jiraPage.getInstanceFormTitle());

        // The ID and the URL are checked before saving.
        jiraPage.setURL("http://localhost:8889/manage");
        assertEquals("The ID is required.", jiraPage.saveInstanceWithError());
        jiraPage.setId("local");
        assertEquals("A JIRA instance with the ID \"local\" already exists.", jiraPage.saveInstanceWithError());
        jiraPage.setId("manage");
        jiraPage.setURL("localhost:8889/manage");
        assertEquals("The JIRA URL must start with http:// or https://.", jiraPage.saveInstanceWithError());
        jiraPage.setURL("http://localhost:8889/manage");
        jiraPage.setAuthenticationType(BASIC_AUTH);
        jiraPage.waitForBasicAuthConfiguration("Username");
        jiraPage.setBasicAuthCredentials("manager", "secret");
        jiraPage.saveInstance();
        assertEquals("http://localhost:8889/manage", jiraPage.getInstanceValue("manage", "JIRA URL"));
        assertEquals("Basic authentication", jiraPage.getInstanceValue("manage", "Authentication type"));

        // Renaming the instance keeps its credentials, including the password when it's left empty.
        jiraPage.clickEditInstance("manage");
        assertEquals("Edit JIRA instance", jiraPage.getInstanceFormTitle());
        assertEquals("manage", jiraPage.getId());
        jiraPage.waitForBasicAuthConfiguration("Username");
        assertEquals("manager", jiraPage.getBasicAuthUsername());
        assertTrue(jiraPage.hasStoredBasicAuthPassword());
        jiraPage.setId("managed");
        jiraPage.saveInstance();
        jiraPage.waitUntilInstanceIsNotListed("manage");
        jiraPage.clickInstanceId("managed");
        jiraPage.waitForBasicAuthConfiguration("Username");
        assertEquals("manager", jiraPage.getBasicAuthUsername());
        assertTrue(jiraPage.hasStoredBasicAuthPassword());
        jiraPage.cancelInstance();

        // Deleting an instance deletes its credentials: a new instance with the same ID doesn't get them.
        jiraPage.clickAddInstance();
        jiraPage.setId("temporary");
        jiraPage.setURL("http://localhost:8889/temporary");
        jiraPage.setAuthenticationType(BASIC_AUTH);
        jiraPage.waitForBasicAuthConfiguration("Username");
        jiraPage.setBasicAuthCredentials("temporary", "secret");
        jiraPage.saveInstance();
        // Each instance keeps its own credentials (JIRA-124).
        jiraPage.clickInstanceId("temporary");
        jiraPage.waitForBasicAuthConfiguration("Username");
        assertEquals("temporary", jiraPage.getBasicAuthUsername());
        jiraPage.cancelInstance();
        jiraPage.clickInstanceId("managed");
        jiraPage.waitForBasicAuthConfiguration("Username");
        assertEquals("manager", jiraPage.getBasicAuthUsername());
        assertTrue(jiraPage.hasStoredBasicAuthPassword());
        jiraPage.cancelInstance();
        jiraPage.deleteInstance("temporary");
        jiraPage.clickAddInstance();
        jiraPage.setId("temporary");
        jiraPage.setURL("http://localhost:8889/temporary");
        jiraPage.setAuthenticationType(BASIC_AUTH);
        jiraPage.waitForBasicAuthConfiguration("Username");
        jiraPage.saveInstance();
        jiraPage.clickInstanceId("temporary");
        jiraPage.waitForBasicAuthConfiguration("Username");
        assertEquals("", jiraPage.getBasicAuthUsername());
        assertFalse(jiraPage.hasStoredBasicAuthPassword());
        jiraPage.cancelInstance();
        jiraPage.deleteInstance("temporary");
        jiraPage.clickInstanceId("managed");
        jiraPage.waitForBasicAuthConfiguration("Username");

        // Switching to no authentication removes the credentials.
        jiraPage.setAuthenticationType("noAuth");
        jiraPage.waitForNoAuthConfiguration();
        jiraPage.saveInstance();
        assertEquals("No authentication", jiraPage.getInstanceValue("managed", "Authentication type"));
        jiraPage.clickInstanceId("managed");
        jiraPage.setAuthenticationType(BASIC_AUTH);
        jiraPage.waitForBasicAuthConfiguration("Username");
        assertEquals("", jiraPage.getBasicAuthUsername());
        jiraPage.cancelInstance();

        // The other instances are kept when one is saved or deleted.
        jiraPage.deleteInstance("managed");
        jiraPage = gotoJIRASection();
        assertEquals(List.of("local", "cloudjira"), jiraPage.getInstanceIds());
    }

    private JIRAAdministrationSectionPage gotoJIRASection()
    {
        AdministrationPage administrationPage = AdministrationPage.gotoPage();
        administrationPage.clickSection("Other", "JIRA");
        return new JIRAAdministrationSectionPage();
    }
}
