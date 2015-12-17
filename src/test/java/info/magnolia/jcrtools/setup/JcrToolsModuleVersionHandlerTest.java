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
package info.magnolia.jcrtools.setup;

import static info.magnolia.jcrtools.setup.JcrToolsModuleVersionHandler.APP_LAUNCHER_LAYOUT_GROUPS_PATH;
import static info.magnolia.test.hamcrest.NodeMatchers.hasNode;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import info.magnolia.module.InstallContext;
import info.magnolia.module.ModuleVersionHandler;
import info.magnolia.module.ModuleVersionHandlerTestCase;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class JcrToolsModuleVersionHandlerTest extends ModuleVersionHandlerTestCase {

    @Override
    protected String getModuleDescriptorPath() {
        return "/META-INF/magnolia/jcr-tools.xml";
    }

    @Override
    protected ModuleVersionHandler newModuleVersionHandlerForTests() {
        return new JcrToolsModuleVersionHandler();
    }

    @Override
    protected List<String> getModuleDescriptorPathsForTests() {
        return Arrays.asList("/META-INF/magnolia/core.xml");
    }

    @Test
    public void cleanInstallRemovesLegacyAppsFromAppLauncherConfiguration() throws Exception {
        // GIVEN
        setupConfigNode(APP_LAUNCHER_LAYOUT_GROUPS_PATH + "/tools/apps/export");
        setupConfigNode(APP_LAUNCHER_LAYOUT_GROUPS_PATH + "/tools/apps/import");
        setupConfigNode(APP_LAUNCHER_LAYOUT_GROUPS_PATH + "/dev/apps/jcrQueryUtils");

        // WHEN
        final InstallContext installContext = executeUpdatesAsIfTheCurrentlyInstalledVersionWas(null);

        // THEN
        assertThat(installContext.getConfigJCRSession().getNode(APP_LAUNCHER_LAYOUT_GROUPS_PATH + "/tools/apps"), not(hasNode("export")));
        assertThat(installContext.getConfigJCRSession().getNode(APP_LAUNCHER_LAYOUT_GROUPS_PATH + "/tools/apps"), not(hasNode("import")));
        assertThat(installContext.getConfigJCRSession().getNode(APP_LAUNCHER_LAYOUT_GROUPS_PATH + "/dev/apps"), not(hasNode("jcrQueryUtils")));
        assertThat(installContext.getConfigJCRSession().getNode(APP_LAUNCHER_LAYOUT_GROUPS_PATH + "/dev/apps"), hasNode("jcr-tools"));
    }
}
