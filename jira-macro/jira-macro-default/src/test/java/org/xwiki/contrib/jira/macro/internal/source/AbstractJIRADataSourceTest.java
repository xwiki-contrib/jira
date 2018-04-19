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

import java.util.Collection;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.JIRAMacroParameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
}
