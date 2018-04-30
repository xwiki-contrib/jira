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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;

import org.apache.commons.codec.binary.Base64;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.MatcherAssert;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.JIRAMacroParameters;

import com.sun.jdi.connect.spi.Connection;

import ch.qos.logback.core.boolex.Matcher;

/**
 * Unit tests for {@link AbstractJIRADataSource}.
 *
 * @version $Id$
 */
public class AbstractJIRADataSourceTest
{
    public class TestableAbstractJIRADataSource extends AbstractJIRADataSource
    {
        @Override
        public Collection<Element> getData(String macroContent, JIRAMacroParameters parameters)
        {
            return null;
        }

        @Override
        protected SAXBuilder createSAXBuilder()
        {
            throw new RuntimeException("error level 1",
                new RuntimeException("error level 2",
                    new RuntimeException("server error for URL: http://jira.xwiki.org/sr/"
                        + "jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery="
                        + "&os_username=%s&os_password=%s&os_authType=basic")));
        }
    }

    @Test
    public void preventShowingCredentialsInURLWhenError()
    {
        TestableAbstractJIRADataSource source = new TestableAbstractJIRADataSource();
        JIRAServer server = new JIRAServer("http://jira.xwiki.org", "username", "password");

        try {
            source.getXMLDocument(server, "query", 100);
            fail("should have thrown an exception here");
        } catch (Exception expected) {
            assertEquals("Failed to retrieve JIRA data from [http://jira.xwiki.org] for JQL [query]. Root cause: "
                + "[RuntimeException: server error for URL: http://jira.xwiki.org/sr/jira.issueviews:"
                + "searchrequest-xml/temp/SearchRequest.xml?jqlQuery=xxx]", expected.getMessage());
        }
    }

    @Test
    public void testGetXMLDocumentUsesHttpBasicAuthWithCredentials() throws Exception
    {
        String urlString = "http://localhost/jira/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?"
                + "jqlQuery=query";
        String base64Creds = Base64.encodeBase64String("username:password".getBytes());
        JIRAServer jiraServer = new JIRAServer("http://localhost/jira", "username", "password");

        AbstractJIRADataSource jiraDataSource = new TestableAbstractJIRADataSource();
        AbstractJIRADataSource spyDataSource = spy(jiraDataSource);
        InputStream is = mock(InputStream.class);
        Document doc = mock(Document.class);
        URLConnection connection = mock(URLConnection.class);
        SAXBuilder saxBuilder = mock(SAXBuilder.class);

        doReturn(saxBuilder).when(spyDataSource).createSAXBuilder();
        doReturn(doc).when(saxBuilder).build(is);
        doReturn(connection).when(spyDataSource).newConnectionFromUrl( eq( new URL(urlString) ) ) ;
        doReturn(is).when(connection).getInputStream();

        Document actualDoc = spyDataSource.getXMLDocument(jiraServer, "query", -1);
        assertSame(doc, actualDoc);
        verify(connection).setRequestProperty("Authorization", "Basic " + base64Creds);
    }

    @Test
    public void testGetXMLDocumentDoesNotHttpBasicAuthWithNoPassword() throws Exception
    {
        String urlString = "http://localhost/jira/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?"
                + "jqlQuery=query";
        JIRAServer jiraServer = new JIRAServer("http://localhost/jira", "username", null);

        AbstractJIRADataSource jiraDataSource = new TestableAbstractJIRADataSource();
        AbstractJIRADataSource spyDataSource = spy(jiraDataSource);
        InputStream is = mock(InputStream.class);
        Document doc = mock(Document.class);
        URLConnection connection = mock(URLConnection.class);
        SAXBuilder saxBuilder = mock(SAXBuilder.class);

        doReturn(saxBuilder).when(spyDataSource).createSAXBuilder();
        doReturn(doc).when(saxBuilder).build(is);
        doReturn(connection).when(spyDataSource).newConnectionFromUrl( eq( new URL(urlString) ) ) ;
        doReturn(is).when(connection).getInputStream();

        Document actualDoc = spyDataSource.getXMLDocument(jiraServer, "query", -1);
        assertSame(doc, actualDoc);
        verify(connection, never()).setRequestProperty(eq("Authorization"), any(String.class));
    }

}
