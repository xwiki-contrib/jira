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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Extracts error messages from JIRA HTML responses.
 *
 * @version $Id$
 */
@Singleton
@Component(roles = { ErrorMessageExtractor.class })
public class ErrorMessageExtractor
{
    private static final String UNKNOWN_ERROR = "Unknown error";

    @Inject
    private Logger logger;

    /**
     * @param entity the stream containing the HTML content with the error message
     * @return the extracted error message
     * @throws IOException in case of reading error
     */
    public List<String> extract(HttpEntity entity) throws IOException
    {
        String contentType = ContentType.parse(entity.getContentType()).getMimeType();
        String content = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
        if ("text/html".equals(contentType)) {
            try {
                /*
                When JIRA returns an Error for an HTML or XML content the error returned is the HTML error from tomcat.
                Ideally we would like to use the REST API which return a more strong structured message instead.
                But this need a full rewrite of some components to use the REST API result, and we are not sure of the
                compatibility with JIRA cloud.
                Fortunately, it seems that from the Atlassian documentation JIRA can only be used with Tomcat we can
                suppose that the message will have allways the same structure. Generally Tomcat will return something
                like this:
                --------------------------
                Type Status Report
                Message The value 'TEST' does not exist for the field 'project'.
                Description The server cannot or will not process the request due to something that is perceived to
                    be a client error (e.g., malformed request syntax, invalid request message framing, or deceptive
                    request routing).
                --------------------------
                The interesting message from JIRA here is "The value 'TEST' does not exist for the field 'project'.".
                So we need to get this line from the HTML returned by Tomcat.
                So for this reason we will try to get the second paragraph from the body.
                 */
                Document d = org.jsoup.Jsoup.parse(content);
                Element body = d.getElementsByTag("body").first();
                if (body != null) {
                    List<Element> paragraphs = new ArrayList<>(body.getElementsByTag("p"));
                    if (paragraphs.size() > 2) {
                        return List.of(paragraphs.get(1).ownText());
                    }
                }
            } catch (Exception e) {
                logger.debug("Can't get message details from JIRA. Full HTML [{}]", content, e);
                return List.of(UNKNOWN_ERROR);
            }
        } else if ("application/json".equals(contentType)) {
            try {
                JIRAJSONError jirajsonError = new ObjectMapper()
                    // We don't want the parsing to fail on unknown properties: we want to have possibility to ignore
                    // some values and to avoid issue in case of evolution of the returns
                    .disable(FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(content, JIRAJSONError.class);
                return jirajsonError.getErrorMessages();
            } catch (JacksonException e) {
                logger.debug("Can't decode JSON from JIRA. JSON [{}]", content, e);
                return List.of(content);
            }
        }
        return List.of(UNKNOWN_ERROR);
    }
}
