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

/**
 * Generic token parser for list of tokens following the format:
 * {@code token11<separator11>token12<separator12>...,token21<separator21>token22<separator22>...,...}.
 * The parser support single and double quotes around each token. All tokens are also optional.
 *
 * @version $Id$
 * @since 8.3
 */
public abstract class AbstractTokenListParser
{
    private static final char SINGLE_QUOTE = '\'';

    private static final char DOUBLE_QUOTE = '\"';

    private static final char ITEM_SEPARATOR = ',';

    protected List<String[]> parseGeneric(String content)
    {
        List<String[]> tokenList = new LinkedList<>();

        if (StringUtils.isBlank(content)) {
            return tokenList;
        }

        String[] values = new String[getTokenCount()];
        int currentPosition = 0;
        StringBuilder buffer = new StringBuilder();
        char currentQuoteChar = 0;
        char currentChar;
        for (int i = 0; i < content.length(); i++) {
            currentChar = content.charAt(i);
            if (currentQuoteChar != 0) {
                if (currentChar == currentQuoteChar) {
                    currentQuoteChar = 0;
                } else {
                    buffer.append(currentChar);
                }
            } else if (currentChar == SINGLE_QUOTE || currentChar == DOUBLE_QUOTE) {
                currentQuoteChar = currentChar;
            } else if (isSeparator(currentChar)) {
                // Save the buffer
                saveToken(currentPosition, buffer, values);
                currentPosition = getNextPosition(currentChar);
            } else if (currentChar == ITEM_SEPARATOR) {
                // Moving to the next token
                saveToken(currentPosition, buffer, values);
                tokenList.add(values);
                values = new String[getTokenCount()];
                currentPosition = 0;
                currentQuoteChar = 0;
            } else {
                buffer.append(currentChar);
            }
        }
        // Save the last tokens
        saveToken(currentPosition, buffer, values);
        tokenList.add(values);

        return tokenList;
    }

    private void saveToken(int currentPosition, StringBuilder buffer, String[] values)
    {
        values[currentPosition] = buffer.toString().trim();
        buffer.setLength(0);
    }

    protected abstract int getTokenCount();

    protected abstract boolean isSeparator(char currentChar);

    protected abstract int getNextPosition(char currentChar);
}
