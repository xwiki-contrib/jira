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
package org.xwiki.contrib.jira.macro.internal.displayer.field;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;

import org.jdom2.Element;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.macro.JIRAField;
import org.xwiki.contrib.jira.macro.JIRAMacroParameters;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.VerbatimBlock;

/**
 * Default displayer that simply displays the field as text.
 *
 * @version $Id$
 * @since 4.2M1
 */
@Component
@Singleton
public class DefaultJIRAFieldDisplayer extends AbstractJIRAFieldDisplayer
{
    @Override
    public List<Block> displayField(JIRAField field, Element issue, JIRAMacroParameters parameters)
    {
        List<Block> result = Collections.emptyList();

        String value = getValue(field, issue);
        if (value != null) {
            result = Arrays.asList(new VerbatimBlock(value, true));
        }

        return result;
    }
}
