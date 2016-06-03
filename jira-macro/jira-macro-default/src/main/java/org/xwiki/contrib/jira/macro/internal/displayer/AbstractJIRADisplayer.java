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
package org.xwiki.contrib.jira.macro.internal.displayer;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.contrib.jira.macro.JIRADisplayer;
import org.xwiki.contrib.jira.macro.JIRAFieldDisplayer;
import org.xwiki.contrib.jira.macro.JIRAMacroParameters;
import org.xwiki.contrib.jira.macro.JIRAField;

/**
 * Common issue Displayer that Displayers can extend and that provides common methods.
 *
 * @version $Id$
 * @since 4.2M1
 */
public abstract class AbstractJIRADisplayer implements JIRADisplayer
{
    /**
     * Used to find specific Field displayers.
     */
    @Inject
    protected ComponentManager componentManager;

    /**
     * Default field displayer to use when there's no specific field displayer for a field.
     */
    @Inject
    protected JIRAFieldDisplayer defaultDisplayer;

    /**
     * @param field the field to display
     * @return the field displayer to use to display the passed field
     */
    protected JIRAFieldDisplayer getFieldDisplayer(JIRAField field)
    {
        JIRAFieldDisplayer displayer;
        try {
            // Look for a field displayer for the field id
            displayer = this.componentManager.getInstance(JIRAFieldDisplayer.class, field.getId());
        } catch (ComponentLookupException e) {
            // Look for a field displayer for the field type
            try {
                displayer = this.componentManager.getInstance(JIRAFieldDisplayer.class,
                    String.format("type/%s", field.getType()));
            } catch (ComponentLookupException ee) {
                // Use the default displayer
                displayer = this.defaultDisplayer;
            }
        }
        return displayer;
    }

    /**
     * @param parameters the macro parameters from which to get an optional list of JIRA field names to display (if not
     *            defined by the user then use default field names)
     * @return the list of JIRA fields to be displayed
     */
    protected List<JIRAField> normalizeFields(JIRAMacroParameters parameters)
    {
        List<JIRAField> fields = parameters.getFields().getFields();
        if (fields.isEmpty()) {
            fields = getDefaultFields();
        } else {
            // Normalize field data, i.e. fill up any blank by using default field data

            // Step 1: For backward-compatiblity reasons, use the field names defined in the macro parameters if defined
            if (parameters.getFieldNames() != null) {
                List<String> fieldNames = parameters.getFieldNames();
                for (int i = 0; i < fields.size(); i++) {
                    if (StringUtils.isBlank(fields.get(i).getLabel()) && fieldNames.size() > i) {
                        fields.get(i).setLabel(fieldNames.get(i));
                    }
                }
            }

            // Step 2: Normalize using default fields definitions
            for (JIRAField field : fields) {
                normalizeField(field);
            }
        }

        return fields;
    }

    private void normalizeField(JIRAField field)
    {
        if (StringUtils.isBlank(field.getLabel()) || StringUtils.isBlank(field.getType())) {
            JIRAField defautField = JIRAField.DEFAULT_FIELDS.get(field.getId());
            if (defautField != null) {
                if (StringUtils.isBlank(field.getLabel())) {
                    field.setLabel(defautField.getLabel());
                }
                if (StringUtils.isBlank(field.getType())) {
                    field.setType(defautField.getType());
                }
            }
        }
    }

    /**
     * @return the default list of fields to display if not overriden by the user
     */
    protected abstract List<JIRAField> getDefaultFields();
}
