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
package org.xwiki.contrib.jira.charts.internal.createdvsresolved;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.charts.internal.JIRAChartDataFetcher;
import org.xwiki.contrib.jira.charts.createdvsresolved.JIRACreatedVsResolvedMacroParameters;
import org.xwiki.contrib.jira.charts.internal.JIRADataChartJSDataConverter;
import org.xwiki.contrib.jira.charts.internal.createdvsresolved.source.JIRACreatedVsResolvedDataSource;
import org.xwiki.contrib.jira.charts.internal.display.ChartJSDataSource;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.macro.AbstractMacro;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.rendering.transformation.MacroTransformationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Displays a line chart based on the created vs resolved query statistics.
 *
 * @version $Id$
 * @since 9.1
 */
@Component
@Named("jiraCreatedVsResolved")
@Singleton
public class JIRACreatedVsResolvedMacro extends AbstractMacro<JIRACreatedVsResolvedMacroParameters>
{
    /**
     * The description of the macro.
     */
    private static final String DESCRIPTION =
        "Displays a line chart displaying the created vs resolved issues based on the performed query.";

    @Inject
    private JIRAChartDataFetcher<JIRACreatedVsResolvedMacroParameters, JIRACreatedVsResolvedDataSource> dataFetcher;

    @Inject
    private JIRADataChartJSDataConverter<JIRACreatedVsResolvedDataSource, JIRACreatedVsResolvedMacroParameters>
        converter;

    /**
     * Create and initialize the descriptor of the macro.
     */
    public JIRACreatedVsResolvedMacro()
    {
        super("jiraCreatedVsResolved", DESCRIPTION, null, JIRACreatedVsResolvedMacroParameters.class);
        setDefaultCategories(Collections.singleton(DEFAULT_CATEGORY_CONTENT));
    }

    @Override
    public boolean supportsInlineMode()
    {
        return false;
    }

    @Override
    public List<Block> execute(JIRACreatedVsResolvedMacroParameters parameters, String content,
        MacroTransformationContext context) throws MacroExecutionException
    {
        JIRACreatedVsResolvedDataSource dataSource =
            this.dataFetcher.fetch(parameters, JIRACreatedVsResolvedDataSource.class);

        ChartJSDataSource convert = this.converter.convert(dataSource, parameters);
        String json;
        try {
            json = new ObjectMapper().writeValueAsString(convert);
        } catch (JsonProcessingException e) {
            throw new MacroExecutionException("Error when transforming data to JSON", e);
        }

        // TODO: handle displaying the versions
        Map<String, String> chartJSParameterMap = new LinkedHashMap<>();
        chartJSParameterMap.put("type", "line");

        MacroBlock macroBlock = new MacroBlock("chartjs", chartJSParameterMap, json, false);

        return Collections.singletonList(macroBlock);
    }
}
