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

    @Override
    public boolean isAllowed(String datumId) {
        return getGlobalPermissionPreferences().getBoolean(datumId, false);
    }

    @Override
    public void allow(String datumId) {
        store(datumId, true);
    }

    @Override
    public void disallow(String datumId) {
        store(datumId, false);
    }

    private void store(String datumId, boolean value) {
        IEclipsePreferences store = getGlobalPermissionPreferences();
        store.putBoolean(datumId, value);
        try {
            store.flush();
        } catch (BackingStoreException e) {
            LOG.error("Failed to flush preferences", e);
        }
    }

    private IEclipsePreferences getGlobalPermissionPreferences() {
        return InstanceScope.INSTANCE.getNode(Constants.PREF_NODE_ID_PERMISSIONS);
    }
}
