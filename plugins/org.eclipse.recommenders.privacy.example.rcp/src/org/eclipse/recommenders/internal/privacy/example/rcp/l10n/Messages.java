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

    public static String ADVANCED_DIALOG_DESCRIPTION;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
