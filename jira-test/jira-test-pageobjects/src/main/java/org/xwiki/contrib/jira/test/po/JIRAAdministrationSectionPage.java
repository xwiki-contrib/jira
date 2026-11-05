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
package org.xwiki.contrib.jira.test.po;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.xwiki.administration.test.po.AdministrationSectionPage;

/**
 * Represents the actions possible on the JIRA Administration Page.
 * 
 * @version $Id$
 * @since 8.2
 */
public class JIRAAdministrationSectionPage extends AdministrationSectionPage
{
    private static final String USERNAME = "username";

    private static final String PASSWORD = "password";

    private static final String LABEL_SELECTOR = "label[for$='_%s']";

    /**
     * Construct an object representing the "JIRA" section.
     */
    public JIRAAdministrationSectionPage()
    {
        super("JIRA");
    }

    /**
     * Get the Nth jira {@code id} xproperty for the {@code JIRA.JIRAConfigClass} xclass.
     *
     * @param number the position of the {@code JIRA.JIRAConfigClass} xobject in the table
     * @return the xpoperty jira id
     */
    public String getId(int number)
    {
        return getIdWebElement(number).getText();
    }

    /**
     * Fill the jira id value on the displayed input.
     *
     * @param number the position of the {@code JIRA.JIRAConfigClass} xobject in the table
     * @param id the jira id to set
     */
    public void setId(int number, String id)
    {
        WebElement idElement = getIdWebElement(number);
        idElement.clear();
        idElement.sendKeys(id);
    }

    private WebElement getIdWebElement(int number)
    {
        return getDriver().findElement(By.id(String.format("JIRA.JIRAConfigClass_%d_id", number)));
    }

    /**
     * Get the Nth jira {@code url} xproperty for the {@code JIRA.JIRAConfigClass} xclass.
     *
     * @param number the position of the {@code JIRA.JIRAConfigClass} xobject in the table
     * @return the xpoperty jira url
     */
    public String getURL(int number)
    {
        return getURLWebElement(number).getText();
    }

    /**
     * Fill the jira url value on the displayed input.
     *
     * @param number the position of the {@code JIRA.JIRAConfigClass} xobject in the table
     * @param url the jira url to set
     */
    public void setURL(int number, String url)
    {
        WebElement idElement = getURLWebElement(number);
        idElement.clear();
        idElement.sendKeys(url);
    }

    private WebElement getURLWebElement(int number)
    {
        return getDriver().findElement(By.id(String.format("JIRA.JIRAConfigClass_%d_url", number)));
    }

    /**
     * @return the numbers of the {@code JIRA.JIRAConfigClass} xobjects displayed in the table, in display order
     * @since 11.2.0
     */
    public List<Integer> getInstanceNumbers()
    {
        Pattern idPattern = Pattern.compile("JIRA\\.JIRAConfigClass_(\\d+)_id");
        return getDriver().findElements(By.cssSelector("form#jiraConfig .jira-server-id input")).stream()
            .map(input -> idPattern.matcher(input.getAttribute("id")))
            .filter(Matcher::matches)
            .map(matcher -> Integer.valueOf(matcher.group(1)))
            .collect(Collectors.toList());
    }

    /**
     * Add a new JIRA instance, which reloads the section.
     *
     * @return the number of the {@code JIRA.JIRAConfigClass} xobject of the new instance
     * @since 11.2.0
     */
    public int addInstance()
    {
        List<Integer> numbers = getInstanceNumbers();
        getDriver().findElement(By.cssSelector("form#jiraConfig a.xobject-add-control")).click();
        getDriver().waitUntilCondition(driver -> getInstanceNumbers().size() > numbers.size());
        return getInstanceNumbers().stream().filter(number -> !numbers.contains(number)).findFirst().orElseThrow();
    }

