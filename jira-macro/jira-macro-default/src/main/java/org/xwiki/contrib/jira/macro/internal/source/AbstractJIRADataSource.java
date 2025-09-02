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
package org.xwiki.contrib.jira.macro.internal.source;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;

import org.jdom2.Document;
import org.jdom2.Element;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.JIRADataSource;
import org.xwiki.contrib.jira.macro.JIRAField;
import org.xwiki.contrib.jira.macro.JIRAMacroParameters;
import org.xwiki.contrib.jira.macro.internal.HTTPJIRAFetcher;
import org.xwiki.contrib.jira.macro.internal.JIRABadRequestException;
import org.xwiki.contrib.jira.macro.internal.JIRAConnectionException;
import org.xwiki.contrib.jira.macro.internal.JIRAURLHelper;
import org.xwiki.rendering.macro.MacroExecutionException;

/**
 * Common implementation for JIRA Data Source that knowns how to execute a JQL query on a JIRA instance and retrieve the
 * list of matching JIRA issues.
 *
 * @version $Id$
 * @since 4.2M1
 */
public abstract class AbstractJIRADataSource implements JIRADataSource
{
    @Inject
    private HTTPJIRAFetcher jiraFetcher;

    @Inject
    private JIRAServerResolver jiraServerResolver;

    @Inject
    private JIRAURLHelper urlHelper;

    /**
     * @param document the XML document from which to extract JIRA issues
     * @return the list of XML Elements for each JIRA issue, indexed in a Map with the issue id as the key
     */
    protected Map<String, Element> buildIssues(Document document)
    {
        Map<String, Element> issues = new LinkedHashMap<>();
        Element channel = document.getRootElement().getChild("channel");
        if (channel != null) {
            for (Element item : channel.getChildren("item")) {
                issues.put(item.getChildText(JIRAField.KEY.getId()), item);
            }
        }
        return issues;
    }

    /**
     * @param jiraServer the JIRA Server definition to use
     * @param jqlQuery the JQL query to execute
     * @param maxCount the max number of issues to get
     * @return the XML document containing the matching JIRA issues
     * @throws MacroExecutionException if the JIRA issues cannot be retrieved
     */
    public Document getXMLDocument(JIRAServer jiraServer, String jqlQuery, int maxCount)
        throws MacroExecutionException, JIRABadRequestException
    {
        Document document;

        try {
            String urlString = this.urlHelper.getSearchURL(jiraServer, jqlQuery, maxCount);
            document = this.jiraFetcher.fetch(urlString, jiraServer);
        } catch (JIRABadRequestException e) {
            throw e;
        } catch (JIRAConnectionException e) {
            throw new MacroExecutionException(String.format("Failed to retrieve JIRA data from [%s] for JQL [%s]",
                jiraServer.getURL(), jqlQuery), e);
        }
        return document;
    }

    /**
     * @param parameters the macro's parameters
     * @return the url to the JIRA instance (e.g. {@code http://jira.xwiki.org"})
     * @throws MacroExecutionException if no URL has been specified (either in the macro parameter or configuration)
     */
    protected JIRAServer getJIRAServer(JIRAMacroParameters parameters) throws MacroExecutionException
    {
        return this.jiraServerResolver.resolve(parameters);
    }
}
