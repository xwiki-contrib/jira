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
import java.util.Map;

/**
 * The serialized JSON error from a JIRA requet.
 *
 * @version $Id$
 * @since 11.0.1
 */
public class JIRAJSONError
{
    private List<String> errorMessages;

    private Map<Object, Object> errors;

    /**
     * Get the return message from JIRA.
     *
     * @return the error message.
     */
    public List<String> getErrorMessages()
    {
        return errorMessages;
    }

    /**
     * Set the error from JIRA.
     *
     * @param errorMessages the error message.
     */
    public void setErrorMessages(List<String> errorMessages)
    {
        this.errorMessages = errorMessages;
    }

    /**
     * Return the errors details from JIRA.
     *
     * @return the errors detail.
     */
    public Map<Object, Object> getErrors()
    {
        return errors;
    }

    /**
     * Set the errors details from JIRA.
     *
     * @param errors the errors detail.
     */
    public void setErrors(Map<Object, Object> errors)
    {
        this.errors = errors;
    }
}
