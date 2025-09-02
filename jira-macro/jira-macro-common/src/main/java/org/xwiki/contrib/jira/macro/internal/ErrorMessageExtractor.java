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

import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Extracts error messages from JIRA HTML responses.
 *
 * @version $Id$
 */
public class ErrorMessageExtractor
{
    private static final String UNKNOWN_ERROR = "Unknown error";

    /**
     * @param entity the stream containing the HTML content with the error message
     * @return the extracted error message
     * @throws IOException in case of reading error
     */
    List<String> extract(HttpEntity entity) throws IOException
    {
        String contentType = ContentType.parse(entity.getContentType()).getMimeType();
        String content = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
        if ("text/html".equals(contentType)) {
            try {
                Document d = org.jsoup.Jsoup.parse(content);
                Element body = d.getElementsByTag("body").first();
                if (body != null) {
                    List<Element> paragraphs = new ArrayList<>(body.getElementsByTag("p"));
                    if (paragraphs.size() > 2) {
                        return List.of(paragraphs.get(1).ownText());
                    }
                }
            } catch (Exception e) {
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
                return List.of(content);
            }
        }
        return List.of(UNKNOWN_ERROR);
    }
}
