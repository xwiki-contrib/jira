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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Provider;

import org.junit.jupiter.api.Test;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.component.util.DefaultParameterizedType;
import org.xwiki.contrib.jira.config.JIRAServer;
import org.xwiki.contrib.jira.macro.JIRAMacroTransformation;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.WordBlock;
import org.xwiki.rendering.transformation.MacroTransformationContext;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link JIRAMacroTransformationManager}.
 *
 * @version $Id$
 * @since 10.3
 */
@ComponentTest
@ComponentList({ JIRAMacroTransformationManager.class })
public class JIRATransformationManagerTest
{
    @InjectMockComponents
    private JIRAMacroTransformationManager transformationManager;

    @MockComponent
    @Named("context")
    private Provider<ComponentManager> componentManagerProvider;

    static class TransformationTestClass implements JIRAMacroTransformation<String>
    {
        @Override
        public List<Block> transform(List<Block> blocks, String parameters,
            MacroTransformationContext context, JIRAServer jiraServer, String macroName)
        {
            List<Block> l = new ArrayList<>(blocks);
            l.add(new WordBlock("yy"));
            return l;
        }
    }

    @Test
    void TestTransformation() throws ComponentLookupException
    {
        ComponentManager componentManager = mock(ComponentManager.class);
        when(componentManagerProvider.get()).thenReturn(componentManager);
        when(componentManager.getInstanceList(any(DefaultParameterizedType.class))).thenReturn(
            List.of(new TransformationTestClass()));
        List<Block> res = transformationManager.transform(List.of(new WordBlock("ss")), "p", null, null, null);
        assertEquals(List.of(new WordBlock("ss"), new WordBlock("yy")), res);
    }
}
