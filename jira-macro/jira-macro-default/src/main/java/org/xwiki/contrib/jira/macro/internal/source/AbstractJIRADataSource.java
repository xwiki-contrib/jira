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
import org.xwiki.contrib.jira.config.JIRAConfiguration;
import org.xwiki.contrib.jira.macro.JIRAMacroParameters;
import org.xwiki.contrib.jira.config.JIRAServer;
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
     * @param jiraServer the JIRA Server definition to use
     * @param jqlQuery the JQL query to execute
     * @return the XML document containing the matching JIRA issues
     * @throws MacroExecutionException if the JIRA issues cannot be retrieved
     */
    public Document getXMLDocument(JIRAServer jiraServer, String jqlQuery) throws MacroExecutionException
    {
        Document document;

        try {
            // Note: we encode using UTF8 since it's the W3C recommendation.
            // See http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars
            document = createSAXBuilder().build(new URL(computeFullURL(jiraServer, jqlQuery)));
        } catch (Exception e) {
            throw new MacroExecutionException(String.format("Failed to retrieve JIRA data from [%s] for JQL [%s]",
                jiraServer.getURL(), jqlQuery), e);
        }
        return document;
    }

    protected String computeFullURL(JIRAServer jiraServer, String jqlQuery)
    {
        String additionalQueryString;
        if (!StringUtils.isBlank(jiraServer.getUsername())
            && !StringUtils.isBlank(jiraServer.getPassword()))
        {
            additionalQueryString = String.format("&os_username=%s&os_password=%s&os_authType=basic",
                encode(jiraServer.getUsername()), encode(jiraServer.getPassword()));
        } else {
            additionalQueryString = "";
        }
        return String.format("%s%s%s%s", jiraServer.getURL(), JQL_URL_PREFIX, encode(jqlQuery), additionalQueryString);
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
    protected JIRAServer getJIRAServer(JIRAMacroParameters parameters) throws MacroExecutionException
    {
        JIRAServer jiraServer;

        // Check if the user has provided an explicit url in the macro. If so, try to find a matching URL in the
        // configured JIRA Server list in order to find if there are any credentials. If not found, return a public
        // JIRA instance.
        String url = parameters.getURL();
        if (StringUtils.isBlank(url)) {
            // If not, then check if the user has provided a server id in the macro
            String id = parameters.getId();
            if (StringUtils.isBlank(id)) {
                // Note: we could have decided that if there's a single JIRA server definition we would use that id by
                // default. However doing so would break all Macro calls not specifying an id as soon as a second jira
                // server definiton is added later on...
                throw new MacroExecutionException("No JIRA Server found. You must specify a JIRA server, using the "
                    + "\"url\" macro parameter or using the \"id\" macro parameter to reference a server defined in "
                    + "the JIRA Macro configuration.");
            }
            jiraServer = this.configuration.getJIRAServers().get(id);
            if (jiraServer == null) {
                throw new MacroExecutionException(String.format("The JIRA Server id [%s] is not defined in the macro's "
                    + "configuration. Please fix the id or add a new server in the JIRA Macro configuration.", id));
            }
        } else {
            jiraServer = null;
            for (JIRAServer server : this.configuration.getJIRAServers().values()) {
                if (server.getURL().equals(url)) {
                    jiraServer = server;
                    break;
                }
            }
            if (jiraServer == null) {
                jiraServer = new JIRAServer(url);
            }
        }

        return jiraServer;
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
