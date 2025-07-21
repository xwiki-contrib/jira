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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.function.FailableFunction;
import org.apache.hc.client5.http.ContextBuilder;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.contrib.jira.config.JIRAServer;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

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

    @Inject
    @Named("context")
    private Provider<ComponentManager> componentManagerProvider;

    /**
     * @param urlString the full JIRA URL to call
     * @param jiraServer the jira server data containing optional credentials (used to setup preemptive basic
     *     authentication
     * @return the {@link Document} object containing the XML data
     * @throws JIRAConnectionException if an error happened during the fetch or if the passed URL is malformed
     */
    public Document fetch(String urlString, JIRAServer jiraServer) throws JIRAConnectionException
    {
        try {
            return performRequest(urlString, jiraServer, is -> createSAXBuilder().build(is));
        } catch (Exception e) {
            // The XML has failed to be parsed, read it as plain text and return it, for debugging purpose.
            String message = String.format("Failed to parse JIRA XML content [%s]",
                getRawJIRAResponse(urlString, jiraServer));
            throw new JIRAConnectionException(message, e);
        }
    }

    private ObjectMapper getObjectMapper()
    {
        return new ObjectMapper()
            // We don't want the parsing to fail on unknown properties: we want to have possibility to ignore some
            // values and to avoid issue in case of evolution of the returns
            .disable(FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * Fetch and parse a JSON based on the given information.
     *
     * @param urlString the full JIRA URL to call
     * @param jiraServer the jira server data containing optional credentials (used to setup preemptive basic
     *     authentication
     * @param type the actual type to obtain when parsing the JSON
     * @param <T> the expected type
     * @return an instance of the POJO corresponding to the JSON answer
     * @throws JIRAConnectionException in case of problem when performing the request
     */
    public <T> T fetchJSON(String urlString, JIRAServer jiraServer, Class<T> type) throws JIRAConnectionException
    {
        try {
            return performRequest(urlString, jiraServer, is -> getObjectMapper().readValue(is, type));
        } catch (Exception e) {
            // The JSON has failed to be parsed, read it as plain text and return it, for debugging purpose.
            String message = String.format("Failed to parse JIRA JSON content [%s]",
                getRawJIRAResponse(urlString, jiraServer));
            throw new JIRAConnectionException(message, e);
        }
    }

    private String getRawJIRAResponse(String urlString, JIRAServer jiraServer)
    {
        String message;
        try {
            message = performRequest(urlString, jiraServer, is -> IOUtils.toString(is, StandardCharsets.UTF_8));
        } catch (Exception e) {
            message = String.format("Failed to get JIRA content for [%s]", urlString);
        }
        return message;
    }

    private <T> T performRequest(String url, JIRAServer jiraServer,
        FailableFunction<InputStream, T, Exception> callback) throws Exception
    {
        HttpGet httpGet = new HttpGet(url);
        try (CloseableHttpClient httpClient = createHttpClientBuilder().build()) {
            HttpHost targetHost = createHttpHost(jiraServer);
            ContextBuilder context = ContextBuilder.create();
            jiraServer.getJiraAuthenticator().ifPresent(a ->
                a.authenticateInHttpClient(context, httpGet, targetHost));
            try (CloseableHttpResponse response = httpClient.execute(targetHost, httpGet, context.build())) {
                // Only parse the content if there was no error.
                if (response.getCode() >= 200 && response.getCode() < 300) {
                    return callback.apply(response.getEntity().getContent());
                } else {
                    // The error message is in the HTML. We extract it to perform some good error-reporting,
                    // by extracting it from the <h1> tag.
                    throw new JIRAConnectionException(String.format("Error = [%s]. URL = [%s]",
                        EXTRACTOR.extract(response.getEntity().getContent()), httpGet.getUri().toString()));
                }
            }
        }
    }

    private HttpHost createHttpHost(JIRAServer server) throws MalformedURLException
    {
        URL jiraURL = new URL(server.getURL());
        return new HttpHost(jiraURL.getProtocol(), jiraURL.getHost(), jiraURL.getPort());
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

        // Note: Prevent XXE attacks
        builder.setFeature("http://xml.org/sax/features/external-general-entities", false);
        builder.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        builder.setExpandEntities(false);

        return builder;
    }
}
