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
package org.eclipse.recommenders.internal.privacy.rcp.services;

import static org.eclipse.recommenders.internal.privacy.rcp.Constants.PREF_NODE_APPROVAL;
import static org.eclipse.recommenders.privacy.rcp.PermissionState.*;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.recommenders.internal.privacy.rcp.l10n.Messages;
import org.eclipse.recommenders.privacy.rcp.IPrivacySettingsService;
import org.eclipse.recommenders.privacy.rcp.PermissionState;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("restriction")
class PrivacySettingsService implements IPrivacySettingsService {

    private static final Logger LOG = LoggerFactory.getLogger(PrivacySettingsService.class);

    private static final String PREF_APPROVED = "+"; //$NON-NLS-1$
    private static final String PREF_DISAPPROVED = "-"; //$NON-NLS-1$

    private final IEclipsePreferences preferences;

    @Inject
    public PrivacySettingsService(@Preference IEclipsePreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public void setState(String principalId, String datumId, PermissionState state) {
        store(principalId, datumId, state);
    }

    @Override
    public PermissionState getState(String principalId, String datumId) {
        Preferences root = preferences.node(PREF_NODE_APPROVAL);
        String state = root.node(datumId).get(principalId, ""); //$NON-NLS-1$

        if (PREF_APPROVED.equals(state)) {
            return APPROVED;
        } else if (PREF_DISAPPROVED.equals(state)) {
            return DISAPPROVED;
        } else {
            return UNKNOWN;
        }
    }

    @Override
    public void approve(String principalId, String datumId) {
        store(datumId, principalId, APPROVED);
    }

    @Override
    public void disapprove(String principalId, String datumId) {
        store(datumId, principalId, DISAPPROVED);
    }

    @Override
    public boolean isApproved(String principalId, String... datumsIds) {
        for (String datumId : datumsIds) {
            PermissionState state = getState(principalId, datumId);
            if (DISAPPROVED.equals(state) || UNKNOWN.equals(state)) {
                return false;
            }
        }
        return datumsIds.length > 0;
    }

    @Override
    public boolean isNotYetDecided(String principalId, String... datumsIds) {
        for (String datumId : datumsIds) {
            PermissionState state = getState(principalId, datumId);
            if (UNKNOWN.equals(state)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAllApproved(String principalId) {
        String[] datums = getDatumsForPrincipal(principalId);
        return isApproved(principalId, datums);
    }

    @Override
    public boolean isNeverApproved(String datumId) {
        Preferences root = preferences.node(PREF_NODE_APPROVAL);
        try {
            if (!root.nodeExists(datumId)) {
                return true;
            }
            Preferences node = root.node(datumId);
            for (String principalId : node.keys()) {
                if (PREF_APPROVED.equals(node.get(principalId, ""))) { //$NON-NLS-1$
                    return false;
                }
            }
        } catch (BackingStoreException e) {
            LOG.error(Messages.LOG_ERROR_LOADING_PREFERENCES, e);
        }
        return true;
    }

    private String[] getDatumsForPrincipal(String principalId) {
        Preferences root = preferences.node(PREF_NODE_APPROVAL);
        ArrayList<String> datums = new ArrayList<String>();

        try {
            for (String datumId : root.childrenNames()) {
                Preferences datum = root.node(datumId);
                if (Arrays.asList(datum.keys()).contains(principalId)) {
                    datums.add(datumId);
                }
            }
        } catch (BackingStoreException e) {
            LOG.error(Messages.LOG_ERROR_LOADING_PREFERENCES, e);
        }
        return datums.toArray(new String[] {});
    }

    private void store(String principalId, String datumId, PermissionState state) {
        String value = PermissionState.APPROVED.equals(state) ? PREF_APPROVED : PREF_DISAPPROVED;

        Preferences root = preferences.node(PREF_NODE_APPROVAL);
        root.node(datumId).put(principalId, value);
        try {
            preferences.flush();
        } catch (BackingStoreException e) {
            LOG.error(Messages.LOG_ERROR_SAVING_PREFERENCES, e);
        }
    }
}
