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
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.xwiki.text.XWikiToStringBuilder;

/**
 * Represents a JIRA Field (id, label to display for displayer needing a label, type of the field, e.g.
 * {@code text}, {@code date}, {@code url}).
 *
 * @version $Id$
 * @since 8.3
 */
public class JIRAField
{
    /**
     * JIRA Summary field.
     */
    public static final JIRAField SUMMARY = new JIRAField("summary", "Summary", "text");

    /**
     * JIRA Key field (eg "XWIKI-1000").
     */
    public static final JIRAField KEY = new JIRAField("key", "Key", "text");

    /**
     * JIRA Type field (eg Bug, Improvement, etc).
     */
    public static final JIRAField TYPE = new JIRAField("type", "Type", "text");

    /**
     * JIRA Status field (eg Closed, Open, etc).
     */
    public static final JIRAField STATUS = new JIRAField("status", "Status", "text");

    /**
     * JIRA Assignee field (the person assigned to fix the issue).
     */
    public static final JIRAField ASSIGNEE = new JIRAField("assignee", "Assignee", "text");

    /**
     * JIRA Reporter field (the person who reported the issue).
     */
    public static final JIRAField REPORTER = new JIRAField("reporter", "Reporter", "text");

    /**
     * JIRA Created date field (the date the issue was created).
     */
    public static final JIRAField CREATED = new JIRAField("created", "Created Date", "date");

    /**
     * JIRA Updated date field (the date the issue was last modified).
     */
    public static final JIRAField UPDATED = new JIRAField("updated", "Updated Date", "date");

    /**
     * JIRA Resolved date field (the date the issue was resolved).
     */
    public static final JIRAField RESOLVED = new JIRAField("updated", "Resolved Date", "date");

    /**
     * JIRA Fix Version field (the version in which the issue was resolved or closed).
     */
    public static final JIRAField FIXVERSION = new JIRAField("fixVersion", "Fixed in", "text");

    /**
     * JIRA Affected Versions field (the list of Versions for which the issue was reported).
     */
    public static final JIRAField VERSION = new JIRAField("version", "Affected Versions", "text");

    /**
     * JIRA Component field (the list of domains/categories for the issue).
     */
    public static final JIRAField COMPONENT = new JIRAField("component", "Component", "text");

    /**
     * JIRA Vote field (the number of votes for the issue).
     */
    public static final JIRAField VOTES = new JIRAField("votes", "Votes", "number");

    /**
     * JIRA Resolution field (eg Closed, Won't Fix, Duplicate; etc).
     */
    public static final JIRAField RESOLUTION = new JIRAField("resolution", "Resolution", "text");

    /**
     * JIRA link field (the URL to the issue on the JIRA instance).
     */
    public static final JIRAField LINK = new JIRAField("link", "Link", "url");

    /**
     * Special field used by the List Data Source which allows the user to define notes for a given issue.
     */
    public static final String NOTE = "note";

    public static final Map<String, JIRAField> DEFAULT_FIELDS = new HashMap<String, JIRAField>() {{
        put(SUMMARY.getId(), SUMMARY);
        put(KEY.getId(), KEY);
        put(TYPE.getId(), TYPE);
        put(STATUS.getId(), STATUS);
        put(ASSIGNEE.getId(), ASSIGNEE);
        put(REPORTER.getId(), REPORTER);
        put(CREATED.getId(), CREATED);
        put(UPDATED.getId(), UPDATED);
        put(RESOLVED.getId(), RESOLVED);
        put(FIXVERSION.getId(), FIXVERSION);
        put(VERSION.getId(), VERSION);
        put(COMPONENT.getId(), COMPONENT);
        put(VOTES.getId(), VOTES);
        put(RESOLUTION.getId(), RESOLUTION);
        put(LINK.getId(), LINK);
    }};

    private String id;

    private String label;

    private String type;

    /**
     * @param id see {@link #getId()}
     */
    public JIRAField(String id)
    {
        this.id = id;
    }

    /**
     * @param id see {@link #getId()}
     * @param label see {@link #getLabel()}
     * @param type see {@link #getType()}
     */
    public JIRAField(String id, String label, String type)
    {
        this(id);
        setLabel(label);
        setType(type);
    }

    /**
     * @return the field id as represented by JIRA. For custom fields, this is the custom field name.
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * @return the optional label (pretty name) representing the field, to be used by displayers if they need it
     */
    public String getLabel()
    {
        return this.label;
    }

    /**
     * @param label see {@link #getLabel()}
     */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
     * @return the optional type of the field (e.g. {@code text}, {@code date}, {@code url}), which is used to locate a
     *         field displayer when no specific displayer is found for the field id. If no type is specified then the
     *         default field displayer is used
     */
    public String getType()
    {
        return this.type;
    }

    /**
     * @param type see {@link #getType()}
     */
    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(1, 5)
            .append(getId())
            .append(getLabel())
            .append(getType())
            .toHashCode();
    }

    @Override
    public boolean equals(Object object)
    {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object.getClass() != getClass()) {
            return false;
        }
        JIRAField rhs = (JIRAField) object;
        return new EqualsBuilder()
            .append(getId(), rhs.getId())
            .append(getLabel(), rhs.getLabel())
            .append(getType(), rhs.getType())
            .isEquals();
    }

    @Override
    public String toString()
    {
        ToStringBuilder builder = new XWikiToStringBuilder(this);
        builder.append("id", getId());
        builder.append("label", getLabel());
        builder.append("type", getType());
        return builder.toString();
    }
}
