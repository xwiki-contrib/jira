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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.xwiki.administration.test.po.AdministrationPage;
import org.xwiki.ckeditor.test.po.CKEditor;
import org.xwiki.contrib.jira.test.po.JIRAAdministrationSectionPage;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.test.docker.junit5.UITest;
import org.xwiki.test.ui.TestUtils;
import org.xwiki.test.ui.po.ViewPage;
import org.xwiki.test.ui.po.editor.WYSIWYGEditPage;
import org.xwiki.test.ui.po.editor.WikiEditPage;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verify the functionality of the CKEditor paste plugin.
 *
 * @version $Id$
 * @since 10.0
 */
@UITest
public class PastePluginIT
{
    private static final String ADMIN_CONFIG_JIRA_ID = "testId";

    private static final String ADMIN_CONFIG_JIRA_URL = "http://localhost:8889";

    private static final EntityReference TEST_PAGE_REFERENCE = new DocumentReference("wiki", "Test", "Macro");

    private static final String LINK_JIRA_TICKET = ADMIN_CONFIG_JIRA_URL + "/browse/XWIKI-1000";

    private static final String LINK_JIRA_JQL =
        ADMIN_CONFIG_JIRA_URL + "/issues/?jql=issuekey%20in%20(XWIKI-1000%2C%20XWIKI-1001)";

    private WireMockServer wireMockServer;

    @AfterAll
    void noMoreWireMock()
    {
        this.wireMockServer.stop();
        this.wireMockServer = null;
    }

