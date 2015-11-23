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
package info.magnolia.jcrtools.exporter;

import info.magnolia.commands.CommandsManager;
import info.magnolia.commands.impl.ExportCommand;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.jcrtools.JcrToolsBaseSubApp;
import info.magnolia.jcrtools.JcrToolsConstants;
import info.magnolia.jcrtools.JcrToolsView;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.dialog.formdialog.FormBuilder;
import info.magnolia.ui.framework.util.TempFileStreamResource;
import info.magnolia.ui.vaadin.form.FormViewReduced;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;

import com.vaadin.data.Item;
import com.vaadin.server.Page;

/**
 * Sub app that creates an export file from a given workspace using {@link ExportCommand}.
 */
public class ExporterSubApp extends JcrToolsBaseSubApp {
    private final UiContext uiContext;
    private final SimpleTranslator i18n;

    @Inject
    public ExporterSubApp(final SubAppContext subAppContext, final FormViewReduced formView, final JcrToolsView view,
                          final FormBuilder builder, final CommandsManager commandsManager, final UiContext uiContext,
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

            doExport(item);
        }
    }

    private void doExport(final Item item) {
        final String workspace = item.getItemProperty(JcrToolsConstants.WORKSPACE).getValue().toString();
        final String rawBasePath = item.getItemProperty(JcrToolsConstants.BASE_PATH).getValue().toString();
        final String compression = item.getItemProperty(JcrToolsConstants.COMPRESSION).getValue().toString();

        final String tmpFileName = rawBasePath.equals("/") ? (workspace + compression).replace("/", ".") : (workspace + rawBasePath + compression).replace("/", ".");

        OutputStream tempFileOutputStream = null;

        try {
            final String formatXml = item.getItemProperty(JcrToolsConstants.FORMAT_XML).getValue().toString();
            TempFileStreamResource tempFileStreamResource = new TempFileStreamResource(tmpFileName);
            tempFileStreamResource.setTempFileName(tmpFileName);
            tempFileStreamResource.setTempFileExtension(compression);
            tempFileOutputStream = tempFileStreamResource.getTempFileOutputStream();

            Map<String, Object> params = new HashMap<>();
            params.put("repository", workspace);
            params.put("path", rawBasePath);
            params.put(ExportCommand.EXPORT_EXTENSION, compression);
            params.put(ExportCommand.EXPORT_FILE_NAME, tmpFileName);
            params.put(ExportCommand.EXPORT_FORMAT, formatXml);
            params.put(ExportCommand.EXPORT_MIME_EXTENSION, compression);
            params.put(ExportCommand.EXPORT_OUTPUT_STREAM, tempFileOutputStream);

            commandsManager.executeCommand("export", params);

            // TODO bandersen - open() is deprecated; should instead be a {@link https://vaadin.com/api/com/vaadin/ui/Link.html}. */
            Page.getCurrent().open(tempFileStreamResource, "", false);
            uiContext.openNotification(MessageStyleTypeEnum.INFO, true, i18n.translate("jcr-tools.exporter.exportSuccessMessage"));
        } catch (Exception e) {
            log.error("Failed to execute export command", e);
            uiContext.openNotification(MessageStyleTypeEnum.ERROR, true, i18n.translate("jcr-tools.exporter.exportFailedMessage"));
        } finally {
            IOUtils.closeQuietly(tempFileOutputStream);
        }
    }
}