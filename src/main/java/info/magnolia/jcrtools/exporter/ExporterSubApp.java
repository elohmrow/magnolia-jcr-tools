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
import info.magnolia.jcrtools.JcrToolsBaseSubApp;
import info.magnolia.jcrtools.JcrToolsConstants;
import info.magnolia.jcrtools.JcrToolsView;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.dialog.formdialog.FormBuilder;
import info.magnolia.ui.framework.util.TempFileStreamResource;
import info.magnolia.ui.vaadin.form.FormViewReduced;

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
    @Inject
    public ExporterSubApp(final SubAppContext subAppContext, final FormViewReduced formView, final JcrToolsView view,
                          final FormBuilder builder, final CommandsManager commandsManager) {
        super(subAppContext, formView, view, builder, commandsManager);
    }

    @Override
    public void onActionTriggered() {
        super.onActionTriggered();
        if (formView.isValid()) {
            final Item item = getItem();
            final String repository = item.getItemProperty(JcrToolsConstants.REPOSITORY).getValue().toString();
            final String basePath = item.getItemProperty(JcrToolsConstants.BASE_PATH).getValue().toString();
            final String compression = item.getItemProperty(JcrToolsConstants.COMPRESSION).getValue().toString();
            final String formatXml = item.getItemProperty(JcrToolsConstants.FORMAT_XML).getValue().toString();

            doExport(repository, basePath, compression, formatXml);
        }
    }

    private void doExport(final String repository, final String rawBasePath, final String compression, final String formatXml) {
        final String tmpFileName = rawBasePath.equals("/") ? (repository + compression).replace("/", ".") : (repository + rawBasePath + compression).replace("/", ".");
        OutputStream tempFileOutputStream = null;

        try {
            final TempFileStreamResource tempFileStreamResource = new TempFileStreamResource(tmpFileName);
            tempFileStreamResource.setTempFileName(tmpFileName);
            tempFileStreamResource.setTempFileExtension(compression);
            tempFileStreamResource.getTempFileOutputStream();

            Map<String, Object> params = new HashMap<>();
            params.put(JcrToolsConstants.REPOSITORY, repository);
            params.put(JcrToolsConstants.PATH, rawBasePath);
            params.put(ExportCommand.EXPORT_EXTENSION, compression);
            params.put(ExportCommand.EXPORT_FILE_NAME, tmpFileName);
            params.put(ExportCommand.EXPORT_FORMAT, formatXml);
            params.put(ExportCommand.EXPORT_MIME_EXTENSION, compression);
            params.put(ExportCommand.EXPORT_OUTPUT_STREAM, tempFileOutputStream);

            commandsManager.executeCommand("export", params);

            // TODO bandersen - open() is deprecated; should instead be a {@link https://vaadin.com/api/com/vaadin/ui/Link.html}. */
            Page.getCurrent().open(tempFileStreamResource, "", false);
        } catch (Exception e) {
            log.error("Failed to execute export command", e);
        } finally {
            IOUtils.closeQuietly(tempFileOutputStream);
        }
    }
}