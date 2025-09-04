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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.macro.JIRABadRequestException;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.util.ErrorBlockGenerator;

/**
 * Generate error block with nice information for users.
 *
 * @version $Id$
 * @since 11.1.0
 */
@Component(roles = { JIRAErrorGenerator.class })
@Singleton
public class JIRAErrorGenerator
{
    @Inject
    private ErrorBlockGenerator errorBlockGenerator;

    /**
     * Generate an error block following the information provided by the {@link JIRABadRequestException}.
     *
     * @param e the exception.
     * @param inline if the block should be generated inline.
     * @return the error block.
     */
    public List<Block> getBadRequestErrorBlock(JIRABadRequestException e, boolean inline)
    {
        return errorBlockGenerator.generateErrorBlocks(inline, "jira.macro.common.jirabadrequest.errormessage",
            "JIRA returned an error of the request. "
                + "This could be related to the JQL query or the a rights limitation in JIRA for the XWiki user.",
            "Here are the error message from JIRA:\n" + String.join("\n", e.getExtractedMessages()));
    }
}
