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
package org.xwiki.contrib.jira.config.internal;

import java.util.Collections;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.xwiki.contrib.jira.config.JIRAAuthenticator;
import org.xwiki.contrib.jira.config.JIRAConfiguration;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link DefaultJIRAConfiguration}.
 *
 * @version $Id$
 * @since 8.2
 */
public class DefaultJIRAConfigurationTest
{
    @Rule
    public MockitoComponentMockingRule<DefaultJIRAConfiguration> mocker =
        new MockitoComponentMockingRule<>(DefaultJIRAConfiguration.class);

    private class CustomJIRAConfiguration implements JIRAConfiguration
    {
        @Override
        public Map<String, JIRAServer> getJIRAServers()
        {
            return null;
        }
    }

    @Test
    public void getterAndSetters() throws Exception
    {
        DefaultJIRAConfiguration configuration = this.mocker.getComponentUnderTest();

        assertTrue(configuration.getJIRAServers().isEmpty());
        JIRAAuthenticator jiraAuthenticator = new BasicAuthJIRAAuthenticator("username", "password");
        JIRAServer jiraServer = new JIRAServer("url", "id", jiraAuthenticator);
        configuration.setJIRAServers(Collections.singletonMap("key", jiraServer));
        assertEquals(1, configuration.getJIRAServers().size());
        assertEquals("url", configuration.getJIRAServers().get("key").getURL());
        assertTrue(
            configuration.getJIRAServers().get("key").getJiraAuthenticator().orElseThrow().isAuthenticatingRequest());
        assertEquals(jiraAuthenticator, configuration.getJIRAServers().get("key").getJiraAuthenticator().orElseThrow());

        assertFalse(configuration.isAsync());
        configuration.setAsync(true);
        assertTrue(configuration.isAsync());

        // Verify backward compatibility and that async is false by default
        assertFalse(new CustomJIRAConfiguration().isAsync());
    }
}
