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
package org.xwiki.contrib.jira.macro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xwiki.properties.annotation.PropertyDescription;
import org.xwiki.properties.annotation.PropertyMandatory;

/**
 * Parameters for the {@link org.xwiki.contrib.jira.macro.internal.JIRAMacro} Macro.
 *
 * @version $Id$
 * @since 4.2M1
 */
public class JIRAMacroParameters
{
    /**
     * @see #getDefaultFieldNames()
     */
    private static final Map<String, String> FIELD_NAMES = new HashMap<String, String>();

    /**
     * @see #getDefaultFieldNames()
     */
    static {
        FIELD_NAMES.put(JIRAFields.SUMMARY, "Summary");
        FIELD_NAMES.put(JIRAFields.KEY, "Key");
        FIELD_NAMES.put(JIRAFields.TYPE, "Type");
        FIELD_NAMES.put(JIRAFields.STATUS, "Status");
        FIELD_NAMES.put(JIRAFields.ASSIGNEE, "Assignee");
        FIELD_NAMES.put(JIRAFields.REPORTER, "Reporter");
        FIELD_NAMES.put(JIRAFields.CREATED, "Created Date");
        FIELD_NAMES.put(JIRAFields.UPDATED, "Updated Date");
        FIELD_NAMES.put(JIRAFields.RESOLVED, "Resolved Date");
        FIELD_NAMES.put(JIRAFields.FIXVERSION, "Fixed In");
        FIELD_NAMES.put(JIRAFields.COMPONENT, "Component");
        FIELD_NAMES.put(JIRAFields.VOTES, "Votes");
        FIELD_NAMES.put(JIRAFields.RESOLUTION, "Resolution");
        FIELD_NAMES.put(JIRAFields.LINK, "Link");
        FIELD_NAMES.put(JIRAFields.VERSION, "Affected Versions");
    }

    /**
     * @see #getURL()
     */
    private String url;

    /**
     * @see #getSource()
     */
    private String source = "list";

    /**
     * @see #getStyle()
     */
    private String style = "table";

    /**
     * @see #getFields()
     */
    private List<String> fields;

    /**
     * @see #getFieldNames()
     */
    private List<String> fieldNames;

    /**
     * @see #getId()
     */
    private String id;

    /**
     * @param url see {@link #getURL()}
     */
    @PropertyDescription("the JIRA Server URL")
    public void setURL(String url)
    {
        this.url = url;
    }

    /**
     * @return the JIRA Server URL (e.g. {@code http://jira.xwiki.org})
     */
    public String getURL()
    {
        return this.url;
    }

    /**
     * @param id see {@link #getId()}
     */
    @PropertyDescription("the configuration id of the JIRA Server URL to use")
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * @return the configuration id of the JIRA Server URL to use (defined in the Macro configuration settings). Note
     *         that if a URL is specified it'll take precedence over this parameter. If no URL and no id is specified
     *         then the default id from the configuration will be used (if any)
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * @param source see {@link #getSource()}
     */
    @PropertyDescription("how JIRA issues are defined (e.g. \"jql\", \"list\")")
    public void setSource(String source)
    {
        this.source = source;
    }

    /**
     * @return the hint of the data source to use to fetch JIRA issues
     */
    public String getSource()
    {
        return this.source;
    }

    /**
     * @param style see {@link #getStyle()}
     */
    @PropertyDescription("how JIRA issues are displayed (e.g. \"table\", \"list\", \"enum\")")
    public void setStyle(String style)
    {
        this.style = style;
    }

    /**
     * @return the hint of the Displayer to use to display JIRA issues
     */
    public String getStyle()
    {
        return this.style;
    }

    /**
     * @return the default field names (used for example by the Table displayer as table headers)
     */
    public Map<String, String> getDefaultFieldNames()
    {
        return FIELD_NAMES;
    }

    /**
     * @param fields see {@link #getFields()}
     */
    @PropertyDescription(
        "the fields to be displayed (default field list depends on the style used)")
    public void setFields(List<String> fields)
    {
        this.fields = fields;
    }

    /**
     * @return the list of JIRA fields to display (if not defined, a default list of fields defined by the chosen
     *         Displayer will be used)
     */
    public List<String> getFields()
    {
        return this.fields;
    }

    /**
     * @param fieldNames see {@link #getFieldNames()}
     */
    @PropertyDescription("the pretty names of the fields in the order in which they are displayed")
    public void setFieldNames(List<String> fieldNames)
    {
        this.fieldNames = fieldNames;
    }

    /**
     * @return the names to use for JIRA issue fields for Displayers displaying the field names (eg the Table
     *         Data Source)
     */
    public List<String> getFieldNames()
    {
        return this.fieldNames;
    }
}
