/**
 * Copyright (c) 2014 Yasser Aziza.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olav Lenz - initial API and implementation.
 */
package org.eclipse.recommenders.internal.privacy.rcp.preferences;

import static org.eclipse.recommenders.internal.privacy.rcp.Constants.PREFERENCE_PAGE_EXTENTIONPOINT_ID;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class PreferencesHelper {

    private static final String ID_ATTRIBUTE = "id"; //$NON-NLS-1$
    private static final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$
    private static final String CATEGORY_ATTRIBUTE = "category"; //$NON-NLS-1$

    public static String createLinkLabelToPreferencePage(String preferencePageId) {
        String text = getNameOfPreferencePage(preferencePageId);

        String categoryId = getCategoryOfPreferencePage(preferencePageId);
        while (categoryId != null) {
            text = getNameOfPreferencePage(categoryId) + " > " + text; //$NON-NLS-1$
            categoryId = getCategoryOfPreferencePage(categoryId);
        }
        return text == null ? "" : text; //$NON-NLS-1$
    }

    private static String getNameOfPreferencePage(String preferencePageId) {
        return getAttributeOfPreferencePage(preferencePageId, NAME_ATTRIBUTE);
    }

    private static String getCategoryOfPreferencePage(String preferencePageId) {
        return getAttributeOfPreferencePage(preferencePageId, CATEGORY_ATTRIBUTE);
    }

    private static String getAttributeOfPreferencePage(String preferencePageId, String attribute) {
        if (preferencePageId != null) {
            IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                    PREFERENCE_PAGE_EXTENTIONPOINT_ID);

            if (elements != null) {
                for (IConfigurationElement e : elements) {
                    String configId = e.getAttribute(ID_ATTRIBUTE);
                    if (preferencePageId.equalsIgnoreCase(configId)) {
                        String value = e.getAttribute(attribute);
                        return value;
                    }
                }
            }
        }
        return null;
    }
}
