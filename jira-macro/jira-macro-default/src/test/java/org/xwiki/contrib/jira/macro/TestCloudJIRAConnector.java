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

import java.util.List;
import java.util.Map;

import org.jdom2.Element;
import org.xwiki.contrib.jira.config.JIRAServer;

/**
 * A connector for a fictitious "cloud" deployment type, used to check that the macros get their data from the connector
 * of the deployment type of the server.
 *
 * @version $Id$
 */
public class TestCloudJIRAConnector implements JIRAConnector
{
    @Override
    public List<Element> searchIssues(JIRAServer server, String jql, int maxCount)
    {
        Element item = new Element("item");
        item.addContent(new Element("key").setText("CLOUD-1"));
        item.addContent(new Element("link").setText("http://jira.cloud.invalid/browse/CLOUD-1"));
        item.addContent(new Element("summary").setText("Issue returned by the cloud connector for [" + jql + "]"));
        item.addContent(new Element("resolution").setAttribute("id", "-1").setText("Unresolved"));
        return List.of(item);
    }

    @Override
    public long countIssues(JIRAServer server, String jql)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getChartData(JIRAServer server, JIRAChartType type, Map<String, List<String>> parameters)
    {
        throw new UnsupportedOperationException();
    }
}
