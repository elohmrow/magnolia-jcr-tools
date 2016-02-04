/**
 * This file Copyright (c) 2015-2016 Magnolia International
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
package info.magnolia.jcrtools;

import info.magnolia.commands.CommandsManager;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.dialog.formdialog.FormBuilder;
import info.magnolia.ui.form.definition.FormDefinition;
import info.magnolia.ui.framework.app.BaseSubApp;
import info.magnolia.ui.vaadin.form.FormViewReduced;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.util.PropertysetItem;

/**
 * Jcr tools sub app base class.
 *
 * @see info.magnolia.jcrtools.JcrToolsView.Listener
 */
public class JcrToolsBaseSubApp extends BaseSubApp implements JcrToolsView.Listener {
    protected static final Logger log = LoggerFactory.getLogger(JcrToolsBaseSubApp.class);

    protected final FormViewReduced formView;
    protected final JcrToolsView view;
    protected final FormBuilder builder;
    protected final FormDefinition formDefinition;
    protected final CommandsManager commandsManager;

    private Item item;

    @Inject
    public JcrToolsBaseSubApp(final SubAppContext subAppContext, final FormViewReduced formView, final JcrToolsView view,
                              final FormBuilder builder, final CommandsManager commandsManager) {
        super(subAppContext, view);
        this.formView = formView;
        this.view = view;
        this.builder = builder;
        this.commandsManager = commandsManager;
        this.formDefinition = ((JcrToolsSubAppDescriptor) subAppContext.getSubAppDescriptor()).getForm();
    }

    @Override
    protected void onSubAppStart() {
        item = new PropertysetItem();

        builder.buildReducedForm(formDefinition, formView, item, null);
        view.setFormView(formView);
        view.setListener(this);
    }

    @Override
    public void onActionTriggered() {
        formView.showValidation(true);
    }

    public Item getItem() {
        return item;
    }

}