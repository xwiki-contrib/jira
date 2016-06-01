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
