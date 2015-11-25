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
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;

import javax.jcr.Node;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;

public class DumperSubAppTest {
    private static final String workspace = "workspace";
    private static final String level = "2";
    private static final String path = "path";
    private static final String expectedResultString = "/\n/" + path + "\n";

    private JcrToolsResultView view;
    private UiContext uiContext;
    private SimpleTranslator i18n;
    private DumperSubApp dumperSubApp;

    @Before
    public void setUp() throws Exception {
        // GIVEN
        final FormViewReduced formView = mock(FormViewReduced.class);
        final CommandsManager commandsManager = mock(CommandsManager.class);
        final FieldFactoryFactory fieldFactoryFactory = mock(FieldFactoryFactory.class);
        final DefaultI18NAuthoringSupport defaultI18NAuthoringSupport = mock(DefaultI18NAuthoringSupport.class);
        final ComponentProvider componentProvider = mock(ComponentProvider.class);
        final Location location = mock(Location.class);

        final MockSession session = new MockSession(workspace);
        final MockContext context = new MockContext();

        view = mock(JcrToolsResultView.class);
        uiContext = mock(UiContext.class);
        i18n = mock(SimpleTranslator.class);

        doReturn(true).when(formView).isValid();

        final FormBuilder builder = new FormBuilder(fieldFactoryFactory, defaultI18NAuthoringSupport, uiContext, componentProvider);
        final ConfiguredJcrToolsSubAppDescriptor subAppDescriptor = new ConfiguredJcrToolsSubAppDescriptor();
        final FormDefinition formDefinition = new ConfiguredFormDefinition();

        subAppDescriptor.setForm(formDefinition);
        SubAppContext subAppContext = new SubAppContextImpl(subAppDescriptor, null);

        context.addSession(workspace, session);

        dumperSubApp = new DumperSubApp(subAppContext, formView, view, builder, commandsManager, context, uiContext, i18n);
        dumperSubApp.start(location);

        final Node node = session.getRootNode().addNode(path);
        final Item item = dumperSubApp.getItem();

        item.addItemProperty(JcrToolsConstants.LEVEL_STRING, new ObjectProperty<>(level));
        item.addItemProperty(JcrToolsConstants.WORKSPACE, new ObjectProperty<>(workspace));
        item.addItemProperty(node, new ObjectProperty(node));
    }

    @Test
    public void testDumperSubApp() throws Exception {
        // WHEN
        dumperSubApp.onActionTriggered();

        // THEN
        verify(view).setResult(matches(expectedResultString));
        verify(uiContext).openNotification(MessageStyleTypeEnum.INFO, true, i18n.translate("jcr-tools.dumper.dumpSuccessMessage"));
        verify(uiContext, never()).openNotification(MessageStyleTypeEnum.ERROR, true, i18n.translate("jcr-tools.dumper.dumpFailedMessage"));
    }
}