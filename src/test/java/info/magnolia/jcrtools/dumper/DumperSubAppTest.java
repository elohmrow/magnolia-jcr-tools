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

import static org.mockito.Mockito.*;

import info.magnolia.commands.CommandsManager;
import info.magnolia.context.MgnlContext;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.jcrtools.ConfiguredJcrToolsSubAppDescriptor;
import info.magnolia.jcrtools.JcrToolsConstants;
import info.magnolia.jcrtools.JcrToolsResultView;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.test.mock.MockContext;
import info.magnolia.test.mock.jcr.MockSession;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.location.Location;
import info.magnolia.ui.dialog.formdialog.FormBuilder;
import info.magnolia.ui.form.definition.ConfiguredFormDefinition;
import info.magnolia.ui.form.definition.FormDefinition;
import info.magnolia.ui.form.field.factory.FieldFactoryFactory;
import info.magnolia.ui.framework.app.SubAppContextImpl;
import info.magnolia.ui.framework.i18n.DefaultI18NAuthoringSupport;
import info.magnolia.ui.vaadin.form.FormViewReduced;

import javax.jcr.Node;

import org.junit.Test;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;

/**
 * Tests for the {@link DumperSubApp}.
 */
public class DumperSubAppTest {
    public static final String workspace = "workspace";
    public static final String level = "2";
    public static final String path = "path";

    ConfiguredJcrToolsSubAppDescriptor subAppDescriptor;
    FormDefinition formDefinition;

    private FormViewReduced formView;
    private SubAppContext subAppContext;
    private JcrToolsResultView view;
    private FormBuilder builder;
    private CommandsManager commandsManager;
    private UiContext uiContext;
    private SimpleTranslator i18n;
    private DumperSubApp dumperSubApp;

    @Test
    public void testDumperSubApp() throws Exception {
        formView = mock(FormViewReduced.class);
        doReturn(true).when(formView).isValid();
        view = mock(JcrToolsResultView.class);

        builder = new FormBuilder(mock(FieldFactoryFactory.class), mock(DefaultI18NAuthoringSupport.class), uiContext, mock(ComponentProvider.class));
        commandsManager = mock(CommandsManager.class);
        uiContext = mock(UiContext.class);
        i18n = mock(SimpleTranslator.class);

        subAppDescriptor = new ConfiguredJcrToolsSubAppDescriptor();
        formDefinition = new ConfiguredFormDefinition();

        subAppDescriptor.setForm(formDefinition);
        subAppContext = new SubAppContextImpl(subAppDescriptor, null);

        final MockSession session = new MockSession(workspace);
        final MockContext context = new MockContext();
        context.addSession(workspace, session);
        MgnlContext.setInstance(context);

        dumperSubApp = new DumperSubApp(subAppContext, formView, view, builder, commandsManager, context, uiContext, i18n);
        dumperSubApp.start(mock(Location.class));
        final Item item = dumperSubApp.getItem();
        item.addItemProperty(JcrToolsConstants.LEVEL_STRING, new ObjectProperty<>(level));
        item.addItemProperty(JcrToolsConstants.WORKSPACE, new ObjectProperty<>(workspace));

        Node node = session.getRootNode().addNode(path);
        item.addItemProperty(node, new ObjectProperty(node));

        dumperSubApp.onActionTriggered();

        verify(view).setResult("/\n/" + path + "\n");
    }
}