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

import org.apache.hc.core5.http.NameValuePair;
import org.jdom2.Document;
import org.xwiki.component.annotation.Role;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.stability.Unstable;

/**
 * Defines how the JIRA macros get their data from a given kind of JIRA instance. The component hint is the instance
 * type it handles (see {@link JIRAServer#getInstanceType()}), which allows extensions to add the support of other kinds
 * of JIRA instances.
 * <p>
 * Whatever the kind of instance, the data are returned in the formats of JIRA Data Center, which are the ones
 * understood by the macros.
 *
 * @version $Id$
 * @since 11.2.0
 */
@Role
@Unstable
public interface JIRAConnector
{
    /**
     * @param server the JIRA server to search on
     * @param jql the JQL query to execute
     * @param maxCount the maximum number of issues to return, or {@code -1} to not limit it (in which case an
     *     implementation can apply its own limit)
     * @return the matching issues, as the document returned by the JIRA XML search view (i.e. one
     *     {@code rss/channel/item} element per issue)
     * @throws JIRAConnectionException if the issues cannot be retrieved, and more specifically a
     *     {@link JIRABadRequestException} when the query is refused (e.g. invalid JQL or missing rights)
     */
    Document searchIssues(JIRAServer server, String jql, int maxCount) throws JIRAConnectionException;

    /**
     * @param server the JIRA server to search on
     * @param jql the JQL query to execute
     * @return the number of issues matching the query
     * @throws JIRAConnectionException if the count cannot be retrieved, and more specifically a
     *     {@link JIRABadRequestException} when the query is refused (e.g. invalid JQL or missing rights)
     */
    Integer countIssues(JIRAServer server, String jql) throws JIRAConnectionException;

    /**
     * @param server the JIRA server to get the data from
     * @param type the gadget the data are requested for
     * @param parameters the query parameters of the JIRA gadget REST resource
     * @param expectedType the type in which to deserialize the JSON returned by the JIRA gadget REST resource
     * @param <T> the expected type
     * @return the data of a chart, in the JSON format of the JIRA gadget REST resource
     * @throws JIRAConnectionException if the data cannot be retrieved, and more specifically a
     *     {@link JIRABadRequestException} when the query is refused (e.g. invalid JQL or missing rights)
     */
    <T> T fetchGadgetData(JIRAServer server, JIRAGadgetType type, List<NameValuePair> parameters,
        Class<T> expectedType) throws JIRAConnectionException;
}
