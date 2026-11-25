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

import java.util.List;

import org.jdom2.Element;
import org.junit.jupiter.api.Test;
import org.xwiki.contrib.jira.macro.JIRAField;
import org.xwiki.contrib.jira.macro.JIRAMacroParameters;
import org.xwiki.localization.ContextualLocalizationManager;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.VerbatimBlock;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link KeyJIRAFieldDisplayer}.
 *
 * @version $Id$
 */
@ComponentTest
class KeyJIRAFieldDisplayerTest
{
    private static final String MOVED_TO = "jira.macro.default.displayer.key.movedTo";

    @InjectMockComponents
    private KeyJIRAFieldDisplayer displayer;

    @MockComponent
    private ContextualLocalizationManager localizationManager;

    @Test
    void displayMovedIssue()
    {
        when(this.localizationManager.getTranslationPlain(MOVED_TO, "OLD-1", "NEW-1"))
            .thenReturn("OLD-1 (moved to NEW-1)");

        assertEquals("OLD-1 (moved to NEW-1)", display());
    }

    @Test
    void displayMovedIssueWhenTranslationMissing()
    {
        assertEquals("NEW-1", display());
    }

    private String display()
    {
        Element issue = new Element("item");
        issue.addContent(new Element(JIRAField.KEY.getId()).addContent("NEW-1"));
        issue.addContent(new Element(JIRAField.ORIGINAL_KEY).addContent("OLD-1"));
        issue.addContent(new Element(JIRAField.RESOLUTION.getId()).setAttribute("id", "-1"));

        List<Block> blocks = this.displayer.displayField(JIRAField.KEY, issue, new JIRAMacroParameters());

        return ((VerbatimBlock) blocks.get(0)).getProtectedString();
    }
}
