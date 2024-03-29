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
package info.magnolia.jcrtools;

import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.app.AppView;
import info.magnolia.ui.api.location.DefaultLocation;
import info.magnolia.ui.api.location.Location;
import info.magnolia.ui.framework.app.BaseApp;

import javax.inject.Inject;

/**
 * Jcr tools app base class.
 * This class opens the Jcr tools sub apps - currently: Dumper, Exporter, Importer, Query.
 *
 * Due to a known bug, the sub app that is in focus on app launch must be the last one opened.
 * So the app defaults now to having the Query sub app opened on launch.
 */
public class JcrToolsBaseApp extends BaseApp {

    @Inject
    public JcrToolsBaseApp(final AppContext appContext, final AppView view) {
        super(appContext, view);
    }

    @Override
    public void start(final Location location) {
        super.start(location);

        final AppContext appContext = getAppContext();
        final String appName = getAppContext().getAppDescriptor().getName();
        final String[] subAppNames = {"dumper", "exporter", "importer", "query"};

        for (String subAppName : subAppNames) {
            appContext.openSubApp(new DefaultLocation(Location.LOCATION_TYPE_APP, appName, subAppName, ""));
        }
    }

}