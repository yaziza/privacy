/**
 * Copyright (c) 2014 Yasser Aziza.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Yasser Aziza - initial implementation
 */
package org.eclipse.recommenders.internal.privacy.rcp;

public final class Constants {

    public static final String BUNDLE_ID = "org.eclipse.recommenders.privacy.rcp"; //$NON-NLS-1$

    public static final String PREF_NODE_GLOBAL_PERMISSIONS_PATH = "org.eclipse.recommenders.privacy.rcp.permissions"; //$NON-NLS-1$
    public static final String PREF_NODE_GLOBAL_PERMISSIONS_VALUE = "permissions"; //$NON-NLS-1$
    public static final String PREF_NODE_GLOBAL_ACTIVATION_VALUE = "activation"; //$NON-NLS-1$
    public static final String PREF_ROOT_NODE = "approval"; //$NON-NLS-1$
    public static final String PREF_ROOT_NODE_GENERAL = "general"; //$NON-NLS-1$
    public static final String PREF_ROOT_NODE_ADVANCED = "advanced"; //$NON-NLS-1$

    public static final String DEFAULT_PRINCIPAL_ICON = "icons/obj16/defaultPrincipal.gif"; //$NON-NLS-1$
    public static final String DEFAULT_DATUM_ICON = "icons/obj16/defaultDatum.gif"; //$NON-NLS-1$
    public static final String BANNER_ICON = "icons/wizban/logo.png"; //$NON-NLS-1$

    public static final String PREFERENCE_PAGE_EXTENTIONPOINT_ID = "org.eclipse.ui.preferencePages"; //$NON-NLS-1$
    public static final String PREF_PAGE_ID = "org.eclipse.recommenders.privacy.rcp.preferencesPages.privacy"; //$NON-NLS-1$

    private Constants() {
        // Not meant to be instantiated
    }
}
