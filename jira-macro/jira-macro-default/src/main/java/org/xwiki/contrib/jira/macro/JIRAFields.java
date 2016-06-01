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

/**
 * Definition of JIRA field ids.
 *
 * @version $Id$
 * @since 4.2M1
 */
public interface JIRAFields
{
    /**
     * JIRA Summary field.
     */
    JIRAField SUMMARY = new JIRAField("summary", "Summary", "text");

    /**
     * JIRA Key field (eg "XWIKI-1000").
     */
    JIRAField KEY = new JIRAField("key", "Key", "text");

    /**
     * JIRA Type field (eg Bug, Improvement, etc).
     */
    JIRAField TYPE = new JIRAField("type", "Type", "text");

    /**
     * JIRA Status field (eg Closed, Open, etc).
     */
    JIRAField STATUS = new JIRAField("status", "Status", "text");

    /**
     * JIRA Assignee field (the person assigned to fix the issue).
     */
    JIRAField ASSIGNEE = new JIRAField("assignee", "Assignee", "text");

    /**
     * JIRA Reporter field (the person who reported the issue).
     */
    JIRAField REPORTER = new JIRAField("reporter", "Reporter", "text");

    /**
     * JIRA Created date field (the date the issue was created).
     */
    JIRAField CREATED = new JIRAField("created", "Created Date", "date");

    /**
     * JIRA Updated date field (the date the issue was last modified).
     */
    JIRAField UPDATED = new JIRAField("updated", "Updated Date", "date");

    /**
     * JIRA Resolved date field (the date the issue was resolved).
     */
    JIRAField RESOLVED = new JIRAField("updated", "Resolved Date", "date");

    /**
     * JIRA Fix Version field (the version in which the issue was resolved or closed).
     */
    JIRAField FIXVERSION = new JIRAField("fixVersion", "Fixed in", "text");

    /**
     * JIRA Affected Versions field (the list of Versions for which the issue was reported).
     */
    JIRAField VERSION = new JIRAField("version", "Affected Versions", "text");

    /**
     * JIRA Component field (the list of domains/categories for the issue).
     */
    JIRAField COMPONENT = new JIRAField("component", "Component", "text");

    /**
     * JIRA Vote field (the number of votes for the issue).
     */
    JIRAField VOTES = new JIRAField("votes", "Votes", "number");

    /**
     * JIRA Resolution field (eg Closed, Won't Fix, Duplicate; etc).
     */
    JIRAField RESOLUTION = new JIRAField("resolution", "Resolution", "text");

    /**
     * JIRA link field (the URL to the issue on the JIRA instance).
     */
    JIRAField LINK = new JIRAField("link", "Link", "url");

    /**
     * Special field used by the List Data Source which allows the user to define notes for a given issue.
     */
    String NOTE = "note";

    Map<String, JIRAField> DEFAULT_FIELDS = new HashMap<String, JIRAField>() {{
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
}
