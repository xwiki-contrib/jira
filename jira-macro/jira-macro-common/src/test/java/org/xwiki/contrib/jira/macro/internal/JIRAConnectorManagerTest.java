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

import javax.inject.Named;
import javax.inject.Provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.JIRAConnector;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectComponentManager;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;
import org.xwiki.test.mockito.MockitoComponentManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link JIRAConnectorManager}.
 */
@ComponentTest
class JIRAConnectorManagerTest
{
    @InjectMockComponents
    private JIRAConnectorManager connectorManager;

    @MockComponent
    @Named("context")
    private Provider<ComponentManager> componentManagerProvider;

    @InjectComponentManager
    private MockitoComponentManager componentManager;

    @BeforeEach
    void setUp()
    {
        when(this.componentManagerProvider.get()).thenReturn(this.componentManager);
    }

    @Test
    void getConnectorForDefaultInstanceType() throws Exception
    {
        JIRAConnector defaultConnector = this.componentManager.registerMockComponent(JIRAConnector.class, "default");
        this.componentManager.registerMockComponent(JIRAConnector.class, "cloud");

        assertSame(defaultConnector,
            this.connectorManager.getConnector(new JIRAServer("http://localhost/jira", "id")));
        // An empty instance type is the default one.
        assertSame(defaultConnector,
            this.connectorManager.getConnector(new JIRAServer("http://localhost/jira", "id", null, "")));
    }

    @Test
    void getConnectorForContributedInstanceType() throws Exception
    {
        this.componentManager.registerMockComponent(JIRAConnector.class, "default");
        JIRAConnector cloudConnector = this.componentManager.registerMockComponent(JIRAConnector.class, "cloud");

        assertSame(cloudConnector,
            this.connectorManager.getConnector(new JIRAServer("https://site.example.org", "id", null, "cloud")));
    }

    @Test
    void getConnectorForUnknownInstanceType() throws Exception
    {
        // The default connector must not be used for an instance type it doesn't handle.
        this.componentManager.registerMockComponent(JIRAConnector.class, "default");

        MacroExecutionException exception = assertThrows(MacroExecutionException.class,
            () -> this.connectorManager.getConnector(new JIRAServer("https://site.example.org", "id", null,
                "cloud")));

        assertEquals("The JIRA instance type [cloud] of the JIRA server [https://site.example.org] is not supported. "
            + "The extension providing it may be missing or not installed on this wiki.", exception.getMessage());
    }

    @Test
    void getConnectorWhenLookupFails() throws Exception
    {
        ComponentManager failingComponentManager = mock(ComponentManager.class);
        when(this.componentManagerProvider.get()).thenReturn(failingComponentManager);
        when(failingComponentManager.hasComponent(JIRAConnector.class, "default")).thenReturn(true);
        ComponentLookupException cause = new ComponentLookupException("error");
        when(failingComponentManager.getInstance(JIRAConnector.class, "default")).thenThrow(cause);

        MacroExecutionException exception = assertThrows(MacroExecutionException.class,
            () -> this.connectorManager.getConnector(new JIRAServer("http://localhost/jira", "id")));

        assertEquals("Failed to initialize the support of the JIRA instance type [default] of the JIRA server "
            + "[http://localhost/jira].", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
