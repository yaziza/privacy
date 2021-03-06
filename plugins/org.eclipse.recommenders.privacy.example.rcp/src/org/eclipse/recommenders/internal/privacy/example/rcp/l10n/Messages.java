/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Sewe - initial implementation
 */
package org.eclipse.recommenders.internal.privacy.example.rcp.l10n;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.eclipse.recommenders.internal.privacy.example.rcp.l10n.messages"; //$NON-NLS-1$

    public static String JOB_SENDING_HEARTBEAT;

    public static String CONFIGURATION_DIALOG_DESCRIPTION;

    public static String LOG_INFO_SENDING_PERMISSION_APPROVED;
    public static String LOG_INFO_SENDING_PERMISSION_DISAPPROVED;
    public static String LOG_INFO_SENDING_PERMISSION_UNKNOWN;

    public static String HEARTBEAT_INTERVAL_ONCE;
    public static String HEARTBEAT_INTERVAL_HOURLY;
    public static String HEARTBEAT_INTERVAL_DAILY;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
