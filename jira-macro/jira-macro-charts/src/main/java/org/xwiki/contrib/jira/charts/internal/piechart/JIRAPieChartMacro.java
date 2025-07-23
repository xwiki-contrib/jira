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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.charts.internal.AbstractJIRAChartMacro;
import org.xwiki.contrib.jira.charts.internal.JIRAChartDataFetcher;
import org.xwiki.contrib.jira.charts.internal.JIRADataChartJSDataConverter;
import org.xwiki.contrib.jira.charts.internal.piechart.source.JIRAPieChartDataSource;
import org.xwiki.contrib.jira.charts.piechart.JIRAPieChartMacroParameters;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.rendering.transformation.MacroTransformationContext;

/**
 * Displays a pie chart based on the performed query.
 *
 * @version $Id$
 * @since 10.0
 */
@Component
@Named(JIRAPieChartMacro.MACRO_NAME)
@Singleton
public class JIRAPieChartMacro extends AbstractJIRAChartMacro<JIRAPieChartMacroParameters, JIRAPieChartDataSource>
{
    static final String MACRO_NAME = "jiraPieChart";

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
        super(MACRO_NAME, DESCRIPTION, JIRAPieChartMacroParameters.class);
        setDefaultCategories(Set.of(DEFAULT_CATEGORY_CONTENT));
    }


    @Override
    protected JIRAChartDataFetcher<JIRAPieChartMacroParameters, JIRAPieChartDataSource> getDataFetcher()
    {
        return this.dataFetcher;
    }

    @Override
    protected JIRADataChartJSDataConverter<JIRAPieChartDataSource, JIRAPieChartMacroParameters> getConverter()
    {
        return this.converter;
    }

    @Override
    public List<Block> execute(JIRAPieChartMacroParameters parameters, String content,
        MacroTransformationContext context) throws MacroExecutionException
    {
        String json = this.getJSONData(parameters, JIRAPieChartDataSource.class);
        Map<String, String> chartJSParameterMap = new LinkedHashMap<>();
        chartJSParameterMap.put("type", "pie");

        MacroBlock macroBlock = new MacroBlock("chartjs", chartJSParameterMap, json, false);

        return transformMacroResult(parameters, context, macroBlock, JIRAPieChartMacro.MACRO_NAME);
    }
}
