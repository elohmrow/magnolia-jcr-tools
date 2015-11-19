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
package info.magnolia.jcrtools.importer;

import info.magnolia.commands.CommandsManager;
import info.magnolia.commands.impl.ImportCommand;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.jcrtools.JcrToolsBaseSubApp;
import info.magnolia.jcrtools.JcrToolsConstants;
import info.magnolia.jcrtools.JcrToolsView;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.dialog.formdialog.FormBuilder;
import info.magnolia.ui.form.field.upload.UploadReceiver;
import info.magnolia.ui.vaadin.form.FormViewReduced;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;

import com.vaadin.data.Item;

/**
 * Sub app that reads in an import file from [the export of] a given workspace using {@link UploadReceiver}.
 */
public class ImporterSubApp extends JcrToolsBaseSubApp {
    private final UiContext uiContext;
    private final SimpleTranslator i18n;

    @Inject
    public ImporterSubApp(final SubAppContext subAppContext, final UiContext uiContext, final FormViewReduced formView,
                          final JcrToolsView view, final FormBuilder builder, final CommandsManager commandsManager,
                          final SimpleTranslator i18n) {
        super(subAppContext, formView, view, builder, commandsManager);
        this.uiContext = uiContext;
        this.i18n = i18n;
    }

    @Override
    public void onActionTriggered() {
        super.onActionTriggered();
        if (formView.isValid()) {
            final Item item = getItem();
            final String workspace = item.getItemProperty(JcrToolsConstants.WORKSPACE).getValue().toString();
            final String path = item.getItemProperty(JcrToolsConstants.PATH).getValue().toString();
            final UploadReceiver file = (UploadReceiver) item.getItemProperty(JcrToolsConstants.FILE).getValue();
            final String behavior = item.getItemProperty(JcrToolsConstants.BEHAVIOR).getValue().toString();

            doImport(workspace, path, file, behavior);
        }
    }

    private void doImport(final String workspace, final String path, final UploadReceiver file, final String behavior) {
        final InputStream contentAsStream = file.getContentAsStream();

        Map<String, Object> params = new HashMap<>();
        params.put("repository", workspace);
        params.put("path", path);
        params.put(ImportCommand.IMPORT_IDENTIFIER_BEHAVIOR, behavior);
        params.put(ImportCommand.IMPORT_XML_STREAM, contentAsStream);
        params.put(ImportCommand.IMPORT_XML_FILE_NAME, file.getFileName());

        try {
            commandsManager.executeCommand("import", params);
            uiContext.openNotification(MessageStyleTypeEnum.INFO, true, i18n.translate("jcr-tools.importer.importSuccessMessage"));
        } catch (Exception e) {
            log.error("Failed to execute import command.", e);
            uiContext.openNotification(MessageStyleTypeEnum.ERROR, true, i18n.translate("jcr-tools.importer.importFailedMessage"));
        } finally {
            IOUtils.closeQuietly(contentAsStream);
        }
    }
}