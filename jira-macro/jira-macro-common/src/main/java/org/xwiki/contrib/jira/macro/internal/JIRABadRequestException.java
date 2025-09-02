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

import java.util.List;

/**
 * Exception thrown when there is an error when accessing the remote JIRA server.
 *
 * @version $Id$
 * @since 11.0.1
 */
public class JIRABadRequestException extends JIRAConnectionException
{
    private static final long serialVersionUID = -2356790547774480311L;

    private final List<String> extractedMessages;

    /**
     * Construct a new {@code JIRABadRequestException} with the specified detail message.
     *
     * @param exceptionMessage the detail message. The detail message is saved for later retrieval by the
     *     {@link #getMessage()}
     * @param extractedMessages the extracted message from the {@link ErrorMessageExtractor}.
     */
    public JIRABadRequestException(String exceptionMessage, List<String> extractedMessages)
    {
        super(exceptionMessage);
        this.extractedMessages = extractedMessages;
    }

    /**
     * Get the extracted message from the JIRA answer.
     *
     * @return the extracted message from the {@link ErrorMessageExtractor}.
     */
    public List<String> getExtractedMessages()
    {
        return extractedMessages;
    }
}
