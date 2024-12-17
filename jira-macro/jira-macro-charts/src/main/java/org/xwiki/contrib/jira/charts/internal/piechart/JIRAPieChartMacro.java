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
package org.xwiki.contrib.jira.charts.internal.piechart;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.charts.internal.JIRAChartDataFetcher;
import org.xwiki.contrib.jira.charts.internal.JIRADataChartJSDataConverter;
import org.xwiki.contrib.jira.charts.internal.display.ChartJSDataSource;
import org.xwiki.contrib.jira.charts.internal.piechart.source.JIRAPieChartDataSource;
import org.xwiki.contrib.jira.charts.piechart.JIRAPieChartMacroParameters;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.macro.AbstractMacro;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.rendering.transformation.MacroTransformationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Displays a pie chart based on the performed query.
 *
 * @version $Id$
 * @since 9.1
 */
@Component
@Named("jiraPieChart")
@Singleton
public class JIRAPieChartMacro extends AbstractMacro<JIRAPieChartMacroParameters>
{
    /**
     * The description of the macro.
     */
    private static final String DESCRIPTION =
        "Displays a pie chart based on the performed query.";

    @Inject
    private JIRAChartDataFetcher<JIRAPieChartMacroParameters, JIRAPieChartDataSource> dataFetcher;

    @Inject
    private JIRADataChartJSDataConverter<JIRAPieChartDataSource, JIRAPieChartMacroParameters> converter;

    /**
     * Create and initialize the descriptor of the macro.
     */
    public JIRAPieChartMacro()
    {
        super("jiraPieChart", DESCRIPTION, null, JIRAPieChartMacroParameters.class);
        setDefaultCategories(Collections.singleton(DEFAULT_CATEGORY_CONTENT));
    }

    @Override
    public boolean supportsInlineMode()
    {
        return false;
    }

    @Override
    public List<Block> execute(JIRAPieChartMacroParameters parameters, String content,
        MacroTransformationContext context) throws MacroExecutionException
    {
        // TODO: introduce a maximum number of elements to be displayed in the piechart (default should be 10)
        JIRAPieChartDataSource dataSource = this.dataFetcher.fetch(parameters, JIRAPieChartDataSource.class);
        ChartJSDataSource convert = this.converter.convert(dataSource, parameters);
        String json;
        try {
            json = new ObjectMapper().writeValueAsString(convert);
        } catch (JsonProcessingException e) {
            throw new MacroExecutionException("Error when transforming data to JSON", e);
        }

        Map<String, String> chartJSParameterMap = new LinkedHashMap<>();
        chartJSParameterMap.put("type", "pie");

        MacroBlock macroBlock = new MacroBlock("chartjs", chartJSParameterMap, json, false);

        return Collections.singletonList(macroBlock);
    }
}