    /**
     * Save the configuration and wait for the section to be reloaded.
     * <p>
     * The save button first saves the authentication configurations with JavaScript, then submits the form.
     *
     * @since 11.2.0
     */
    public void saveAndWaitForReload()
    {
        WebElement html = getDriver().findElement(By.tagName("html"));
        clickSave();
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(getDriver().getTimeout()));
        try {
            wait.until(ExpectedConditions.stalenessOf(html));
        } catch (TimeoutException ignored) {
            // The page may already be reloaded.
        }
        wait.until(driver -> "complete".equals(((JavascriptExecutor) driver).executeScript(
            "return document.readyState")));
    }

    /**
     * @return {@code true} if the "Instance type" column is displayed
     * @since 11.2.0
     */
    public boolean hasInstanceTypeColumn()
    {
        return !getDriver().findElements(By.cssSelector("form#jiraConfig select.jira-config-instance-type")).isEmpty();
    }

    /**
     * @param number the number of the {@code JIRA.JIRAConfigClass} xobject
     * @return the labels of the instance types that can be selected
     * @since 11.2.0
     */
    public List<String> getInstanceTypeLabels(int number)
    {
        return getInstanceTypeSelect(number).getOptions().stream().map(WebElement::getText)
            .collect(Collectors.toList());
    }

    /**
     * @param number the number of the {@code JIRA.JIRAConfigClass} xobject
     * @return the label of the selected instance type
     * @since 11.2.0
     */
    public String getSelectedInstanceTypeLabel(int number)
    {
        return getInstanceTypeSelect(number).getFirstSelectedOption().getText();
    }

    /**
     * Select an instance type, which reloads the authentication configuration of the instance.
     *
     * @param number the number of the {@code JIRA.JIRAConfigClass} xobject
     * @param instanceType the instance type to select (empty for the default one)
     * @since 11.2.0
     */
    public void setInstanceType(int number, String instanceType)
    {
        getInstanceTypeSelect(number).selectByValue(instanceType);
    }

    private Select getInstanceTypeSelect(int number)
    {
        return new Select(
            getDriver().findElement(By.id(String.format("JIRA.JIRAConfigClass_%d_instanceType", number))));
    }

    /**
     * Select an authentication type, which loads its configuration for the instance.
     *
     * @param number the number of the {@code JIRA.JIRAConfigClass} xobject
     * @param authenticationType the authentication type to select (e.g. {@code basicAuth})
     * @since 11.2.0
     */
    public void setAuthenticationType(int number, String authenticationType)
    {
        new Select(getDriver().findElement(By.id(String.format("JIRA.JIRAConfigClass_%d_authenticationType", number))))
            .selectByValue(authenticationType);
    }

    /**
     * Wait for the basic authentication configuration of an instance to be displayed.
     *
     * @param number the number of the {@code JIRA.JIRAConfigClass} xobject
     * @param usernameLabel the expected label of the username field, which depends on the instance type
     * @since 11.2.0
     */
    public void waitForBasicAuthConfiguration(int number, String usernameLabel)
    {
        getDriver().waitUntilCondition(driver -> {
            List<WebElement> labels =
                getAuthConfiguration(number).findElements(By.cssSelector(String.format(LABEL_SELECTOR, USERNAME)));
            return !labels.isEmpty() && usernameLabel.equals(getLabelText(labels.get(0)));
        });
    }

    /**
     * @param number the number of the {@code JIRA.JIRAConfigClass} xobject
     * @return the label of the password field of the basic authentication configuration
     * @since 11.2.0
     */
    public String getBasicAuthPasswordLabel(int number)
    {
        return getLabelText(
            getAuthConfiguration(number).findElement(By.cssSelector(String.format(LABEL_SELECTOR, PASSWORD))));
    }

    /**
     * @param number the number of the {@code JIRA.JIRAConfigClass} xobject
     * @return the username of the basic authentication configuration
     * @since 11.2.0
     */
    public String getBasicAuthUsername(int number)
    {
        return getBasicAuthField(number, USERNAME).getAttribute("value");
    }

    /**
     * @param number the number of the {@code JIRA.JIRAConfigClass} xobject
     * @param username the username to set in the basic authentication configuration
     * @param password the password to set in the basic authentication configuration
     * @since 11.2.0
     */
    public void setBasicAuthCredentials(int number, String username, String password)
    {
        WebElement usernameField = getBasicAuthField(number, USERNAME);
        usernameField.clear();
        usernameField.sendKeys(username);
        WebElement passwordField = getBasicAuthField(number, PASSWORD);
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    private WebElement getBasicAuthField(int number, String name)
    {
        return getAuthConfiguration(number).findElement(By.cssSelector(String.format("input[name$='_%s']", name)));
    }

    private WebElement getAuthConfiguration(int number)
    {
        return getIdWebElement(number).findElement(By.xpath("./ancestor::tr"))
            .findElement(By.cssSelector("td.jira-auth-configuration"));
    }

    private String getLabelText(WebElement label)
    {
        // The skin displays these labels in upper case: use the text of the page rather than the displayed one.
        return label.getAttribute("textContent").trim();
    }
}
