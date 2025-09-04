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

/**
 * Exception thrown when there is an error when accessing the remote JIRA server.
 *
 * @version $Id$
 * @since 10.1.3
 */
public class JIRAConnectionException extends Exception
{
    private static final long serialVersionUID = -2356790547774480310L;

    /**
     * Construct a new {@code MentionsException} with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link
     *     #getMessage()}
     */
    public JIRAConnectionException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new {@code MentionsException} with the specified detail message and cause.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link
     *     #getMessage()}
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method). (A null value
     *     is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public JIRAConnectionException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
