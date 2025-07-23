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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.config.internal.BasicAuthJIRAAuthenticator;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link HTTPJIRAFetcher}.
 */
@ComponentTest
class HTTPJIRAFetcherTest
{
    @InjectMockComponents
    private HTTPJIRAFetcher jiraFetcher;

    private WireMockServer wireMockServer;

    @BeforeEach
    void proxyToWireMock()
    {
        this.wireMockServer = new WireMockServer(8889);
        this.wireMockServer.start();
    }

    @AfterEach
    void noMoreWireMock()
    {
        this.wireMockServer.stop();
        this.wireMockServer = null;
    }

    @Test
    void xmlError()
    {
        // Setup Wiremock to simulate a JIRA instance
        this.wireMockServer.stubFor(get(urlMatching(
            "\\/sr\\/jira.issueviews:searchrequest-xml\\/temp\\/SearchRequest\\.xml\\?jqlQuery=.*"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/xml")
                .withBodyFile("input.xml")));
        JIRAServer jiraServer =
            new JIRAServer("http://localhost:8889", "id", new BasicAuthJIRAAuthenticator("user", "pass"));
        Throwable exception = assertThrows(JIRAConnectionException.class, () -> {
            this.jiraFetcher.fetch(
                "http://localhost:8889/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=whatever",
                jiraServer);
        });
        assertEquals("Failed to parse JIRA XML content [<!--\n"
            + " * See the NOTICE file distributed with this work for additional\n"
            + " * information regarding copyright ownership.\n"
            + " *\n"
            + " * This is free software; you can redistribute it and/or modify it\n"
            + " * under the terms of the GNU Lesser General Public License as\n"
            + " * published by the Free Software Foundation; either version 2.1 of\n"
            + " * the License, or (at your option) any later version.\n"
            + " *\n"
            + " * This software is distributed in the hope that it will be useful,\n"
            + " * but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
            + " * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU\n"
            + " * Lesser General Public License for more details.\n"
            + " *\n"
            + " * You should have received a copy of the GNU Lesser General Public\n"
            + " * License along with this software; if not, write to the Free\n"
            + " * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA\n"
            + " * 02110-1301 USA, or see the FSF site: http://www.fsf.org.\n"
            + "-->\n"
            + "<rss version=\"0.92\">\n"
            + "  <channel>\n"
            + "    <title>XWiki.org JIRA &invalid;</title>\n"
            + "  </channel>\n"
            + "</rss>]", exception.getMessage());
        assertEquals("Error on line 22: The entity \"invalid\" was referenced, but not declared.",
            exception.getCause().getMessage());
    }
}
