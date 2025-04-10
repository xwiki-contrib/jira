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

import javax.inject.Inject;

import org.xwiki.contrib.jira.config.JIRAConfiguration;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.properties.converter.AbstractConverter;

/**
 * Converts a String containing a JIRA server id into a {@link JIRAServer} object.
 *
 * @version $Id$
 * @since 8.2
 */
// @Component
// @Singleton
public class JIRAServerConverter extends AbstractConverter<JIRAServer>
{
    @Inject
    private JIRAConfiguration configuration;

    @Override
    protected JIRAServer convertToType(Type type, Object value)
    {
        if (value == null) {
            return null;
        }

        // Find the id in the jira server configuration
        return this.configuration.getJIRAServers().get(value.toString());
    }
}
