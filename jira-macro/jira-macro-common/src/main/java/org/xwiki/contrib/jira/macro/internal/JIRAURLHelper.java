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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.config.JIRAServer;

/**
 * Utility component to build URLs for requesting JIRA.
 *
 * @version $Id$
 * @since 10.0
 */
@Component(roles = JIRAURLHelper.class)
@Singleton
public class JIRAURLHelper
{
    /**
     * REST endpoint to access directly the statistics for the gadgets.
     */
    private static final String GADGET_REST_ENDPOINT = "/rest/gadget/1.0/";

    /**
     * URL Prefix to use to build the full JQL URL (doesn't contain the JQL query itself which needs to be appended).
     */
    private static final String JQL_SEARCH_URL_PREFIX =
        "/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=";


    @Inject
    private Logger logger;

    /**
     * The type of gadgets we want to request and their corresponding endpoints.
     */
    public enum GadgetType
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

        GadgetType(String endpoint)
        {
            this.endpoint = endpoint;
        }

        /**
         * @return the endpoint to be used in URLs.
         */
        public String getEndpoint()
        {
            return endpoint;
        }
    }

    /**
     * @param server the actual JIRA server to request
     * @param jqlQuery the query to perform in the search
     * @param maxCount the maximum number of issues to get or {@code -1} to not limit it
     * @return the URL to perform the search.
     */
    public String getSearchURL(JIRAServer server, String jqlQuery, int maxCount)
    {
        StringBuilder additionalQueryString = new StringBuilder();

        // Restrict number of issues returned if need be
        if (maxCount > -1) {
            additionalQueryString.append("&tempMax=").append(maxCount);
        }

        // Note: we encode using UTF8 since it's the W3C recommendation.
        // See http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars
        String fullURL = String.format("%s%s%s%s", server.getURL(), JQL_SEARCH_URL_PREFIX, encode(jqlQuery),
            additionalQueryString);
        this.logger.debug("Computed JIRA URL [{}]", fullURL);

        return fullURL;
    }

    /**
     * @param server the actual JIRA server to request
     * @param gadgetType the gadget for which to request statistics
     * @param queryParameters the parameters of the request
     * @return the URL to perform the query.
     */
    public String getChartURL(JIRAServer server, GadgetType gadgetType, List<NameValuePair> queryParameters)
    {
        String queryString = URLEncodedUtils.format(queryParameters, StandardCharsets.UTF_8);
        return String.format("%s%s%s?%s", server.getURL(), GADGET_REST_ENDPOINT, gadgetType.getEndpoint(), queryString);
    }

    private String encode(String content)
    {
        try {
            return URLEncoder.encode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Missing UTF-8 encoding", e);
        }
    }
}
