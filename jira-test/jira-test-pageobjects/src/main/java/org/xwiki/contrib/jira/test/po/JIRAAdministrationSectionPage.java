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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.xwiki.administration.test.po.AdministrationSectionPage;

/**
 * Represents the actions possible on the JIRA Administration Page.
 * 
 * @version $Id$
 * @since 8.2
 */
public class JIRAAdministrationSectionPage extends AdministrationSectionPage
{
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
}
