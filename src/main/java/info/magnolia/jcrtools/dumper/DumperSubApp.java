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
package info.magnolia.jcrtools.dumper;

import info.magnolia.cms.util.DumperUtil;
import info.magnolia.commands.CommandsManager;
import info.magnolia.context.Context;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.jcrtools.JcrToolsBaseSubApp;
import info.magnolia.jcrtools.JcrToolsConstants;
import info.magnolia.jcrtools.JcrToolsResultView;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.dialog.formdialog.FormBuilder;
import info.magnolia.ui.vaadin.form.FormViewReduced;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.vaadin.data.Item;

/**
 * Sub app that creates a dump of a given workspace using {@link DumperUtil}.
 */
public class DumperSubApp extends JcrToolsBaseSubApp {
    private final Context context;
    private final JcrToolsResultView view;
    private final SimpleTranslator i18n;
    private final UiContext uiContext;

    @Inject
    public DumperSubApp(final SubAppContext subAppContext, final FormViewReduced formView, final JcrToolsResultView view,
                        final FormBuilder builder, final CommandsManager commandsManager, final Context context,
                        final UiContext uiContext, final SimpleTranslator i18n) {
        super(subAppContext, formView, view, builder, commandsManager);
        this.context = context;
        this.view = view;
        this.i18n = i18n;
        this.uiContext = uiContext;
    }

    @Override
    public void onActionTriggered() {
        super.onActionTriggered();
        if (formView.isValid()) {
            final Item item = getItem();
            String levelString = item.getItemProperty(JcrToolsConstants.LEVEL_STRING).getValue().toString();
            String workspace = item.getItemProperty(JcrToolsConstants.REPOSITORY).getValue().toString();

            dump(workspace, Integer.parseInt(levelString));
        }
    }

    // get nodes at depth level
    private void dump(final String workspace, final int level) {
        try {
            // print out the user-specified workspace to user-specified depth
            Session session = context.getJCRSession(workspace);
            Node rootSession = session.getRootNode();

            String nodes = DumperUtil.dump(rootSession, level);
            view.setResult(nodes);
            uiContext.openNotification(MessageStyleTypeEnum.INFO, true, i18n.translate("jcr-tools.dumper.dumpSuccessMessage"));
        } catch (RepositoryException re) {
            log.error("Could not create dump for workspace '{}'.", workspace, re);
            uiContext.openNotification(MessageStyleTypeEnum.ERROR, true, i18n.translate("jcr-tools.dumper.dumpFailedMessage"));
        }
    }
}
