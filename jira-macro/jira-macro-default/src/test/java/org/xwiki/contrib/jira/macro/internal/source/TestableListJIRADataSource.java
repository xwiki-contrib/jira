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

import javax.inject.Named;
import javax.inject.Singleton;

import org.jdom2.input.SAXBuilder;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

@Component
@Named("list")
@Singleton
public class TestableListJIRADataSource extends ListJIRADataSource implements Initializable
{
    private SAXBuilder saxBuilder;

    @Override
    public void initialize() throws InitializationException
    {
        try {
            setSAXBuilder(MockSAXBuilder.getSAXBuilder());
        } catch (Exception e) {
            throw new InitializationException("Init failure", e);
        }
    }

    @Override
    protected SAXBuilder createSAXBuilder()
    {
        return this.saxBuilder;
    }

    public void setSAXBuilder(SAXBuilder saxBuilder)
    {
        this.saxBuilder = saxBuilder;
    }
}
