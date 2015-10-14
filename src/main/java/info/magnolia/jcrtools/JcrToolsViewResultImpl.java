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
package info.magnolia.jcrtools;

import info.magnolia.i18nsystem.SimpleTranslator;

import javax.inject.Inject;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextArea;

/**
 * Base class for {@View} implementations for Jcr tools sub apps that have a result field.
 *
 * @see info.magnolia.jcrtools.JcrToolsResultView
 * @see info.magnolia.jcrtools.JcrToolsViewImpl
 */
public class JcrToolsViewResultImpl extends JcrToolsViewImpl implements JcrToolsResultView {
    private final CssLayout resultSection = new CssLayout();

    private TextArea result;

    @Inject
    public JcrToolsViewResultImpl(final SimpleTranslator i18n) {
        super(i18n);
    }

    @Override
    public void setResult(String res) {
        result.setValue(res);
    }

    @Override
    protected void build() {

        super.build();

        result = new TextArea();
        result.setRows(20);
        result.setSizeFull();

        resultSection.addComponent(result);

        content.addSection(inputSection);
        content.addSection(resultSection);

    }
}
