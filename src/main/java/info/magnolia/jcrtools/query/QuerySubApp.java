/**
 * This file Copyright (c) 2015 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This file is dual-licensed under both the Magnolia
 * Network Agreement and the GNU General Public License.
 * You may elect to use one or the other of these licenses.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or MNA you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the Magnolia Network Agreement (MNA), this file
 * and the accompanying materials are made available under the
 * terms of the MNA which accompanies this distribution, and
 * is available at http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.jcrtools.query;

import info.magnolia.cms.util.QueryUtil;
import info.magnolia.commands.CommandsManager;
import info.magnolia.jcrtools.JcrToolsBaseSubApp;
import info.magnolia.jcrtools.JcrToolsConstants;
import info.magnolia.jcrtools.JcrToolsResultView;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.dialog.formdialog.FormBuilder;
import info.magnolia.ui.vaadin.form.FormViewReduced;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;

import com.vaadin.data.Item;

/**
 * QuerySubApp prints the results of a JCR query executed via {@link QueryUtil}.
 */
public class QuerySubApp extends JcrToolsBaseSubApp {
    private final JcrToolsResultView view;

    @Inject
    public QuerySubApp(final SubAppContext subAppContext, final FormViewReduced formView, final JcrToolsResultView view,
                       final FormBuilder builder, final CommandsManager commandsManager) {
        super(subAppContext, formView, view, builder, commandsManager);
        this.view = view;
    }

    @Override
    public void onActionTriggered() {
        super.onActionTriggered();
        if (formView.isValid()) {
            final Item item = getItem();
            String queryLanguage = item.getItemProperty(JcrToolsConstants.QUERY_LANGUAGE).getValue().toString();
            String workspace = item.getItemProperty(JcrToolsConstants.REPOSITORY).getValue().toString();
            String resultItemType = item.getItemProperty(JcrToolsConstants.RESULT_ITEM_TYPE).getValue().toString();
            String statement = item.getItemProperty(JcrToolsConstants.STATEMENT).getValue().toString();

            doQuery(workspace, statement, queryLanguage, resultItemType);
        }
    }

    private void doQuery(final String workspace, final String statement, final String queryLanguage, final String resultItemType) {
        final long start = System.currentTimeMillis();

        final StringBuilder sb = new StringBuilder();
        NodeIterator iterator;
        int count = 0;

        try {
            iterator = QueryUtil.search(workspace, statement, queryLanguage, resultItemType);

            while (iterator.hasNext()) {
                Node node = iterator.nextNode();

                sb.append(node.getPath()); // throws RepositoryException
                sb.append("\n");

                count++;
            }
        } catch (Throwable e) {
            final String result = e.getMessage() != null ? e.getMessage() : e.toString();
            log.error(result, e);
        }

        sb.insert(0, count + " nodes returned in " + (System.currentTimeMillis() - start) + "ms\n");

        view.setResult(sb.toString());
    }
}
