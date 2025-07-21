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
package org.xwiki.contrib.jira.script;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.SearchResult;

import io.atlassian.util.concurrent.Promise;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Functional tests for {@link JiraScriptService}.
 *
 * @version $Id$
 */
@ComponentTest
@Disabled("Test not generic and that connects to jira.xwiki.org, to be used manually for now. Needs to be fixed")
class JiraScriptServiceTest
{
    @InjectMockComponents
    private JiraScriptService jiraScriptService;

    @Test
    void getJiraRestClient() throws Exception
    {
        JIRAServer server = new JIRAServer("https://jira.xwiki.org", "id");
        JiraRestClient client = this.jiraScriptService.getJiraRestClient(server);
        Promise<SearchResult> promise = client.getSearchClient().searchJql("category = 10000 AND issuetype = Task  AND "
            + "component = \"Dependency Upgrades\"  and fixVersion in (16.2.0, 16.2.0-rc-1)");
        SearchResult result = promise.get();
        assertEquals(37, result.getTotal());
    }
}
