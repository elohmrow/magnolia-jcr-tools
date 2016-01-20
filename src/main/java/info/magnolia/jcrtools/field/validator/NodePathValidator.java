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

import info.magnolia.context.Context;
import info.magnolia.jcrtools.JcrToolsConstants;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.validator.AbstractStringValidator;

/**
 * Ensures a node path exists.
 *
 * Example use case: Jcr Tools [Im/Ex]porter sub app 'Base Path' field.
 */
public class NodePathValidator extends AbstractStringValidator {
    private static final Logger log = LoggerFactory.getLogger(NodePathValidator.class);

    private final Item item;
    private final Context context;

    public NodePathValidator(final Item item, final Context context, final String errorMessage) {
        super(errorMessage);

        this.item = item;
        this.context = context;
    }

    @Override
    protected boolean isValidValue(String basePath) {
        if (!StringUtils.isBlank(basePath)) {
            return nodeExists(basePath);
        }

        return false;
    }

    private boolean nodeExists(final String basePath) {
        try {
            final String workspace = item.getItemProperty(JcrToolsConstants.WORKSPACE).getValue().toString();

            Session session = context.getJCRSession(workspace);
            return session.nodeExists(basePath);

        } catch (RepositoryException e) {
            log.error("Node '{}' does not exist.", basePath, e.getMessage());

            return false;
        }
    }
}