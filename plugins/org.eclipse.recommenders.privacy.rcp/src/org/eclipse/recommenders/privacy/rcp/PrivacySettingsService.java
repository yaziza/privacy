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

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrivacySettingsService implements IPrivacySettingsService {

    private static final Logger LOG = LoggerFactory.getLogger(PrivacySettingsService.class);

    private static final String PREF_APPROVED = "+"; //$NON-NLS-1$
    private static final String PREF_DISAPPROVED = "-"; //$NON-NLS-1$
    private static final String PREF_ROOT_NODE = "approval"; //$NON-NLS-1$

    private IEclipsePreferences preferences;

    @Inject
    public PrivacySettingsService(
            @Preference(value = Constants.PREF_NODE_ID_GLOBAL_PERMISSIONS) IEclipsePreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public void setState(String datumId, String principalId, PermissionState state) {
        store(datumId, principalId, state);
    }

    @Override
    public PermissionState getState(String datumId, String principalId) {
        Preferences root = preferences.node(PREF_ROOT_NODE);
        String state = root.node(datumId).get(principalId, ""); //$NON-NLS-1$

        if (state.equals(PREF_APPROVED)) {
            return PermissionState.APPROVED;
        } else if (state.equals(PREF_DISAPPROVED)) {
            return PermissionState.DISAPPROVED;
        } else {
            return PermissionState.UNKNOWN;
        }
    }

    @Override
    public void approve(String datumId, String principalId) {
        store(datumId, principalId, PermissionState.APPROVED);
    }

    @Override
    public void disapprove(String datumId, String principalId) {
        store(datumId, principalId, PermissionState.DISAPPROVED);
    }

    @Override
    public boolean isApproved(String datumId, String principalId) {
        return getState(datumId, principalId).equals(PermissionState.APPROVED);
    }

    private void store(String datumId, String principalId, PermissionState state) {
        String value = state.equals(PermissionState.APPROVED) ? PREF_APPROVED : PREF_DISAPPROVED;

        Preferences root = preferences.node(PREF_ROOT_NODE);
        root.node(datumId).put(principalId, value);
        try {
            preferences.flush();
        } catch (BackingStoreException e) {
            LOG.error("Failed to flush preferences", e); //$NON-NLS-1$
        }
    }
}
