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
    public static final JIRAField SUMMARY;

    /**
     * JIRA Key field (eg "XWIKI-1000").
     */
    public static final JIRAField KEY;

    /**
     * JIRA Type field (eg Bug, Improvement, etc).
     */
    public static final JIRAField TYPE;

    /**
     * JIRA Status field (eg Closed, Open, etc).
     */
    public static final JIRAField STATUS;

    /**
     * JIRA Assignee field (the person assigned to fix the issue).
     */
    public static final JIRAField ASSIGNEE;

    /**
     * JIRA Reporter field (the person who reported the issue).
     */
    public static final JIRAField REPORTER;

    /**
     * JIRA Created date field (the date the issue was created).
     */
    public static final JIRAField CREATED;

    /**
     * JIRA Updated date field (the date the issue was last modified).
     */
    public static final JIRAField UPDATED;

    /**
     * JIRA Resolved date field (the date the issue was resolved).
     */
    public static final JIRAField RESOLVED;

    /**
     * JIRA Fix Version field (the version in which the issue was resolved or closed).
     */
    public static final JIRAField FIXVERSION;

    /**
     * JIRA Affected Versions field (the list of Versions for which the issue was reported).
     */
    public static final JIRAField VERSION;

    /**
     * JIRA Component field (the list of domains/categories for the issue).
     */
    public static final JIRAField COMPONENT;

    /**
     * JIRA Vote field (the number of votes for the issue).
     */
    public static final JIRAField VOTES;

    /**
     * JIRA Resolution field (eg Closed, Won't Fix, Duplicate; etc).
     */
    public static final JIRAField RESOLUTION;

    /**
     * JIRA link field (the URL to the issue on the JIRA instance).
     */
    public static final JIRAField LINK;

    /**
     * JIRA Labels field.
     */
    public static final JIRAField LABELS;

    /**
     * Special field used by the List Data Source which allows the user to define notes for a given issue.
     */
    public static final String NOTE = "note";

    /**
     * Map of all known JIRA fields (with their id, label and type).
     */
    public static final Map<String, JIRAField> DEFAULT_FIELDS = new HashMap<>();

    private static final String TEXT_TYPE = "text";

    private static final String DATE_TYPE = "date";

    private static final String URL_TYPE = "url";

    private static final String LIST_TYPE = "list";

    private static final String NUMBER_TYPE = "number";

    private static final String TYPE_ID = "type";

    static {
        SUMMARY = new JIRAField("summary", "Summary", TEXT_TYPE);
        KEY = new JIRAField("key", "Key", TEXT_TYPE);
        TYPE = new JIRAField(TYPE_ID, "Type", TEXT_TYPE);
        STATUS = new JIRAField("status", "Status", TEXT_TYPE);
        ASSIGNEE = new JIRAField("assignee", "Assignee", TEXT_TYPE);
        REPORTER = new JIRAField("reporter", "Reporter", TEXT_TYPE);
        CREATED = new JIRAField("created", "Created Date", DATE_TYPE);
        UPDATED = new JIRAField("updated", "Updated Date", DATE_TYPE);
        RESOLVED = new JIRAField("resolved", "Resolved Date", DATE_TYPE);
        FIXVERSION = new JIRAField("fixVersion", "Fixed in", TEXT_TYPE);
        VERSION = new JIRAField("version", "Affected Versions", TEXT_TYPE);
        COMPONENT = new JIRAField("component", "Component", TEXT_TYPE);
        VOTES = new JIRAField("votes", "Votes", NUMBER_TYPE);
        RESOLUTION = new JIRAField("resolution", "Resolution", TEXT_TYPE);
        LINK = new JIRAField("link", "Link", URL_TYPE);
        LABELS = new JIRAField("labels", "Labels", LIST_TYPE);
    }

    static {
        DEFAULT_FIELDS.put(SUMMARY.getId(), SUMMARY);
        DEFAULT_FIELDS.put(KEY.getId(), KEY);
        DEFAULT_FIELDS.put(TYPE.getId(), TYPE);
        DEFAULT_FIELDS.put(STATUS.getId(), STATUS);
        DEFAULT_FIELDS.put(ASSIGNEE.getId(), ASSIGNEE);
        DEFAULT_FIELDS.put(REPORTER.getId(), REPORTER);
        DEFAULT_FIELDS.put(CREATED.getId(), CREATED);
        DEFAULT_FIELDS.put(UPDATED.getId(), UPDATED);
        DEFAULT_FIELDS.put(RESOLVED.getId(), RESOLVED);
        DEFAULT_FIELDS.put(FIXVERSION.getId(), FIXVERSION);
        DEFAULT_FIELDS.put(VERSION.getId(), VERSION);
        DEFAULT_FIELDS.put(COMPONENT.getId(), COMPONENT);
        DEFAULT_FIELDS.put(VOTES.getId(), VOTES);
        DEFAULT_FIELDS.put(RESOLUTION.getId(), RESOLUTION);
        DEFAULT_FIELDS.put(LINK.getId(), LINK);
        DEFAULT_FIELDS.put(LABELS.getId(), LABELS);
    }

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
        builder.append(TYPE_ID, getType());
        return builder.toString();
    }
}
