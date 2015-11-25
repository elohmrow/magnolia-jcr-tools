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
package info.magnolia.jcrtools.field.validator;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcrtools.JcrToolsConstants;
import info.magnolia.test.mock.MockContext;
import info.magnolia.test.mock.jcr.MockSession;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import javax.jcr.Node;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.util.ObjectProperty;

public class NodePathValidatorTest {
    private static final String workspace = "workspace";
    private static final String path = "path";
    private static final String propertyName = "propertyName";
    private static final String propertyValue = "propertyValue";

    private MockSession session;
    private MockContext context;
    private Node node;
    private JcrNodeAdapter item;

    @Before
    public void setUp() throws Exception {
        // GIVEN
        session = new MockSession(workspace);
        context = new MockContext();
        context.addSession(workspace, session);
        MgnlContext.setInstance(context);

        node = session.getRootNode().addNode(path);
        item = new JcrNodeAdapter(node);
    }

    @Test
    public void testNodePathValidator() throws Exception {
        // WHEN
        item.addItemProperty(propertyName, new ObjectProperty<>(propertyValue));
        item.addItemProperty(JcrToolsConstants.WORKSPACE, new ObjectProperty<>(workspace));
        final NodePathValidator nodePathValidator = new NodePathValidator(item, context, "");

        // THEN
        Assert.assertTrue(nodePathValidator.isValid(node.getPath()));  // should be true
        Assert.assertFalse(nodePathValidator.isValid("/fakePath"));    // should be false
    }
}
