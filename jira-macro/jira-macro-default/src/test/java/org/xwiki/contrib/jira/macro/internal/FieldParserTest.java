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

import org.junit.Test;
import org.xwiki.contrib.jira.macro.JIRAField;

import static org.junit.Assert.*;

/**
 * Unit test for {@link FieldParser}.
 *
 * @version $Id$
 * @since 8.3
 */
public class FieldParserTest
{
    @Test
    public void parse() throws Exception
    {
        // Test: empty
        List<JIRAField> fields = new FieldParser().parse("");
        assertEquals(0, fields.size());

        // Test: only one field and one id
        fields = new FieldParser().parse("fieldid1");
        JIRAField parameter1 = new JIRAField("fieldid1");
        assertEquals(1, fields.size());
        assertEquals(parameter1, fields.get(0));

        // Test: only one field with id and label
        fields = new FieldParser().parse("fieldid1:fieldlabel1");
        parameter1 = new JIRAField("fieldid1");
        parameter1.setLabel("fieldlabel1");
        assertEquals(1, fields.size());
        assertEquals(parameter1, fields.get(0));

        // Test: only one field with id and type
        fields = new FieldParser().parse("fieldid1!fieldtype1");
        parameter1 = new JIRAField("fieldid1");
        parameter1.setType("fieldtype1");
        assertEquals(1, fields.size());
        assertEquals(parameter1, fields.get(0));

        // Test: only one field with id and label and type
        fields = new FieldParser().parse("fieldid1:fieldlabel1!fieldtype1");
        parameter1 = new JIRAField("fieldid1");
        parameter1.setLabel("fieldlabel1");
        parameter1.setType("fieldtype1");
        assertEquals(1, fields.size());
        assertEquals(parameter1, fields.get(0));

        // Test: two fields and one id
        fields = new FieldParser().parse("fieldid1,fieldid2");
        parameter1 = new JIRAField("fieldid1");
        assertEquals(2, fields.size());
        assertEquals(parameter1, fields.get(0));
        JIRAField parameter2 = new JIRAField("fieldid2");
        assertEquals(parameter2, fields.get(1));

        // Test: two fields with id and label and type
        fields = new FieldParser().parse("fieldid1:fieldlabel1!fieldtype1,fieldid2:fieldlabel2!fieldtype2");
        parameter1 = new JIRAField("fieldid1");
        parameter1.setLabel("fieldlabel1");
        parameter1.setType("fieldtype1");
        assertEquals(2, fields.size());
        assertEquals(parameter1, fields.get(0));
        parameter2 = new JIRAField("fieldid2");
        parameter2.setLabel("fieldlabel2");
        parameter2.setType("fieldtype2");
        assertEquals(parameter2, fields.get(1));

        // Test: id containing a sub-quote
        fields = new FieldParser().parse("fieldid1'some content'");
        parameter1 = new JIRAField("fieldid1some content");
        assertEquals(1, fields.size());
        assertEquals(parameter1, fields.get(0));

        // Test: id containing a quote
        fields = new FieldParser().parse("'some \"content\"'");
        parameter1 = new JIRAField("some \"content\"");
        assertEquals(1, fields.size());
        assertEquals(parameter1, fields.get(0));

        // Test: id containing a double quote
        fields = new FieldParser().parse("\"some 'content'\"");
        parameter1 = new JIRAField("some 'content'");
        assertEquals(1, fields.size());
        assertEquals(parameter1, fields.get(0));

        // Test: id containing a non-terminated quote
        fields = new FieldParser().parse("'some content");
        parameter1 = new JIRAField("some content");
        assertEquals(1, fields.size());
        assertEquals(parameter1, fields.get(0));

        // Test: id containing a quote and other known separator characters
        fields = new FieldParser().parse("'some:content!whatever'");
        parameter1 = new JIRAField("some:content!whatever");
        assertEquals(1, fields.size());
        assertEquals(parameter1, fields.get(0));

        // Test: 2 ids with quotes
        fields = new FieldParser().parse(
            "'fieldid1':'fieldlabel1'!'fieldtype1','fieldid2':'fieldlabel2'!'fieldtype2'");
        parameter1 = new JIRAField("fieldid1");
        parameter1.setLabel("fieldlabel1");
        parameter1.setType("fieldtype1");
        assertEquals(2, fields.size());
        assertEquals(parameter1, fields.get(0));
        parameter2 = new JIRAField("fieldid2");
        parameter2.setLabel("fieldlabel2");
        parameter2.setType("fieldtype2");
        assertEquals(parameter2, fields.get(1));
    }
}
