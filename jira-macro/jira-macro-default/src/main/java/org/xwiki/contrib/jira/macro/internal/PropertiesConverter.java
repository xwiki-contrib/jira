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

import java.lang.reflect.Type;
import java.util.Properties;

import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.properties.converter.AbstractConverter;

/**
 * Converts a String of the format {@code a=b,c=d, ...} into a {@link Properties} object.
 *
 * @version $Id$
 */
@Component
@Singleton
public class PropertiesConverter extends AbstractConverter<Properties>
{
    private static final char TOKEN_SEPARATOR = '=';

    private class PropertiesParser extends AbstractTokenListParser
    {
        public Properties parse(String content)
        {
            Properties properties = new Properties();
            for (String[] tokens : parseGeneric(content)) {
                if (tokens[0] != null) {
                    properties.setProperty(tokens[0], tokens[1]);
                }
            }
            return properties;
        }

        @Override
        protected int getTokenCount()
        {
            return 2;
        }

        @Override
        protected boolean isSeparator(char currentChar)
        {
            return currentChar == TOKEN_SEPARATOR;
        }

        @Override
        protected int getNextPosition(char currentChar)
        {
            return 1;
        }
    }

    private PropertiesParser parser = new PropertiesParser();

    @Override
    protected Properties convertToType(Type targetType, Object value)
    {
        return this.parser.parse(value.toString());
    }
}
