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
    public void setState(String datumId, String principalId, PermissionState state) {
        store(datumId, principalId, state);
    }

    @Override
    public PermissionState getState(String datumId, String principalId) {
        String value = getGlobalPermissionPreferences().get(datumId, ""); //$NON-NLS-1$
        if (!value.contains(Constants.PREF_SEPARATOR)) {
            return PermissionState.UNKNOWN;
        }
        for (String principal : value.split(Constants.PREF_SEPARATOR)) {
            char prefix = principal.charAt(0);
            if (principal.substring(1).equals(principalId)) {
                return prefix == Constants.PREF_APPROVED ? PermissionState.APPROVED : PermissionState.DISAPPROVED;
            }
        }
        return PermissionState.UNKNOWN;
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
        StringBuilder newValue = new StringBuilder();
        String oldValue = getGlobalPermissionPreferences().get(datumId, ""); //$NON-NLS-1$

        for (String principal : oldValue.split(Constants.PREF_SEPARATOR)) {
            if (principal.isEmpty() || principal.substring(1).equals(principalId)) {
                char prefix = state.equals(PermissionState.APPROVED) ? Constants.PREF_APPROVED
                        : Constants.PREF_DISAPPROVED;
                principal = prefix + principalId;
            }
            newValue.append(principal + Constants.PREF_SEPARATOR);
        }

        IEclipsePreferences prefs = getGlobalPermissionPreferences();
        prefs.put(datumId, newValue.toString());
        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            LOG.error("Failed to flush preferences", e); //$NON-NLS-1$
        }
    }

    private IEclipsePreferences getGlobalPermissionPreferences() {
        return InstanceScope.INSTANCE.getNode(qualifier);
    }
}
