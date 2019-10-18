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

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.xwiki.contrib.jira.macro.JIRAField;
import org.xwiki.contrib.jira.macro.JIRAFieldDisplayer;
import org.xwiki.text.StringUtils;

/**
 * Helper to extract field values from JIRA XML.
 *
 * @version $Id$
 * @since 8.3
 */
public abstract class AbstractJIRAFieldDisplayer implements JIRAFieldDisplayer
{
    /**
     * Get the field value from the passed XML represented as an {@link Element} and look in custom fields when not
     * found in the default jira fields.
     *
     * @param field the field for which to get the value
     * @param issue the XML representation of the JIRA issue from which to extract the field's value
     * @return the string value of the field from the passed issue or null if not found
     */
    protected String getValue(JIRAField field, Element issue)
    {
        List<String> texts = new ArrayList<>();

        // First, look for the field name in the default fields
        List<Element> textElements = issue.getChildren(field.getId());
        if (textElements == null || textElements.isEmpty()) {
            // Not found as a default field. Verify if it's a custom field.
            Element customFieldsElement = issue.getChild("customfields");
            if (customFieldsElement != null) {
                for (Element customFieldElement : customFieldsElement.getChildren("customfield")) {
                    String customFieldName = customFieldElement.getChildTextTrim("customfieldname");
                    if (customFieldName != null && customFieldName.equals(field.getId())) {
                        // Found a matching field, render its value and stop looking
                        // Note: we only take into account the first "customfieldvalue" element for now. If a custom
                        // field needs to handle multiple values, it'll need a custom field displayer defined.
                        String value = customFieldElement.getChildren("customfieldvalues").get(0).getValue().trim();
                        texts.add(value);
                        break;
                    }
                }
            }
        } else {
            for (Element element : textElements) {
                if (element != null) {
                    texts.add(element.getTextTrim());
                }
            }
        }

        return texts.isEmpty() ? null : StringUtils.join(texts, ", ");
    }
}
