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
 * The JIRA gadget REST resources used by the chart macros.
 *
 * @version $Id$
 * @since 11.2.0
 */
@Unstable
public enum JIRAGadgetType
{
    /**
     * For the pie chart macro.
     */
    PIE_CHART("statistics"),

    /**
     * For the created vs resolved macro.
     */
    CREATED_VS_RESOLVED("dateCountInPeriod"),

    /**
     * For the two dimensional grid charts.
     */
    BIDIMENSIONAL_CHARTS("twodimensionalfilterstats/generate");

    private final String endpoint;

    JIRAGadgetType(String endpoint)
    {
        this.endpoint = endpoint;
    }

    /**
     * @return the endpoint to be used in URLs.
     */
    public String getEndpoint()
    {
        return this.endpoint;
    }
}
