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
package info.magnolia.jcrtools.field;

import info.magnolia.context.Context;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;
import info.magnolia.ui.form.field.factory.SelectFieldFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * Factory that constructs select field with query language names as options.
 */
public class QueryLanguageSelectFieldFactory extends SelectFieldFactory<QueryLanguageSelectFieldDefinition> {

    private static final Logger log = LoggerFactory.getLogger(QueryLanguageSelectFieldFactory.class);
    private final Context context;
    private final List<String> queryLanguageNames = new ArrayList<>();

    @Inject
    public QueryLanguageSelectFieldFactory(final QueryLanguageSelectFieldDefinition definition, final Item relatedFieldItem, final Context context) {
        super(definition, relatedFieldItem);
        this.context = context;
        initQueryLanguages();
    }

    private void initQueryLanguages() {
        try {
            String[] supportedLanguages = context.getJCRSession(RepositoryConstants.WEBSITE).getWorkspace().getQueryManager().getSupportedQueryLanguages();

            for (String supportedLanguage : supportedLanguages) {
                if (!supportedLanguage.equals(Query.JCR_JQOM)) {
                    queryLanguageNames.add(supportedLanguage);
                }
            }

        } catch (RepositoryException e) {
            log.error("Could not get supported query languages.", e);
        }
    }

    @Override
    public List<SelectFieldOptionDefinition> getSelectFieldOptionDefinition() {
        List<SelectFieldOptionDefinition> selectFieldOptions = new ArrayList<>();
        for (String queryLanguage : queryLanguageNames) {
            SelectFieldOptionDefinition selectFieldOptionDefinition = new SelectFieldOptionDefinition();
            selectFieldOptionDefinition.setLabel(queryLanguage);
            selectFieldOptionDefinition.setValue(queryLanguage);
            selectFieldOptionDefinition.setName(queryLanguage);
            selectFieldOptions.add(selectFieldOptionDefinition);
        }
        return selectFieldOptions;
    }

    @Override
    protected Object createDefaultValue(final Property<?> dataSource) {
        Object value = super.createDefaultValue(dataSource);
        if (value != null) {
            return value;
        }
        if (!queryLanguageNames.isEmpty()) {
            return queryLanguageNames.get(0);
        }
        return null;
    }

}
