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
import org.xwiki.contrib.jira.charts.createdvsresolved.JIRACreatedVsResolvedMacroParameters;
import org.xwiki.contrib.jira.charts.internal.JIRADataChartJSDataConverter;
import org.xwiki.contrib.jira.charts.internal.createdvsresolved.source.JIRACreatedVsResolvedDataSource;
import org.xwiki.contrib.jira.macro.internal.JIRAMacroTransformationManager;
import org.xwiki.contrib.jira.macro.internal.source.JIRAServerResolver;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.rendering.transformation.MacroTransformationContext;

/**
 * Displays a line chart based on the created vs resolved query statistics.
 *
 * @version $Id$
 * @since 10.0
 */
@Component
@Named(JIRACreatedVsResolvedMacro.MACRO_NAME)
@Singleton
public class JIRACreatedVsResolvedMacro
    extends AbstractJIRAChartMacro<JIRACreatedVsResolvedMacroParameters, JIRACreatedVsResolvedDataSource>
{
    static final String MACRO_NAME = "jiraCreatedVsResolvedChart";

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

    @Inject
    private JIRAMacroTransformationManager jiraMacroTransformationManager;

    @Inject
    private JIRAServerResolver jiraServerResolver;

    /**
     * Create and initialize the descriptor of the macro.
     */
    public JIRACreatedVsResolvedMacro()
    {
        super(MACRO_NAME, DESCRIPTION, JIRACreatedVsResolvedMacroParameters.class);
        setDefaultCategories(Set.of(DEFAULT_CATEGORY_CONTENT));
    }

    @Override
    public List<Block> execute(JIRACreatedVsResolvedMacroParameters parameters, String content,
        MacroTransformationContext context) throws MacroExecutionException
    {
        String json = getJSONData(parameters, JIRACreatedVsResolvedDataSource.class);
        // TODO: handle displaying the versions
        Map<String, String> chartJSParameterMap = new LinkedHashMap<>();
        chartJSParameterMap.put("type", "line");

        MacroBlock macroBlock = new MacroBlock("chartjs", chartJSParameterMap, json, false);

        return jiraMacroTransformationManager.transform(List.of(macroBlock), parameters, context,
            jiraServerResolver.resolve(parameters), JIRACreatedVsResolvedMacro.MACRO_NAME);
    }

    @Override
    protected JIRAChartDataFetcher<JIRACreatedVsResolvedMacroParameters, JIRACreatedVsResolvedDataSource>
        getDataFetcher()
    {
        return this.dataFetcher;
    }

    @Override
    protected JIRADataChartJSDataConverter<JIRACreatedVsResolvedDataSource, JIRACreatedVsResolvedMacroParameters>
        getConverter()
    {
        return this.converter;
    }
}
