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
package info.magnolia.jcrtools.field;

import static info.magnolia.objectfactory.Components.newInstance;

import info.magnolia.cms.core.Path;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;
import info.magnolia.ui.form.field.transformer.basic.BasicTransformer;
import info.magnolia.ui.form.field.upload.UploadReceiver;

import javax.inject.Inject;

import com.vaadin.data.Item;

/**
 * {@link info.magnolia.ui.form.field.transformer.Transformer Transformer} creating a {@link UploadReceiver}.
 * Needed to get the upload field working without any JCR specifics. Could probably do less.
 *
 * @see info.magnolia.ui.form.field.transformer.basic.BasicTransformer
 */
public class FileImportTransformer extends BasicTransformer<UploadReceiver> {
    @Inject
    public FileImportTransformer(final Item relatedFormItem, final ConfiguredFieldDefinition definition,
                                 final Class<UploadReceiver> type, final I18NAuthoringSupport i18NAuthoringSupport) {
        super(relatedFormItem, definition, type, i18NAuthoringSupport);
    }

    @Override
    public UploadReceiver readFromItem() {
        return newInstance(UploadReceiver.class, Path.getTempDirectory());
    }
}
