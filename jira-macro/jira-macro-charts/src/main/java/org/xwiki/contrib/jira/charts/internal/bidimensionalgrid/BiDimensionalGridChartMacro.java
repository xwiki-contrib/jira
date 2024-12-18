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
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.charts.bidimensionalgrid.BiDimensionGridChartMacroParameter;
import org.xwiki.contrib.jira.charts.internal.JIRAChartDataFetcher;
import org.xwiki.contrib.jira.charts.internal.bidimensionalgrid.source.BiDimensionalGridChartJIRACell;
import org.xwiki.contrib.jira.charts.internal.bidimensionalgrid.source.BiDimensionalGridChartJIRADataSource;
import org.xwiki.contrib.jira.charts.internal.bidimensionalgrid.source.BiDimensionalGridChartJIRARow;
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

@Component
@Singleton
@Named("jiraBiDimensionalGridChart")
public class BiDimensionalGridChartMacro extends AbstractMacro<BiDimensionGridChartMacroParameter>
{
    /**
     * The description of the macro.
     */
    private static final String DESCRIPTION =
        "Displays a line chart displaying the created vs resolved issues based on the performed query.";

    @Inject
    private JIRAChartDataFetcher<BiDimensionGridChartMacroParameter, BiDimensionalGridChartJIRADataSource> dataFetcher;

    @Inject
    @Named("html")
    private RawBlockFilter rawBlockFilter;

    /**
     * Create and initialize the descriptor of the macro.
     */
    public BiDimensionalGridChartMacro()
    {
        super("jiraBiDimensionalGridChart", DESCRIPTION, null, BiDimensionGridChartMacroParameter.class);
        setDefaultCategories(Collections.singleton(DEFAULT_CATEGORY_CONTENT));
    }

    @Override
    public boolean supportsInlineMode()
    {
        return false;
    }

    @Override
    public List<Block> execute(BiDimensionGridChartMacroParameter parameters, String content,
        MacroTransformationContext context) throws MacroExecutionException
    {
        BiDimensionalGridChartJIRADataSource dataSource =
            this.dataFetcher.fetch(parameters, BiDimensionalGridChartJIRADataSource.class);

        RawBlockFilterParameters rawBlockFilterParameters = new RawBlockFilterParameters(context);
        rawBlockFilterParameters.setRestricted(true);
        rawBlockFilterParameters.setClean(true);

        List<Block> tableRows = new ArrayList<>();

        tableRows.add(getTableRow(dataSource.getFirstRow(), rawBlockFilterParameters, true, dataSource.getyHeading()));
        for (BiDimensionalGridChartJIRARow row : dataSource.getRows()) {
            tableRows.add(getTableRow(row, rawBlockFilterParameters, false, null));
        }

        TableBlock tableBlock = new TableBlock(tableRows);
        return Collections.singletonList(tableBlock);
    }

    private TableRowBlock getTableRow(BiDimensionalGridChartJIRARow rowData,
        RawBlockFilterParameters filterParameters, boolean isHead, String headingName)
        throws MacroExecutionException
    {
        List<Block> tableCells = new ArrayList<>();
        if (isHead) {
            tableCells.add(new TableHeadCellBlock(List.of(getCleanRawBlock(headingName, filterParameters))));
        }
        for (BiDimensionalGridChartJIRACell cell : rowData.getCells()) {
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
