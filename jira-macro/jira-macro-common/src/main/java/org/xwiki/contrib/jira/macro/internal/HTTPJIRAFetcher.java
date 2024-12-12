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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.config.JIRAServer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Fetches remotely the XML content at the passed URL.
 *
 * @version $Id$
 * @since 8.5
 */
@Component(roles = { HTTPJIRAFetcher.class })
@Singleton
public class HTTPJIRAFetcher
{
    private static final ErrorMessageExtractor EXTRACTOR = new ErrorMessageExtractor();

    /**
     * @param urlString the full JIRA URL to call
     * @param jiraServer the jira server data containing optional credentials (used to setup preemptive basic
     * authentication
     * @return the {@link Document} object containing the XML data
     * @throws Exception if an error happened during the fetch or if the passed URL is malformed
     */
    public Document fetch(String urlString, JIRAServer jiraServer) throws Exception
    {
        return performRequest(urlString, jiraServer, is -> {
            try {
                return createSAXBuilder().build(is);
            } catch (JDOMException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Fetch and parse a JSON based on the given information.
     * @param url the full JIRA URL to call
     * @param jiraServer the jira server data containing optional credentials (used to setup preemptive basic
     * authentication
     * @param type the actual type to obtain when parsing the JSON
     * @return an instance of the POJO corresponding to the JSON answer
     * @param <T> the expected type
     * @throws Exception in case of problem when performing the request
     */
    public <T> T fetchJSON(String url, JIRAServer jiraServer, Class<T> type) throws Exception
    {
        return performRequest(url, jiraServer, is -> {
            try {
                return new ObjectMapper().readValue(is, type);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private <T> T performRequest(String url, JIRAServer jiraServer, Function<InputStream, T> callback) throws Exception
    {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpClient httpClient = createHttpClientBuilder().build();

        HttpHost targetHost = createHttpHost(jiraServer);
        HttpClientContext context = HttpClientContext.create();
        setPreemptiveBasicAuthentication(context, jiraServer, targetHost);
        try (CloseableHttpResponse response = httpClient.execute(targetHost, httpGet, context)) {
            // Only parse the content if there was no error.
            if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300) {
                return callback.apply(response.getEntity().getContent());
            } else {
                // The error message is in the HTML. We extract it to perform some good error-reporting, by extracting
                // it from the <h1> tag.
                throw new Exception(String.format("Error = [%s]. URL = [%s]",
                    EXTRACTOR.extract(response.getEntity().getContent()), httpGet.getURI().toString()));
            }
        }
    }

    private void setPreemptiveBasicAuthentication(HttpClientContext context, JIRAServer jiraServer, HttpHost targetHost)
    {
        // Connect to JIRA using basic authentication if username and password are defined
        // Note: Set up preemptive basic authentication since JIRA can accept both unauthenticated and authenticated
        // requests. See https://developer.atlassian.com/server/jira/platform/basic-authentication/
        if (StringUtils.isNotBlank(jiraServer.getUsername()) && StringUtils.isNotBlank(jiraServer.getPassword())) {
            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(
                new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                new UsernamePasswordCredentials(jiraServer.getUsername(), jiraServer.getPassword()));
            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost, basicAuth);
            // Add AuthCache to the execution context
            context.setCredentialsProvider(provider);
            context.setAuthCache(authCache);
        }
    }

    private HttpHost createHttpHost(JIRAServer server) throws MalformedURLException
    {
        URL jiraURL = new URL(server.getURL());
        return new HttpHost(jiraURL.getHost(), jiraURL.getPort(), jiraURL.getProtocol());
    }

    private HttpClientBuilder createHttpClientBuilder()
    {
        // Allows system properties to override our default config (by calling useSystemProperties() first).
        HttpClientBuilder builder = HttpClientBuilder.create().useSystemProperties();
        return builder.setUserAgent("XWikiJIRAMacro");
    }

    /**
     * @return the SAXBuilder instance to use to retrieve the data
     */
    private SAXBuilder createSAXBuilder()
    {
        // Note: SAXBuilder is not thread-safe which is why we're instantiating a new one every time.
        SAXBuilder builder = new SAXBuilder();
        // Note: Prevent XXE attacks by disabling completely DTDs. This is possible since JIRA returns an XML content
        // that doesn't use a DTD.
        builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return builder;
    }
}
