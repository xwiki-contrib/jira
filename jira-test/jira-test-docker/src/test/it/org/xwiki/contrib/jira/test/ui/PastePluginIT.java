package org.xwiki.contrib.jira.test.ui;

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

    private static final String ADMIN_CONFIG_JIRA_URL = "https://localhost:8889";

    private static final EntityReference TEST_PAGE_REFERENCE = new DocumentReference("wiki", "Test", "Macro");

    private static final String LINK_JIRA_TICKET = ADMIN_CONFIG_JIRA_URL + "/browse/XWIKI-1000";

    private static final String LINK_JIRA_JQL = ADMIN_CONFIG_JIRA_URL + "/browse/JIRA-65?jql=project%20%3D%20JIRA"
        + "%20AND%20resolution%20%3D%20Unresolved%20ORDER%20BY%20priority%20DESC%2C%20updated%20DESC";

    @BeforeAll
    void setup(TestUtils setup)
    {
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
        WikiEditPage wikiEditPage = pasteClipboardInCKEditorAndEditWiki(vp, setup, true);

        String content = wikiEditPage.getContent();
        assertEquals(
            "[[https:~~/~~/jira.unconfigured.test/browse/TICKET-134>>https://jira.unconfigured.test/browse/TICKET-134]]",
            content);
    }

    @Test
    void pasteLinkWithoutFormatting(TestUtils setup)
    {
        copyInClipboard(LINK_JIRA_TICKET, setup);

        ViewPage vp = setup.gotoPage(TEST_PAGE_REFERENCE);
        WikiEditPage wikiEditPage = pasteClipboardInCKEditorAndEditWiki(vp, setup, true);

        String content = wikiEditPage.getContent();
        assertEquals("[[https:~~/~~/localhost:8889/browse/XWIKI-1000>>https://localhost:8889/browse/XWIKI-1000]]",
            content);
    }

    @Test
    void pasteLinkWithTicket(TestUtils setup)
    {
        copyInClipboard(LINK_JIRA_TICKET, setup);

        ViewPage vp = setup.gotoPage(TEST_PAGE_REFERENCE);
        WikiEditPage wikiEditPage = pasteClipboardInCKEditorAndEditWiki(vp, setup, false);

        String content = wikiEditPage.getContent();
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
        WikiEditPage wikiEditPage = pasteClipboardInCKEditorAndEditWiki(vp, setup, false);

        String content = wikiEditPage.getContent();
        assertEquals(
            "{{jira id=\"testId\" url=\"\" style=\"table\" fields=\"type,key,summary,status\" source=\"jql\" maxCount=\"20\"}}\n"
                + "project = JIRA AND resolution = Unresolved ORDER BY priority DESC, updated DESC\n"
                + "{{/jira}}", content);
    }

    private WikiEditPage pasteClipboardInCKEditorAndEditWiki(ViewPage page, TestUtils setup, boolean withoutFormatting)
    {
        WYSIWYGEditPage editPage = page.editWYSIWYG();
        CKEditor ckEditor = new CKEditor("content").waitToLoad();
        setup.getDriver().findElement(By.className("cke_notification_close")).click();

        String paste =
            withoutFormatting ? Keys.chord(Keys.CONTROL, Keys.LEFT_SHIFT, "v") : Keys.chord(Keys.CONTROL, "v");
        ckEditor.getRichTextArea().sendKeys(paste);
        editPage.clickSaveAndContinue(true);

        WikiEditPage wikiEditPage = editPage.editWiki();
        wikiEditPage.waitUntilPageIsReady();

        return wikiEditPage;
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
