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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.xwiki.contrib.jira.macro.JIRAField;

/**
 * Parses field definitions from the Macro's {@code field} parameter. Format is:
 * @version $Id$
 * @since 8.3
 */
public class FieldParser
{
    private static final char SINGLE_QUOTE = '\'';

    private static final char DOUBLE_QUOTE = '\"';

    private static final char NAME_SEPARATOR = ':';

    private static final char TYPE_SEPARATOR = '!';

    private static final char FIELD_SEPARATOR = ',';

    private static final int STATE_ID = 0;

    private static final int STATE_NAME = 1;

    private static final int STATE_TYPE = 2;

    /**
     * Valid formats:
     * <ul>
     *   <li>{@code fieldid1,fieldid2}</li>
     *   <li>{@code fieldid1:fieldname1,fieldid2:fieldname2}</li>
     *   <li>{@code fieldid1:fieldname1!fieldtype1,fieldid2:fieldname2!fieldtype2}</li>
     * </ul>
     *
     * @param fieldString
     * @return
     */
    public List<JIRAField> parse(String fieldString)
    {
        List<JIRAField> fields = new LinkedList<>();

        if (StringUtils.isBlank(fieldString)) {
            return fields;
        }

        String[] values = new String[3];
        int currentPosition = STATE_ID;
        StringBuilder buffer = new StringBuilder();
        char currentQuoteChar = 0;
        char currentChar;
        for (int i = 0; i < fieldString.length(); i++) {
            currentChar = fieldString.charAt(i);
            if (currentQuoteChar != 0) {
                if (currentChar == currentQuoteChar) {
                    currentQuoteChar = 0;
                } else {
                    buffer.append(currentChar);
                }
            } else if (currentChar == SINGLE_QUOTE || currentChar == DOUBLE_QUOTE) {
                currentQuoteChar = currentChar;
            } else if (currentChar == NAME_SEPARATOR || currentChar == TYPE_SEPARATOR) {
                // Save the buffer
                values[currentPosition] = buffer.toString();
                buffer.setLength(0);
                if (currentChar == NAME_SEPARATOR) {
                    currentPosition = STATE_NAME;
                } else {
                    currentPosition = STATE_TYPE;
                }
            } else if (currentChar == FIELD_SEPARATOR) {
                // Moving to the next field definition
                values[currentPosition] = buffer.toString();
                buffer.setLength(0);
                saveFieldDefinition(values, fields);
                currentPosition = STATE_ID;
                currentQuoteChar = 0;
            } else {
                buffer.append(currentChar);
            }
        }
        // Save the last field definition
        values[currentPosition] = buffer.toString();
        saveFieldDefinition(values, fields);

        return fields;
    }

    private void saveFieldDefinition(String[] values, List<JIRAField> fields)
    {
        JIRAField fieldParameter = new JIRAField(values[0].trim());
        if (values[1] != null) {
            fieldParameter.setLabel(values[1].trim());
        }
        if (values[2] != null) {
            fieldParameter.setType(values[2].trim());
        }
        fields.add(fieldParameter);
    }
}
