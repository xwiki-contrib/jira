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
package org.xwiki.contrib.jira.macro.internal.source;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

/**
 * Extracts error messages from JIRA HTML responses.
 *
 * @version $Id$
 */
public class ErrorMessageExtractor
{
    private static final Pattern PATTERN = Pattern.compile("<h1>(.*)</h1>");

    /**
     * @param contentStream the stream containing the HTML content with the error message
     * @return the extracted error message
     * @throws IOException in case of reading error
     */
    String extract(InputStream contentStream) throws IOException
    {
        String result;
        String content = IOUtils.toString(contentStream, "UTF-8");
        Matcher matcher = PATTERN.matcher(content);
        if (matcher.find()) {
            result = matcher.group(1);
        } else {
            result = "Unknown error";
        }
        return result;
    }
}
