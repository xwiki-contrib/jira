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

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.xwiki.contrib.jira.macro.JIRAConfiguration;
import org.xwiki.contrib.jira.macro.JIRAMacroParameters;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.contrib.jira.macro.JIRADataSource;
import org.xwiki.contrib.jira.macro.JIRAFields;

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
    protected JIRAConfiguration configuration;

    /**
     * URL Prefix to use to build the full JQL URL (doesn't contain the JQL query itself which needs to be appended).
     */
    private static final String JQL_URL_PREFIX =
        "/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=";

    /**
     * @param document the XML document from which to extract JIRA issues
     * @return the list of XML Elements for each JIRA issue, indexed in a Map with the issue id as the key
     */
    protected Map<String, Element> buildIssues(Document document)
    {
        Map<String, Element> issues = new LinkedHashMap<String, Element>();
        for (Element item : document.getRootElement().getChild("channel").getChildren("item")) {
            issues.put(item.getChildText(JIRAFields.KEY), item);
        }
        return issues;
    }

    /**
     * @param jiraURL the JIRA URL prefix (eg "http://jira.xwiki.org")
     * @param jqlQuery the JQL query to execute
     * @return the XML document containing the matching JIRA issues
     * @throws MacroExecutionException if the JIRA issues cannot be retrieved
     */
    public Document getXMLDocument(String jiraURL, String jqlQuery) throws MacroExecutionException
    {
        Document document;

        try {
            // Note: we encode using UTF8 since it's the W3C recommendation.
            // See http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars
            document = createSAXBuilder().build(new URL(computeFullURL(jiraURL, jqlQuery)));
        } catch (Exception e) {
            throw new MacroExecutionException(String.format("Failed to retrieve JIRA data from [%s] for JQL [%s]",
                jiraURL, jqlQuery), e);
        }
        return document;
    }

    protected String computeFullURL(String jiraURL, String jqlQuery)
    {
        String additionalQueryString;
        if (!StringUtils.isBlank(this.configuration.getUsername())
            && !StringUtils.isBlank(this.configuration.getPassword()))
        {
            additionalQueryString = String.format("&os_username=%s&os_password=%s&os_authType=basic",
                encode(this.configuration.getUsername()), encode(this.configuration.getPassword()));
        } else {
            additionalQueryString = "";
        }
        return String.format("%s%s%s%s", jiraURL, JQL_URL_PREFIX, encode(jqlQuery), additionalQueryString);
    }

    /**
     * @return the SAXBuilder instance to use to retrieve the data
     */
    protected SAXBuilder createSAXBuilder()
    {
        // Note: SAXBuilder is not thread-safe which is why we're instantiating a new one every time.
        return new SAXBuilder();
    }

    /**
     * @param parameters the macro's parameters
     * @return the url to the JIRA instance (eg "http://jira.xwiki.org")
     * @throws MacroExecutionException if no URL has been specified (either in the macro parameter or configuration)
     */
    protected String computeURLPrefix(JIRAMacroParameters parameters) throws MacroExecutionException
    {
        String url;

        // Check if the user has provided an explicit url in the macro
        url = parameters.getURL();
        if (StringUtils.isBlank(url)) {
            // If not, then check if the user has provided a url id in the macro
            String id = parameters.getId();
            if (StringUtils.isBlank(id)) {
                // If not, then check if there's a default id specified in the configuration
                id = this.configuration.getDefaultURLId();
                if (StringUtils.isBlank(id)) {
                    throw new MacroExecutionException("No JIRA URL found. You must specify a URL as a macro "
                        + "parameter or an URL id in as a macro parameter, or define a default URL id in the macro's "
                        + "configuration");
                }
            }
            url = this.configuration.getURLMappings().get(id);
            if (url == null) {
                throw new MacroExecutionException(String.format("The JIRA URL id [%s] is not defined in the macro's "
                    + "configuration. Please fix the id or add a new URL mapping in the configuration", id));
            }
        }

        return url;
    }

    private String encode(String content)
    {
        try {
            return URLEncoder.encode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Missing UTF-8 encoding", e);
        }
    }
}
