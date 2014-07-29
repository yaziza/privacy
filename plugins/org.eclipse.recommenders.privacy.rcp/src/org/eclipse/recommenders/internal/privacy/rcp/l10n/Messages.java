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
package org.eclipse.recommenders.internal.privacy.rcp.l10n;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.eclipse.recommenders.internal.privacy.rcp.l10n.messages"; //$NON-NLS-1$

    public static String JOB_APPROVAL_DIALOG;

    public static String PRIVACY_PREFPAGE_TITLE;
    public static String PRIVACY_PREFPAGE_DESCRIPTION;

    public static String USERID_PREFPAGE_TITLE;
    public static String USERID_PREFPAGE_DESCRIPTION;

    public static String APPROVAL_DIALOG_TITLE;
    public static String APPROVAL_DIALOG_MESSAGE;

    public static String LABEL_GROUP_BY;
    public static String LABEL_INFORMATION;
    public static String LABEL_INTERESTED_PARTY;
    public static String LABEL_USER_ID;

    public static String BUTTON_ENABLE_ALL;
    public static String BUTTON_DISABLE_ALL;
    public static String BUTTON_GENERATE;

    public static String TOOLTIP_INTERESTED_PARTY;

    public static String PREF_LINK_MESSAGE;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
