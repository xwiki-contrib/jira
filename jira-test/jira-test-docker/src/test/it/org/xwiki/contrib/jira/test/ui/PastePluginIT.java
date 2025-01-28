package org.xwiki.contrib.jira.test.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.xwiki.administration.test.po.AdministrationPage;
import org.xwiki.ckeditor.test.po.CKEditor;
import org.xwiki.contrib.jira.test.po.JIRAAdministrationSectionPage;
import org.xwiki.test.docker.junit5.UITest;
import org.xwiki.test.ui.TestUtils;
import org.xwiki.test.ui.po.ViewPage;
import org.xwiki.test.ui.po.editor.WikiEditPage;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;

@UITest
public class PastePluginIT
{
    private static final String ADMIN_CONFIG_JIRA_ID = "testId";
    private static final String ADMIN_CONFIG_JIRA_URL = "https://localhost:8889";
    @BeforeAll
    void setup(TestUtils setup) {
        setup.deletePage("Test", "Page");
    }

    @Test
    void verifyPlugin (TestUtils setup, TestInfo info) {
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

        ViewPage vp = setup.createPage("Test", "Page", "", "");
        WikiEditPage editPage = vp.editWiki();
        editPage.setContent(ADMIN_CONFIG_JIRA_URL + "/browse/XWIKI-1000");
        editPage.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        editPage.sendKeys(Keys.chord(Keys.CONTROL, "c"));
        editPage.clearContent();

        editPage.editWYSIWYG();
        CKEditor ckEditor = new CKEditor("content").waitToLoad();
        setup.getDriver().findElement(By.className("cke_notification_close")).click();

        ckEditor.getRichTextArea().sendKeys(Keys.chord(Keys.CONTROL, "v"));
        editPage.clickSaveAndContinue(true);

        editPage.editWiki().waitUntilPageIsReady();

        editPage = new WikiEditPage();

        String content = editPage.getContent();
        assertEquals("{{jira id=\"testId\" url=\"\" style=\"enum\" fields=\"type,key,summary,status\" source=\"list\" maxCount=\"20\"}}\n"
            + "XWIKI-1000\n"
            + "{{/jira}}", content);

    }
}
