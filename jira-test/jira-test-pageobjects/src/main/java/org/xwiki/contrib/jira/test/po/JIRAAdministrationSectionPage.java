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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.xwiki.administration.test.po.AdministrationSectionPage;

/**
 * Represents the actions possible on the JIRA Administration Page: the JIRA instances are listed in a Live Data and
 * added or edited in a modal.
 *
 * @version $Id$
 * @since 8.2
 */
public class JIRAAdministrationSectionPage extends AdministrationSectionPage
{
    private static final String LIVE_DATA = "#jiraInstances";

    private static final String EDIT_LINK = LIVE_DATA + " a.jira-instance-edit:not(.action)";

    private static final String MODAL = "jiraInstanceModal";

    private static final String DELETE_MODAL = "jiraInstanceDeleteModal";

    private static final String VALUE = "value";

    private static final String DATA_ID = "data-id";

    private static final String HASH = "#";

    private static final String MODAL_SELECTOR = HASH + MODAL;

    private static final String DELETE_MODAL_SELECTOR = HASH + DELETE_MODAL;

    private static final String CLASS = "class";

    private static final String ROW = "./ancestor::tr";

    private static final String TEXT_CONTENT = "textContent";

    private static final String ID = "id";

    private static final String URL = "url";

    private static final String DEPLOYMENT_TYPE = "deploymentType";

    private static final String AUTHENTICATION_TYPE = "authenticationType";

    private static final String USERNAME = "jiraBasicAuthUsername";

    private static final String PASSWORD = "jiraBasicAuthPassword";

    private static final String LABEL_FOR = "label[for='%s']";

    /**
     * Construct an object representing the "JIRA" section.
     */
    public JIRAAdministrationSectionPage()
    {
        super("JIRA");
        waitUntilInstancesLoaded();
    }

    /**
     * Wait until the Live Data listing the JIRA instances is loaded.
     *
     * @since 11.2.0
     */
    public void waitUntilInstancesLoaded()
    {
        getDriver().waitUntilCondition(driver -> {
            List<WebElement> liveData = driver.findElements(By.cssSelector(LIVE_DATA));
            return !liveData.isEmpty() && !liveData.get(0).getAttribute(CLASS).contains("loading")
                && !liveData.get(0).findElements(By.cssSelector("table")).isEmpty();
        });
    }

    /**
     * @return the IDs of the JIRA instances listed in the Live Data, in display order
     * @since 11.2.0
     */
    public List<String> getInstanceIds()
    {
        return getDriver().findElements(By.cssSelector(EDIT_LINK)).stream()
            .map(link -> link.getAttribute(DATA_ID)).collect(Collectors.toList());
    }

