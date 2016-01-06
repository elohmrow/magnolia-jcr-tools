/**
 * This file Copyright (c) 2016 Magnolia International
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

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import info.magnolia.commands.CommandsManager;
import info.magnolia.context.MgnlContext;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcrtools.ConfiguredJcrToolsSubAppDescriptor;
import info.magnolia.jcrtools.JcrToolsConstants;
import info.magnolia.jcrtools.JcrToolsResultView;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.RepositoryTestCase;
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

import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;

public class DumperSubAppTest extends RepositoryTestCase {

    private DumperSubApp dumperSubApp;
    private JcrToolsResultView view;
    private UiContext uiContext;
    private SimpleTranslator i18n;
    private Item item;

    private ArgumentCaptor<String> actualResultText;

    private Node firstNode;
    private Node secondNode;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        final Session session = MgnlContext.getJCRSession(RepositoryConstants.WEBSITE);

        // Setup of node structure
        firstNode = NodeUtil.createPath(session.getRootNode(), "dumperTest", NodeTypes.Page.NAME);
        firstNode.setProperty("myProp", "one-value");
        firstNode.setProperty("myMultiProp", new String[] { "valueA", "valueB", "valueC" });

        secondNode = NodeUtil.createPath(firstNode, "subPage1", NodeTypes.Page.NAME);

        NodeUtil.createPath(secondNode, "metaNavigation", NodeTypes.Area.NAME);

        // Setup of UI components
        final FormViewReduced formView = mock(FormViewReduced.class);
        final CommandsManager commandsManager = mock(CommandsManager.class);
        final FieldFactoryFactory fieldFactoryFactory = mock(FieldFactoryFactory.class);
        final DefaultI18NAuthoringSupport defaultI18NAuthoringSupport = mock(DefaultI18NAuthoringSupport.class);
        final ComponentProvider componentProvider = mock(ComponentProvider.class);

        view = mock(JcrToolsResultView.class);
        uiContext = mock(UiContext.class);
        i18n = mock(SimpleTranslator.class);

        doReturn(true).when(formView).isValid();

        final FormBuilder builder = new FormBuilder(fieldFactoryFactory, defaultI18NAuthoringSupport, uiContext, componentProvider);
        final ConfiguredJcrToolsSubAppDescriptor subAppDescriptor = new ConfiguredJcrToolsSubAppDescriptor();
        final FormDefinition formDefinition = new ConfiguredFormDefinition();

        subAppDescriptor.setForm(formDefinition);
        SubAppContext subAppContext = new SubAppContextImpl(subAppDescriptor, null);

        dumperSubApp = new DumperSubApp(subAppContext, formView, view, builder, commandsManager, MgnlContext.getInstance(), uiContext, i18n);

        dumperSubApp.start(mock(Location.class));

        item = dumperSubApp.getItem();
        item.addItemProperty(JcrToolsConstants.WORKSPACE, new ObjectProperty<>("website"));
        item.addItemProperty(JcrToolsConstants.BASE_PATH, new ObjectProperty<>("/dumperTest"));

        actualResultText = ArgumentCaptor.forClass(String.class);
    }

    @Test
    public void dumperResultCorrectAtLevel1() throws Exception {
        // GIVEN
        item.addItemProperty(JcrToolsConstants.LEVEL_STRING, new ObjectProperty<>(1L));

        // WHEN
        dumperSubApp.onActionTriggered();

        // THEN
        verify(view).setResult(actualResultText.capture());

        final List<String> resultingLines = getResultAsList(actualResultText.getValue());
        assertThat(resultingLines,
                allOf(
                        hasItem("/dumperTest"),
                        hasItem("/dumperTest/myProp=one-value"),
                        hasItem("/dumperTest/myMultiProp=valueA,valueB,valueC"),
                        hasItem("/dumperTest/jcr:uuid=" + firstNode.getIdentifier()),
                        hasItem("/dumperTest/jcr:createdBy=admin"),
                        hasItem("/dumperTest/jcr:mixinTypes=mix:lockable"),
                        hasItem("/dumperTest/jcr:primaryType=mgnl:page"),
                        hasItem("/dumperTest/subPage1[mgnl:page]")
                )
        );

        verifyNotifications();
    }

    @Test
    public void dumperResultCorrectAtLevel2() throws Exception {
        // GIVEN
        item.addItemProperty(JcrToolsConstants.LEVEL_STRING, new ObjectProperty<>(2L));

        // WHEN
        dumperSubApp.onActionTriggered();

        // THEN
        verify(view).setResult(actualResultText.capture());

        final List<String> resultingLines = getResultAsList(actualResultText.getValue());
        assertThat(resultingLines,
                allOf(
                        hasItem("/dumperTest"),
                        hasItem("/dumperTest/myProp=one-value"),
                        hasItem("/dumperTest/myMultiProp=valueA,valueB,valueC"),
                        hasItem("/dumperTest/jcr:uuid=" + firstNode.getIdentifier()),
                        hasItem("/dumperTest/jcr:createdBy=admin"),
                        hasItem("/dumperTest/jcr:mixinTypes=mix:lockable"),
                        hasItem("/dumperTest/jcr:primaryType=mgnl:page"),

                        hasItem("/dumperTest/subPage1"),
                        hasItem("/dumperTest/subPage1/jcr:createdBy=admin"),
                        hasItem("/dumperTest/subPage1/jcr:uuid=" + secondNode.getIdentifier()),
                        hasItem("/dumperTest/subPage1/jcr:mixinTypes=mix:lockable"),
                        hasItem("/dumperTest/subPage1/jcr:primaryType=mgnl:page"),
                        hasItem("/dumperTest/subPage1/metaNavigation[mgnl:area]")
                )
        );

        verifyNotifications();
    }

    private List<String> getResultAsList(String output) {
        return Arrays.asList(output.split("\n"));
    }

    private void verifyNotifications() {
        verify(uiContext).openNotification(MessageStyleTypeEnum.INFO, true, i18n.translate("jcr-tools.dumper.dumpSuccessMessage"));
        verify(uiContext, never()).openNotification(MessageStyleTypeEnum.ERROR, true, i18n.translate("jcr-tools.dumper.dumpFailedMessage"));
    }

}