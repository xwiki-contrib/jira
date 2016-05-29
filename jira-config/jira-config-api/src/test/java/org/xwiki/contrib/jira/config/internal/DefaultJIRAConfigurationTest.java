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

import org.junit.Rule;
import org.junit.Test;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import static org.junit.Assert.*;

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

    @Test
    public void getterAndSetters() throws Exception
    {
        DefaultJIRAConfiguration configuration = this.mocker.getComponentUnderTest();

        assertTrue(configuration.getJIRAServers().isEmpty());

        configuration.setJIRAServers(Collections.singletonMap("key", new JIRAServer("url", "username", "password")));
        assertEquals(1, configuration.getJIRAServers().size());
        assertEquals("url", configuration.getJIRAServers().get("key").getURL());
        assertEquals("username", configuration.getJIRAServers().get("key").getUsername());
        assertEquals("password", configuration.getJIRAServers().get("key").getPassword());
    }
}
