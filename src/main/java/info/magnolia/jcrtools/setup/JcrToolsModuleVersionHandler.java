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
package info.magnolia.jcrtools.setup;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.NodeExistsDelegateTask;
import info.magnolia.module.delta.RemoveNodeTask;
import info.magnolia.module.delta.Task;
import info.magnolia.ui.admincentral.setup.RegisterAppIntoAppLauncherTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Version handler for the jcr-tools module.
 */
public class JcrToolsModuleVersionHandler extends DefaultModuleVersionHandler {

    protected final static String APP_LAUNCHER_LAYOUT_GROUPS_PATH = "/modules/ui-admincentral/config/appLauncherLayout/groups";

    @Override
    protected List<Task> getExtraInstallTasks(InstallContext installContext) {
        final List<Task> tasks = new ArrayList<>();

        // Add the jcr-tools app to the 'tools' group
        tasks.add(new RegisterAppIntoAppLauncherTask("jcr-tools", "tools"));

        // Remove links to the old versions of the apps, if they exist
        tasks.add(new NodeExistsDelegateTask("Remove old Export app link", APP_LAUNCHER_LAYOUT_GROUPS_PATH + "/tools/apps/export",
                new RemoveNodeTask("", APP_LAUNCHER_LAYOUT_GROUPS_PATH + "/tools/apps/export")));
        tasks.add(new NodeExistsDelegateTask("Remove old Import app link", APP_LAUNCHER_LAYOUT_GROUPS_PATH + "/tools/apps/import",
                new RemoveNodeTask("", APP_LAUNCHER_LAYOUT_GROUPS_PATH + "/tools/apps/import")));
        tasks.add(new NodeExistsDelegateTask("Remove old JcrQuery Utils app link", APP_LAUNCHER_LAYOUT_GROUPS_PATH + "/dev/apps/jcrQueryUtils",
                new RemoveNodeTask("", APP_LAUNCHER_LAYOUT_GROUPS_PATH + "/dev/apps/jcrQueryUtils")));

        return tasks;
    }

}
