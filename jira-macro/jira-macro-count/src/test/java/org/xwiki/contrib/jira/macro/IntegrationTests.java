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
package org.xwiki.contrib.jira.macro;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.xwiki.contrib.jira.config.JIRAConfiguration;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.rendering.test.integration.RenderingTestSuite;
import org.xwiki.test.annotation.AllComponents;
import org.xwiki.test.mockito.MockitoComponentManager;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.mockito.Mockito.when;

/**
 * Run all tests found in {@code *.test} files located in the classpath. These {@code *.test} files must follow the
 * conventions described in {@link org.xwiki.rendering.test.integration.TestDataParser}.
 *
 * @version $Id$
 * @since 10.2
 */
@RunWith(RenderingTestSuite.class)
@AllComponents
public class IntegrationTests
{
    private static WireMockServer server;

    @BeforeClass
    public static void setUp()
    {
        // Simulate a fake JIRA instance using WireMock
        server = new WireMockServer(8889);
        server.start();

        // Default answer for issue count
        server.stubFor(get(urlMatching(
            "\\/rest\\/api\\/2\\/search\\?maxResults=0&jql=.*"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/xml")
                .withBody("{\"startAt\":0,\"maxResults\":0,\"total\":2,\"issues\":[]}")));
    }

    @RenderingTestSuite.Initialized
    public void initialize(MockitoComponentManager componentManager) throws Exception
    {
        // Register various JIRAServer configurations:
        // - one with authentication
        // - one without authentication
        JIRAConfiguration configuration = componentManager.registerMockComponent(JIRAConfiguration.class);
        Map<String, JIRAServer> servers = new HashMap<>();
        JIRAServer server1 = new JIRAServer("http://localhost:8889");
        servers.put("jira-noauth", server1);
        JIRAServer server2 = new JIRAServer("http://localhost:8889/auth", "username", "password");
        servers.put("jira-auth", server2);
        when(configuration.getJIRAServers()).thenReturn(servers);
    }

    @AfterClass
    public static void tearDown()
    {
        server.stop();
    }
}
