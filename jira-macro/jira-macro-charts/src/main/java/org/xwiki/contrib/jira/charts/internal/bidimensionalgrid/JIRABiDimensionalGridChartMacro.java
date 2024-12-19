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
package org.xwiki.contrib.jira.charts.internal.bidimensionalgrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.charts.bidimensionalgrid.JIRABiDimensionalGridChartMacroParameter;
import org.xwiki.contrib.jira.charts.internal.JIRAChartDataFetcher;
import org.xwiki.contrib.jira.charts.internal.bidimensionalgrid.source.JIRABiDimensionalGridChartJIRACell;
import org.xwiki.contrib.jira.charts.internal.bidimensionalgrid.source.JIRABiDimensionalGridChartJIRADataSource;
import org.xwiki.contrib.jira.charts.internal.bidimensionalgrid.source.JIRABiDimensionalGridChartJIRARow;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.RawBlock;
import org.xwiki.rendering.block.TableBlock;
import org.xwiki.rendering.block.TableCellBlock;
import org.xwiki.rendering.block.TableHeadCellBlock;
import org.xwiki.rendering.block.TableRowBlock;
import org.xwiki.rendering.macro.AbstractMacro;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.rendering.syntax.Syntax;
import org.xwiki.rendering.transformation.MacroTransformationContext;
import org.xwiki.rendering.transformation.macro.RawBlockFilter;
import org.xwiki.rendering.transformation.macro.RawBlockFilterParameters;

/**
 * Macro displaying a bidimensional table with JIRA issues statistics based on chosen x and y axis fields.
 *
 * @version $Id$
 * @since 10.0
 */
@Component
@Singleton
@Named(JIRABiDimensionalGridChartMacro.MACRO_NAME)
public class JIRABiDimensionalGridChartMacro extends AbstractMacro<JIRABiDimensionalGridChartMacroParameter>
{
    static final String MACRO_NAME = "jiraBiDimensionalGridChart";

    /**
     * The description of the macro.
     */
    private static final String DESCRIPTION =
        "Displays a bidimensional table with JIRA issues statistics based on chosen x and y axis fields.";

    @Inject
    private JIRAChartDataFetcher<JIRABiDimensionalGridChartMacroParameter, JIRABiDimensionalGridChartJIRADataSource>
        dataFetcher;

    @Inject
    @Named("html")
    private RawBlockFilter rawBlockFilter;

    /**
     * Create and initialize the descriptor of the macro.
     */
    public JIRABiDimensionalGridChartMacro()
    {
        super(MACRO_NAME, DESCRIPTION, null, JIRABiDimensionalGridChartMacroParameter.class);
        setDefaultCategories(Set.of(DEFAULT_CATEGORY_CONTENT));
    }

    @Override
    public boolean supportsInlineMode()
    {
        return false;
    }

    @Override
    public List<Block> execute(JIRABiDimensionalGridChartMacroParameter parameters, String content,
        MacroTransformationContext context) throws MacroExecutionException
    {
        JIRABiDimensionalGridChartJIRADataSource dataSource =
            this.dataFetcher.fetch(parameters, JIRABiDimensionalGridChartJIRADataSource.class);

        RawBlockFilterParameters rawBlockFilterParameters = new RawBlockFilterParameters(context);
        rawBlockFilterParameters.setRestricted(true);
        rawBlockFilterParameters.setClean(true);

        List<Block> tableRows = new ArrayList<>();

        tableRows.add(getTableRow(dataSource.getFirstRow(), rawBlockFilterParameters, true, dataSource.getyHeading()));
        for (JIRABiDimensionalGridChartJIRARow row : dataSource.getRows()) {
            tableRows.add(getTableRow(row, rawBlockFilterParameters, false, null));
        }

        TableBlock tableBlock = new TableBlock(tableRows);
        return List.of(tableBlock);
    }

    private TableRowBlock getTableRow(JIRABiDimensionalGridChartJIRARow rowData,
        RawBlockFilterParameters filterParameters, boolean isHead, String headingName)
        throws MacroExecutionException
    {
        List<Block> tableCells = new ArrayList<>();
        if (isHead) {
            tableCells.add(new TableHeadCellBlock(List.of(getCleanRawBlock(headingName, filterParameters))));
        }
        for (JIRABiDimensionalGridChartJIRACell cell : rowData.getCells()) {
            List<Block> listBlock = List.of(getCleanRawBlock(cell.getMarkup(), filterParameters));
            Block cellBlock = (isHead) ? new TableHeadCellBlock(listBlock) : new TableCellBlock(listBlock);
            tableCells.add(cellBlock);
        }
        return new TableRowBlock(tableCells);
    }

    private RawBlock getCleanRawBlock(String value, RawBlockFilterParameters parameters)
        throws MacroExecutionException
    {
        RawBlock rawBlock = new RawBlock(value, Syntax.HTML_5_0);
        return this.rawBlockFilter.filter(rawBlock, parameters);
    }
}
