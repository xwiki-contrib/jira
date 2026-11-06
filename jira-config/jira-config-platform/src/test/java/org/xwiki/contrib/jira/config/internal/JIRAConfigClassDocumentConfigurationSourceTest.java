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

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xwiki.cache.Cache;
import org.xwiki.cache.CacheManager;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.LocalDocumentReference;
import org.xwiki.test.annotation.BeforeComponent;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;
import org.xwiki.wiki.descriptor.WikiDescriptorManager;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.test.MockitoOldcore;
import com.xpn.xwiki.test.junit5.mockito.InjectMockitoOldcore;
import com.xpn.xwiki.test.junit5.mockito.OldcoreTest;
import com.xpn.xwiki.test.reference.ReferenceComponentList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link JIRAConfigClassDocumentConfigurationSource}.
 *
 * @version $Id$
 */
@OldcoreTest
@ReferenceComponentList
@ComponentList({
    BasicAuthJIRAAuthenticatorFactory.class
})
class JIRAConfigClassDocumentConfigurationSourceTest
{
    private static final LocalDocumentReference CONFIG_CLASS = new LocalDocumentReference("JIRA", "JIRAConfigClass");

    @InjectMockitoOldcore
    private MockitoOldcore oldcore;

    @InjectMockComponents
    private JIRAConfigClassDocumentConfigurationSource configurationSource;

    @MockComponent
    private CacheManager cacheManager;

    @MockComponent
    private WikiDescriptorManager wikiDescriptorManager;

    @BeforeComponent
    void beforeComponent(MockitoOldcore oldcore) throws Exception
    {
        when(this.cacheManager.createNewCache(any())).thenReturn(mock(Cache.class));

        // The JIRA authenticator factories are looked up in the context component manager.
        oldcore.getMocker().registerComponent(ComponentManager.class, "context", oldcore.getMocker());
    }

    @BeforeEach
    void setUp()
    {
        when(this.wikiDescriptorManager.getCurrentWikiId()).thenReturn("xwiki");
    }

    @Test
    void getServersWithInstanceType() throws Exception
    {
        addServer("cloudjira", "https://example.atlassian.net", BasicAuthJIRAAuthenticatorFactory.HINT, "cloud");
        addServer("dcjira", "https://jira.example.org", "", "");

        Map<String, JIRAServer> servers = this.configurationSource.getProperty("serverMappings");

        JIRAServer cloudServer = servers.get("cloudjira");
        assertEquals("https://example.atlassian.net", cloudServer.getURL());
        assertEquals("cloud", cloudServer.getInstanceType());
        assertNotNull(cloudServer.getJiraAuthenticator().orElse(null));

        // An empty value means the default instance type, so that the existing configurations keep working.
        JIRAServer defaultServer = servers.get("dcjira");
        assertEquals(JIRAServer.DEFAULT_INSTANCE_TYPE, defaultServer.getInstanceType());
        assertNull(defaultServer.getJiraAuthenticator().orElse(null));
    }

    private void addServer(String id, String url, String authenticationType, String instanceType) throws Exception
    {
        XWikiContext xcontext = this.oldcore.getXWikiContext();
        XWikiDocument document =
            xcontext.getWiki().getDocument(new DocumentReference("xwiki", "JIRA", "JIRAConfig"), xcontext);
        BaseObject server = document.newXObject(CONFIG_CLASS, xcontext);
        server.setStringValue("id", id);
        server.setStringValue("url", url);
        server.setStringValue("authenticationType", authenticationType);
        server.setStringValue("instanceType", instanceType);
        xcontext.getWiki().saveDocument(document, xcontext);

        if (BasicAuthJIRAAuthenticatorFactory.HINT.equals(authenticationType)) {
            XWikiDocument credentialsDocument =
                xcontext.getWiki().getDocument(BasicAuthJIRAAuthenticatorFactory.BASIC_AUTH_CONFIG_REFERENCE, xcontext);
            BaseObject credentials = credentialsDocument
                .newXObject(BasicAuthJIRAAuthenticatorFactory.BASIC_AUTH_DATA_CLASS_REFERENCE, xcontext);
            credentials.setStringValue("id", id);
            credentials.setStringValue("username", "user");
            credentials.setStringValue("password", "secret");
            xcontext.getWiki().saveDocument(credentialsDocument, xcontext);
        }
    }
}
