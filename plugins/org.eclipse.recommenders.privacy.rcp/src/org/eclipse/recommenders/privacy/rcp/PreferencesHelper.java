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
package org.eclipse.recommenders.privacy.rcp;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class PreferencesHelper {

    public static String createLinkLabelToPreferencePage(String preferencePageID) {
        String text = getNameOfPreferencePage(preferencePageID);

        String categoryID = getCategoryOfPreferencePage(preferencePageID);
        while (categoryID != null) {
            text = getNameOfPreferencePage(categoryID) + " > " + text;
            categoryID = getCategoryOfPreferencePage(categoryID);
        }
        return text == null ? "" : text;
    }

    private static String getNameOfPreferencePage(String preferencePageID) {
        return getAttributeOfPreferencePage(preferencePageID, "name");
    }

    private static String getCategoryOfPreferencePage(String preferencePageID) {
        return getAttributeOfPreferencePage(preferencePageID, "category");
    }

    private static String getAttributeOfPreferencePage(String preferencePageID, String attribute) {
        if (preferencePageID != null) {
            IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                    Constants.PREFERENCE_PAGE_EXTENTIONPOINT_ID);

            if (elements != null) {
                for (IConfigurationElement e : elements) {
                    String configId = e.getAttribute("id");
                    if (preferencePageID.equalsIgnoreCase(configId)) {
                        String value = e.getAttribute(attribute);
                        return value;
                    }
                }
            }
        }
        return null;
    }
}
