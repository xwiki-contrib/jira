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
package org.xwiki.contrib.jira.macro.internal;

import org.junit.jupiter.api.Test;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link JIRAURLHelper}.
 *
 * @version $Id$
 * @since 10.0
 */
@ComponentTest
class JIRAURLHelperTest
{
    @InjectMockComponents
    private JIRAURLHelper jiraURLHelper;

    @Test
    void computeFullURL()
    {
        // No credentials passed
        JIRAServer jiraServer = new JIRAServer("http://localhost/jira");
        assertEquals("http://localhost/jira/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=query",
            this.jiraURLHelper.getSearchURL(jiraServer, "query", -1));

        // Just username defined but no password (or empty password)
        jiraServer = new JIRAServer("http://localhost/jira", "username", "");
        assertEquals("http://localhost/jira/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=query",
            this.jiraURLHelper.getSearchURL(jiraServer, "query", -1));

        // With Max Count and no credentials
        jiraServer = new JIRAServer("http://localhost/jira");
        assertEquals("http://localhost/jira/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?"
                + "jqlQuery=query&tempMax=5",
            this.jiraURLHelper.getSearchURL(jiraServer, "query", 5));

        // Rest search - No credentials passed
        jiraServer = new JIRAServer("http://localhost/jira");
        assertEquals("http://localhost/jira/rest/api/2/search?maxResults=0&jql=query",
            this.jiraURLHelper.getRestSearchURL(jiraServer, "query"));

        // Rest search - Just username defined but no password (or empty password)
        jiraServer = new JIRAServer("http://localhost/jira", "username", "");
        assertEquals("http://localhost/jira/rest/api/2/search?maxResults=0&jql=query",
            this.jiraURLHelper.getRestSearchURL(jiraServer, "query"));
    }
}