    /**
     * Wait until the Live Data lists a JIRA instance with the given ID.
     *
     * @param id the ID of the instance
     * @since 11.2.0
     */
    public void waitUntilInstanceIsListed(String id)
    {
        getDriver().waitUntilCondition(driver -> {
            try {
                return getInstanceIds().contains(id);
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });
    }

    /**
     * Wait until the Live Data doesn't list a JIRA instance with the given ID.
     *
     * @param id the ID of the instance
     * @since 11.2.0
     */
    public void waitUntilInstanceIsNotListed(String id)
    {
        getDriver().waitUntilCondition(driver -> {
            try {
                return !getInstanceIds().contains(id);
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });
    }

    /**
     * @return {@code true} if the Live Data has a "Deployment type" column
     * @since 11.2.0
     */
    public boolean hasDeploymentTypeColumn()
    {
        return getColumnNames().contains("Deployment type");
    }

    /**
     * @param id the ID of the instance
     * @param columnName the name of the column, e.g. "JIRA URL"
     * @return the text displayed in the given column of the given instance
     * @since 11.2.0
     */
    public String getInstanceValue(String id, String columnName)
    {
        int index = getColumnNames().indexOf(columnName);
        WebElement row = getEditLink(id).findElement(By.xpath(ROW));
        return row.findElements(By.xpath("./td")).get(index).getAttribute(TEXT_CONTENT).trim();
    }

    private List<String> getColumnNames()
    {
        // The first header row holds the column names (the next ones hold e.g. the filters).
        return getDriver().findElement(By.cssSelector(LIVE_DATA + " table thead tr"))
            .findElements(By.xpath("./th")).stream().map(th -> th.getAttribute(TEXT_CONTENT).trim())
            .collect(Collectors.toList());
    }

    private WebElement getEditLink(String id)
    {
        Optional<WebElement> link = getDriver().findElements(By.cssSelector(EDIT_LINK)).stream()
            .filter(element -> id.equals(element.getAttribute(DATA_ID))).findFirst();
        return link.orElseThrow(() -> new IllegalArgumentException("No JIRA instance with ID " + id));
    }

    /**
     * Open the form to add a JIRA instance.
     *
     * @since 11.2.0
     */
    public void clickAddInstance()
    {
        getDriver().findElement(By.cssSelector("#jiraConfig .jira-instance-add")).click();
        waitUntilModalIsShown(MODAL);
    }

    /**
     * Open the form to edit a JIRA instance by clicking its ID.
     *
     * @param id the ID of the instance
     * @since 11.2.0
     */
    public void clickInstanceId(String id)
    {
        getEditLink(id).click();
        waitUntilModalIsShown(MODAL);
    }

    /**
     * Open the form to edit a JIRA instance with its edit action.
     *
     * @param id the ID of the instance
     * @since 11.2.0
     */
    public void clickEditInstance(String id)
    {
        getAction(id, "jira-instance-edit").click();
        waitUntilModalIsShown(MODAL);
    }

    /**
     * Delete a JIRA instance with its delete action, and confirm.
     *
     * @param id the ID of the instance
     * @since 11.2.0
     */
    public void deleteInstance(String id)
    {
        getAction(id, "jira-instance-delete").click();
        waitUntilModalIsShown(DELETE_MODAL);
        getDriver().findElement(By.cssSelector(DELETE_MODAL_SELECTOR + " .jira-instance-delete-confirm")).click();
        waitUntilModalIsHidden(DELETE_MODAL);
        waitUntilInstanceIsNotListed(id);
    }

    private WebElement getAction(String id, String cssClass)
    {
        return getEditLink(id).findElement(By.xpath(ROW))
            .findElement(By.cssSelector(String.format("a.action.%s", cssClass)));
    }

    /**
     * @return the title of the instance form
     * @since 11.2.0
     */
    public String getInstanceFormTitle()
    {
        return getModal().findElement(By.className("modal-title")).getText();
    }

    /**
     * @return the ID in the instance form
     * @since 11.2.0
     */
    public String getId()
    {
        return getField(ID).getAttribute(VALUE);
    }

    /**
     * @param id the ID to set in the instance form
     * @since 11.2.0
     */
    public void setId(String id)
    {
        setText(getField(ID), id);
    }

    /**
     * @return the JIRA URL in the instance form
     * @since 11.2.0
     */
    public String getURL()
    {
        return getField(URL).getAttribute(VALUE);
    }

    /**
     * @param url the JIRA URL to set in the instance form
     * @since 11.2.0
     */
    public void setURL(String url)
    {
        setText(getField(URL), url);
    }

    /**
     * @return {@code true} if the instance form has a deployment type field
     * @since 11.2.0
     */
    public boolean hasDeploymentTypeField()
    {
        return !getModal().findElements(By.cssSelector("select[name='deploymentType']")).isEmpty();
    }

    /**
     * @return the labels of the deployment types that can be selected in the instance form
     * @since 11.2.0
     */
    public List<String> getDeploymentTypeLabels()
    {
        return getSelect(DEPLOYMENT_TYPE).getOptions().stream().map(WebElement::getText)
            .collect(Collectors.toList());
    }

    /**
     * @return the label of the deployment type selected in the instance form
     * @since 11.2.0
     */
    public String getSelectedDeploymentTypeLabel()
    {
        return getSelect(DEPLOYMENT_TYPE).getFirstSelectedOption().getText();
    }

    /**
     * Select a deployment type in the instance form, which reloads the authentication settings.
     *
     * @param deploymentType the deployment type to select (empty for Data Center)
     * @since 11.2.0
     */
    public void setDeploymentType(String deploymentType)
    {
        getSelect(DEPLOYMENT_TYPE).selectByValue(deploymentType);
    }

    /**
     * Select an authentication type in the instance form, which loads its settings.
     *
     * @param authenticationType the authentication type to select (e.g. {@code basicAuth})
     * @since 11.2.0
     */
    public void setAuthenticationType(String authenticationType)
    {
        getSelect(AUTHENTICATION_TYPE).selectByValue(authenticationType);
    }

    /**
     * @return the label of the authentication type selected in the instance form
     * @since 11.2.0
     */
    public String getSelectedAuthenticationTypeLabel()
    {
        return getSelect(AUTHENTICATION_TYPE).getFirstSelectedOption().getText();
    }

    /**
     * Wait for the basic authentication settings to be displayed in the instance form.
     *
     * @param usernameLabel the expected label of the username field, which depends on the deployment type
     * @since 11.2.0
     */
    public void waitForBasicAuthConfiguration(String usernameLabel)
    {
        getDriver().waitUntilCondition(driver -> {
            List<WebElement> labels =
                getAuthConfiguration().findElements(By.cssSelector(String.format(LABEL_FOR, USERNAME)));
            return !labels.isEmpty() && usernameLabel.equals(getLabelText(labels.get(0)));
        });
    }

    /**
     * Wait until the instance form doesn't display any authentication settings.
     *
     * @since 11.2.0
     */
    public void waitForNoAuthConfiguration()
    {
        getDriver().waitUntilCondition(
            driver -> getAuthConfiguration().findElements(By.cssSelector(":scope input")).isEmpty());
    }

    /**
     * @return the label of the password field of the basic authentication settings
     * @since 11.2.0
     */
    public String getBasicAuthPasswordLabel()
    {
        return getLabelText(getAuthConfiguration().findElement(By.cssSelector(String.format(LABEL_FOR, PASSWORD))));
    }

    /**
     * @return {@code true} if the basic authentication settings say that a password is stored, which is kept when the
     *     password field is left empty
     * @since 11.2.0
     */
    public boolean hasStoredBasicAuthPassword()
    {
        return !getAuthConfiguration().findElements(By.id("jiraBasicAuthPasswordHint")).isEmpty();
    }

    /**
     * @return the username of the basic authentication settings
     * @since 11.2.0
     */
    public String getBasicAuthUsername()
    {
        return getAuthConfiguration().findElement(By.id(USERNAME)).getAttribute(VALUE);
    }

    /**
     * @param username the username to set in the basic authentication settings
     * @param password the password to set in the basic authentication settings
     * @since 11.2.0
     */
    public void setBasicAuthCredentials(String username, String password)
    {
        setText(getAuthConfiguration().findElement(By.id(USERNAME)), username);
        setText(getAuthConfiguration().findElement(By.id(PASSWORD)), password);
    }

    /**
     * Save the instance form and wait until the modal is closed and the instance is listed.
     *
     * @since 11.2.0
     */
    public void saveInstance()
    {
        String id = getId().trim();
        clickSaveInstance();
        waitUntilModalIsHidden(MODAL);
        waitUntilInstanceIsListed(id);
    }

    /**
     * Save the instance form, expecting an error.
     *
     * @return the error message displayed in the instance form
     * @since 11.2.0
     */
    public String saveInstanceWithError()
    {
        clickSaveInstance();
        By error = By.cssSelector(MODAL_SELECTOR + " .jira-instance-error");
        getDriver().waitUntilCondition(driver -> driver.findElement(error).isDisplayed());
        return getDriver().findElement(error).getText();
    }

    private void clickSaveInstance()
    {
        // The form can't be saved until the authentication settings are loaded.
        By saveButton = By.cssSelector(MODAL_SELECTOR + " .modal-footer button[type='submit']");
        getDriver().waitUntilCondition(driver -> driver.findElement(saveButton).isEnabled());
        getDriver().findElement(saveButton).click();
    }

    /**
     * Close the instance form without saving.
     *
     * @since 11.2.0
     */
    public void cancelInstance()
    {
        getModal().findElement(By.cssSelector(".modal-footer button[data-dismiss='modal']")).click();
        waitUntilModalIsHidden(MODAL);
    }

    private WebElement getModal()
    {
        return getDriver().findElement(By.id(MODAL));
    }

    private WebElement getField(String name)
    {
        return getModal().findElement(By.cssSelector(String.format("[name='%s']", name)));
    }

    private Select getSelect(String name)
    {
        return new Select(getField(name));
    }

    private WebElement getAuthConfiguration()
    {
        return getModal().findElement(By.className("jira-auth-configuration"));
    }

    private void setText(WebElement element, String text)
    {
        element.clear();
        element.sendKeys(text);
    }

    private void waitUntilModalIsShown(String modalId)
    {
        // Wait for the end of the animation, when the modal gets the "in" class and is displayed.
        getDriver().waitUntilCondition(driver -> {
            WebElement modal = driver.findElement(By.id(modalId));
            return modal.isDisplayed() && modal.getAttribute(CLASS).contains("in")
                && "1".equals(modal.getCssValue("opacity"));
        });
    }

    private void waitUntilModalIsHidden(String modalId)
    {
        getDriver().waitUntilCondition(driver -> !driver.findElement(By.id(modalId)).isDisplayed()
            && driver.findElements(By.className("modal-backdrop")).isEmpty());
    }

    private String getLabelText(WebElement label)
    {
        // The skin displays these labels in upper case: use the text of the page rather than the displayed one.
        return label.getAttribute(TEXT_CONTENT).trim();
    }
}
