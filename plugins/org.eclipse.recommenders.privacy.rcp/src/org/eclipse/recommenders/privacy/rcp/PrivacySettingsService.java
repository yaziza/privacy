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
package org.eclipse.recommenders.privacy.rcp;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrivacySettingsService implements IPrivacySettingsService {
    private static final Logger LOG = LoggerFactory.getLogger(PrivacySettingsService.class);

    private final String qualifier;

    public PrivacySettingsService() {
        this(Constants.PREF_NODE_ID_GLOBAL_PERMISSIONS);
    }

    public PrivacySettingsService(String qualifier) {
        this.qualifier = qualifier;
    }

    @Override
    public boolean isAllowed(String datumId, String principal) {
        String key = datumId.concat("." + principal);
        return getGlobalPermissionPreferences().getBoolean(key, false);
    }

    @Override
    public void allow(String datumId, String principal) {
        store(datumId, principal, true);
    }

    @Override
    public void disallow(String datumId, String principal) {
        store(datumId, principal, false);
    }

    private void store(String datumId, String principal, boolean value) {
        String key = datumId.concat("." + principal);
        IEclipsePreferences prefs = getGlobalPermissionPreferences();
        prefs.putBoolean(key, value);
        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            LOG.error("Failed to flush preferences", e);
        }
    }

    private IEclipsePreferences getGlobalPermissionPreferences() {
        return InstanceScope.INSTANCE.getNode(qualifier);
    }
}