    @BeforeAll
    void setup(TestUtils setup)
    {
        this.wireMockServer = new WireMockServer(8889);
        this.wireMockServer.start();

        this.wireMockServer.stubFor(get(urlMatching(
            "\\/sr\\/jira.issueviews:searchrequest-xml\\/temp\\/SearchRequest\\.xml\\?jqlQuery=.*"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/xml")
                .withBodyFile("input.xml")));

        setup.deletePage(TEST_PAGE_REFERENCE);

        setup.loginAsSuperAdmin();

        AdministrationPage wikiAdministrationPage = AdministrationPage.gotoPage();

        wikiAdministrationPage.clickSection("Other", "JIRA");
        JIRAAdministrationSectionPage jiraPage = new JIRAAdministrationSectionPage();
        jiraPage.setId(0, ADMIN_CONFIG_JIRA_ID);
        jiraPage.setURL(0, ADMIN_CONFIG_JIRA_URL);
        jiraPage.clickSave();
        wikiAdministrationPage = new AdministrationPage();
        wikiAdministrationPage.waitUntilPageIsReady();

        wikiAdministrationPage.clickSection("Other", "WYSIWYG Editor");
        WebElement element = setup.getDriver().findElement(By.id("CKEditor.ConfigClass_0_advanced"));
        element.clear();
        element.sendKeys("config.extraPlugins = 'xwiki-jira-paste';");
        WebElement saveButton = setup.getDriver().findElement(By.xpath("//input[@type='submit'][@name"
            + "='action_saveandcontinue']"));
        saveButton.click();
    }

    @Test
    void pasteLinkFromUnconfiguredJiraInstance(TestUtils setup)
    {
        String linkFromUnconfiguredJiraInstance = "https://jira.unconfigured.test/browse/TICKET-134";
        copyInClipboard(linkFromUnconfiguredJiraInstance, setup);

        ViewPage vp = setup.gotoPage(TEST_PAGE_REFERENCE);

        String content = pasteClipboardInCKEditorAndEditWiki(vp, setup, true);
        assertEquals(
            "[[https:~~/~~/jira.unconfigured.test/browse/TICKET-134>>https://jira.unconfigured.test/browse/TICKET-134]]",
            content);
    }

    @Test
    void pasteLinkWithoutFormatting(TestUtils setup)
    {
        copyInClipboard(LINK_JIRA_TICKET, setup);

        ViewPage vp = setup.gotoPage(TEST_PAGE_REFERENCE);

        String content = pasteClipboardInCKEditorAndEditWiki(vp, setup, true);
        assertEquals("[[http:~~/~~/localhost:8889/browse/XWIKI-1000>>http://localhost:8889/browse/XWIKI-1000]]",
            content);
    }

    @Test
    void pasteLinkWithTicket(TestUtils setup)
    {
        copyInClipboard(LINK_JIRA_TICKET, setup);

        ViewPage vp = setup.gotoPage(TEST_PAGE_REFERENCE);
        WYSIWYGEditPage editPage = vp.editWYSIWYG();
        CKEditor ckEditor = new CKEditor("content").waitToLoad();
        setup.getDriver().findElement(By.className("cke_notification_close")).click();
        String paste = Keys.chord(Keys.CONTROL, "v");
        ckEditor.getRichTextArea().sendKeys(paste);

        setup.getDriver().waitUntilCondition(
            webDriver -> ckEditor.getRichTextArea().getText().equals("XWIKI-1000 Improve PDF Output"));

        String content = saveAndGetContent(editPage);
        assertEquals(
            "{{jira id=\"testId\" url=\"\" style=\"enum\" fields=\"type,key,summary,status\" source=\"list\" maxCount=\"20\"}}\n"
                + "XWIKI-1000\n"
                + "{{/jira}}", content);
    }

    @Test
    void pasteLinkWithJQL(TestUtils setup)
    {
        copyInClipboard(LINK_JIRA_JQL, setup);

        ViewPage vp = setup.gotoPage(TEST_PAGE_REFERENCE);
        WYSIWYGEditPage editPage = vp.editWYSIWYG();
        CKEditor ckEditor = new CKEditor("content").waitToLoad();
        setup.getDriver().findElement(By.className("cke_notification_close")).click();
        String paste = Keys.chord(Keys.CONTROL, "v");
        ckEditor.getRichTextArea().sendKeys(paste);

        setup.getDriver()
            .waitUntilCondition(webDriver -> ckEditor.getRichTextArea().getText().equals("Type Key Summary Status\n"
                + "XWIKI-1001 On jetty, non-default skins are not usable\n"
                + "XWIKI-1000 Improve PDF Output"));

        String content = saveAndGetContent(editPage);
        assertEquals(
            "{{jira id=\"testId\" url=\"\" style=\"table\" fields=\"type,key,summary,status\" source=\"jql\" maxCount=\"20\"}}\n"
                + "issuekey in (XWIKI-1000, XWIKI-1001)\n"
                + "{{/jira}}", content);
    }

    private String saveAndGetContent(WYSIWYGEditPage editPage)
    {
        editPage.clickSaveAndContinue(true);
        WikiEditPage wikiEditPage = editPage.editWiki();
        wikiEditPage.waitUntilPageIsReady();
        return wikiEditPage.getContent();
    }

    private String pasteClipboardInCKEditorAndEditWiki(ViewPage page, TestUtils setup, boolean withoutFormatting)
    {
        WYSIWYGEditPage editPage = page.editWYSIWYG();
        CKEditor ckEditor = new CKEditor("content").waitToLoad();
        setup.getDriver().findElement(By.className("cke_notification_close")).click();

        String paste =
            withoutFormatting ? Keys.chord(Keys.CONTROL, Keys.LEFT_SHIFT, "v") : Keys.chord(Keys.CONTROL, "v");
        ckEditor.getRichTextArea().sendKeys(paste);

        return saveAndGetContent(editPage);
    }

    private void copyInClipboard(String stringToCopy, TestUtils setup)
    {
        ViewPage vp = setup.createPage(TEST_PAGE_REFERENCE, "", "");
        WikiEditPage editPage = vp.editWiki();
        editPage.setContent(stringToCopy);
        editPage.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        editPage.sendKeys(Keys.chord(Keys.CONTROL, "c"));
        editPage.clearContent();
    }
}
