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

import org.xwiki.stability.Unstable;

/**
 * The charts displayed by the JIRA chart macros, for which a {@link JIRAConnector} provides the data.
 * <p>
 * For each chart, the documentation below lists the parameters, as built by the macros, that they pass to
 * {@link JIRAConnector#getChartData}, and the JSON the connector must return. The issues of a chart are selected either
 * by a saved filter or by a JQL query, passed in a way that depends on the chart (see each chart). A parameter value is
 * {@code null} when the macro has no value for it (e.g. {@code jql} when neither a filter nor a query is set). The
 * macros read only the JSON properties listed, and ignore the other ones. Changing these formats is an API change.
 *
 * @version $Id$
 * @since 11.2.0
 */
@Unstable
public enum JIRAChartType
{
    /**
     * The pie chart macro: the number of issues for each value of a field.
     * <p>
     * Parameters:
     * <ul>
     * <li>{@code filterId}: the id of the filter (without the {@code filter-} prefix), or {@code jql}: the JQL
     * query;</li>
     * <li>{@code statType}: the field to group the issues by, one of {@code statuses}, {@code allFixfor},
     * {@code assignees}, {@code priorities}, {@code components} or {@code issuetype}.</li>
     * </ul>
     * JSON: {@code filterTitle}, {@code filterUrl}, {@code statType}, {@code issueCount}, and {@code results}, a list
     * of {@code key}, {@code value} (the number of issues) and {@code url}.
     */
    PIE,

    /**
     * The created vs resolved chart macro: the number of issues created and resolved in each period.
     * <p>
     * Parameters:
     * <ul>
     * <li>{@code jql}: the JQL query, or {@code filter=<id>} for a filter;</li>
     * <li>{@code field}, with several values: {@code created}, {@code resolved}, and {@code unresolvedTrend} when the
     * trend of the unresolved issues is displayed;</li>
     * <li>{@code period}: {@code hourly}, {@code daily}, {@code weekly}, {@code monthly}, {@code quarterly} or
     * {@code yearly};</li>
     * <li>{@code operation}: {@code count} for the number of issues of each period, or {@code cumulative} for the
     * number of issues since the beginning;</li>
     * <li>{@code daysprevious}: the number of days to display, back from today;</li>
     * <li>{@code includeVersions}: {@code true} when the versions released in the displayed days are requested, absent
     * otherwise.</li>
     * </ul>
     * JSON: {@code filterTitle}, {@code filterUrl}, {@code totals} ({@code created} and {@code resolved} counts),
     * {@code results}, a list of periods with {@code start} and {@code end} (timestamps in milliseconds) and
     * {@code data} ({@code created}, {@code resolved} and {@code unresolvedTrend}, each with a {@code count}), and
     * {@code versions}, a list of {@code id}, {@code name}, {@code url}, {@code released}, {@code archived},
     * {@code releaseDate} and {@code project} ({@code id}, {@code name}, {@code key}).
     */
    CREATED_VS_RESOLVED,

    /**
     * The two dimensional grid macro: the number of issues for each pair of values of two fields.
     * <p>
     * Parameters:
     * <ul>
     * <li>{@code filterId}: {@code filter-<id>} for a filter, or {@code jql-<query>} for a JQL query;</li>
     * <li>{@code xstattype} and {@code ystattype}: the fields of the columns and of the rows, each one of
     * {@code assignees}, {@code reporter}, {@code statuses}, {@code allFixfor}, {@code components},
     * {@code priorities} or {@code issuetype};</li>
     * <li>{@code sortDirection}: {@code asc} or {@code desc};</li>
     * <li>{@code sortBy}: {@code natural} to sort the rows by value, or {@code total} by number of issues;</li>
     * <li>{@code numberToShow}: the maximum number of rows.</li>
     * </ul>
     * JSON: {@code filterTitle}, {@code filterUrl}, {@code xHeading}, {@code yHeading}, {@code totalRows}, and
     * {@code firstRow} and {@code rows}, rows of {@code cells}, each with an HTML {@code markup}.
     */
    TWO_DIMENSIONAL_GRID
}
