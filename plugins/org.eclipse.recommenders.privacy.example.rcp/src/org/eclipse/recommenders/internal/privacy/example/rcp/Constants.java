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
package org.eclipse.recommenders.internal.privacy.example.rcp;

public class Constants {

    public static final String URI_PREFIX = "http://recommenders.eclipse.org/heartbeat"; //$NON-NLS-1$
    public static final String BUNDLE_ID = "org.eclipse.recommenders.privacy.example.rcp"; //$NON-NLS-1$

    public static final String PREF_NODE_ADVANCED_PERMISSIONS_PATH = "org.eclipse.recommenders.privacy.example.rcp.advanced"; //$NON-NLS-1$
    public static final String PREF_ROOT_NODE_ADVANCED = "advanced"; //$NON-NLS-1$

    public static final String PRINCIPAL_ID = "org.eclipse.recommenders.privacy.example.rcp.principals.example"; //$NON-NLS-1$
    public static final String HEARTBEAT = "org.eclipse.recommenders.privacy.rcp.datums.heartbeat"; //$NON-NLS-1$
    public static final String ANONYMOUS_ID = "org.eclipse.recommenders.privacy.rcp.datums.anonymousId"; //$NON-NLS-1$

    public static final String BUNDLE_VERSION = "Bundle-Version"; //$NON-NLS-1$
    public static final String BUNDLE_NAME = "Bundle-Name"; //$NON-NLS-1$

    public static final String PREF_DELAY = "delay"; //$NON-NLS-1$

    private Constants() {
        // Not meant to be instantiated
    }
}
