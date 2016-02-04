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
package info.magnolia.jcrtools.query;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
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
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;

import javax.jcr.Node;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;

public class QuerySubAppTest {
    private static final String workspace = "workspace";
    private static final String path = "path";
    private static final String queryLanguage = "JCR-SQL2";
    private static final String resultItemType = "nt:base";
    private static final String primaryNodeTypeName = "mgnl:content";
    private static final String statement = "select * from [" + primaryNodeTypeName + "]";
    private static final String successMessage = "1 nodes returned in";

    private JcrToolsResultView view;
    private UiContext uiContext;
    private SimpleTranslator i18n;
    private QuerySubApp querySubApp;

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
        MgnlContext.setInstance(context);

        querySubApp = new QuerySubApp(subAppContext, formView, view, builder, commandsManager, i18n, uiContext);
        querySubApp.start(location);

        final Node node = session.getRootNode().addNode(path, primaryNodeTypeName);
        final Item item = querySubApp.getItem();

        item.addItemProperty(JcrToolsConstants.WORKSPACE, new ObjectProperty<>(workspace));
        item.addItemProperty(JcrToolsConstants.QUERY_LANGUAGE, new ObjectProperty<>(queryLanguage));
        item.addItemProperty(JcrToolsConstants.RESULT_ITEM_TYPE, new ObjectProperty<>(resultItemType));
        item.addItemProperty(JcrToolsConstants.STATEMENT, new ObjectProperty<>(statement));
        item.addItemProperty(node, new ObjectProperty(node));
    }

    @After
    public void tearDown() throws Exception {
        MgnlContext.setInstance(null);
    }

    @Test
    public void testQuerySubApp() throws Exception {
        // WHEN
        querySubApp.onActionTriggered();

        // THEN
        ArgumentCaptor<String> resultView = ArgumentCaptor.forClass(String.class);
        verify(view).setResult(resultView.capture());
        assertThat(resultView.getValue(), containsString(successMessage));
        verify(uiContext).openNotification(MessageStyleTypeEnum.INFO, true, i18n.translate("jcr-tools.query.querySuccessMessage"));
        verify(uiContext, never()).openNotification(MessageStyleTypeEnum.ERROR, true, i18n.translate("jcr-tools.query.queryFailedMessage"));
    }
}