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
import org.xwiki.component.annotation.Role;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.stability.Unstable;

/**
 * Defines how the JIRA macros get their data from a given kind of JIRA instance. The component hint is the deployment
 * type it handles (see {@link JIRAServer#getDeploymentType()}), which allows extensions to add the support of other
 * kinds of JIRA instances.
 * <p>
 * Whatever the kind of instance, the data must be returned in the formats described below, which are the ones read by
 * the macros (and the same as the ones of JIRA Data Center). Changing these formats is an API change.
 *
 * <h2>Issues</h2>
 * One {@code <item>} element per issue, as in the XML search view of JIRA Data Center, which is also what the
 * {@code JIRADataSource}, {@code JIRADisplayer} and {@code JIRAFieldDisplayer} components of the {@code jira} macro
 * exchange. The default displayers read the following children of an item:
 * <ul>
 * <li>{@code key}, {@code summary}, {@code link} (the URL of the issue), {@code assignee}, {@code reporter} and
 * {@code votes}, as text;</li>
 * <li>{@code type} and {@code status}: the name as text and the URL of the icon in the {@code iconUrl} attribute;</li>
 * <li>{@code resolution}, required: the name as text and its {@code id} attribute, {@code -1} when the issue is
 * not resolved (the issue is displayed as resolved otherwise);</li>
 * <li>{@code created}, {@code updated} and {@code resolved}: dates in the {@code EEE, d MMM yyyy HH:mm:ss Z} format,
 * as returned by JIRA Data Center (e.g. {@code Fri, 16 Apr 2004 10:57:58 -0700}). They are parsed with the default
 * locale of the XWiki server, and a date that cannot be parsed is displayed as is;</li>
 * <li>{@code version}, {@code fixVersion} and {@code component}: one element per value;</li>
 * <li>{@code labels}: one {@code label} child element per label;</li>
 * <li>{@code customfields}: one {@code customfield} element per custom field, with the name of the field in
 * {@code customfieldname} and its values in {@code customfieldvalues/customfieldvalue}. The
 * {@code customfieldvalues} element is required, and only the text of the first one is displayed.</li>
 * </ul>
 * Any other field asked by the user is read as the text of the child element named after the field.
 *
 * <h2>Chart data</h2>
 * The JSON data of the chart. The parameters passed for each chart and the JSON properties read by the macros are
 * described in {@link JIRAChartType}.
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
     * @return the matching issues, one {@code <item>} element per issue (see the format above), in the order returned
     *     by the query, which must be kept (e.g. for the {@code ORDER BY} clause)
     * @throws JIRAConnectionException if the issues cannot be retrieved, and more specifically a
     *     {@link JIRABadRequestException} when the query is refused (e.g. invalid JQL or missing rights)
     */
    List<Element> searchIssues(JIRAServer server, String jql, int maxCount) throws JIRAConnectionException;

    /**
     * @param server the JIRA server to search on
     * @param jql the JQL query to execute
     * @return the number of issues matching the query
     * @throws JIRAConnectionException if the count cannot be retrieved, and more specifically a
     *     {@link JIRABadRequestException} when the query is refused (e.g. invalid JQL or missing rights)
     */
    long countIssues(JIRAServer server, String jql) throws JIRAConnectionException;

    /**
     * @param server the JIRA server to get the data from
     * @param type the chart the data are requested for
     * @param parameters the parameters of the chart (see {@link JIRAChartType}), in the order built by the macros.
     *     A parameter can have several values (e.g. {@code field} for {@link JIRAChartType#CREATED_VS_RESOLVED}), and
     *     a value can be {@code null} when the macro has no value for it.
     * @return the data of the chart, in the JSON format described in {@link JIRAChartType}, never {@code null}
     * @throws JIRAConnectionException if the data cannot be retrieved, and more specifically a
     *     {@link JIRABadRequestException} when the query is refused (e.g. invalid JQL or missing rights)
     */
    String getChartData(JIRAServer server, JIRAChartType type, Map<String, List<String>> parameters)
        throws JIRAConnectionException;
}
