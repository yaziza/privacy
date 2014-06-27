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

import static org.eclipse.recommenders.privacy.rcp.PermissionState.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

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
    private static final String USER_HOME = "user.home"; //$NON-NLS-1$
    private static final String USER_ID_FILE_NAME = "/.eclipse/userId"; //$NON-NLS-1$

    private final String userHomePath;
    private IEclipsePreferences preferences;

    @Inject
    public PrivacySettingsService(
            @Preference(value = Constants.PREF_NODE_ID_GLOBAL_PERMISSIONS) IEclipsePreferences preferences) {
        this.preferences = preferences;
        this.userHomePath = System.getProperty(USER_HOME);

    }

    @Override
    public void setState(String principalId, String datumId, PermissionState state) {
        store(principalId, datumId, state);
    }

    @Override
    public PermissionState getState(String principalId, String datumId) {
        Preferences root = preferences.node(PREF_ROOT_NODE);
        String state = root.node(datumId).get(principalId, ""); //$NON-NLS-1$

        if (state.equals(PREF_APPROVED)) {
            return APPROVED;
        } else if (state.equals(PREF_DISAPPROVED)) {
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
    public boolean isAllApproved(String principalId) {
        String[] datums = retrievePrincipalDatums(principalId);
        return isApproved(principalId, datums);
    }

    private String[] retrievePrincipalDatums(String principalId) {
        Preferences root = preferences.node(PREF_ROOT_NODE);
        ArrayList<String> datums = new ArrayList<String>();

        try {
            for (String datumId : root.childrenNames()) {
                Preferences datum = root.node(datumId);
                if (Arrays.asList(datum.keys()).contains(principalId)) {
                    datums.add(datumId);
                }
            }
        } catch (BackingStoreException e) {
            LOG.error("Failed to load preferences", e); //$NON-NLS-1$
        }
        return datums.toArray(new String[] {});
    }

    @Override
    public boolean isApproved(String principalId, String... datumsIds) {
        for (String datumId : datumsIds) {
            PermissionState state = getState(principalId, datumId);
            if (state.equals(DISAPPROVED) || state.equals(UNKNOWN)) {
                return false;
            }
        }
        return datumsIds.length > 0;
    }

    private void store(String principalId, String datumId, PermissionState state) {
        String value = state.equals(PermissionState.APPROVED) ? PREF_APPROVED : PREF_DISAPPROVED;

        Preferences root = preferences.node(PREF_ROOT_NODE);
        root.node(datumId).put(principalId, value);
        try {
            preferences.flush();
        } catch (BackingStoreException e) {
            LOG.error("Failed to flush preferences", e); //$NON-NLS-1$
        }
    }

    @Override
    public boolean isNeverApproved(String datumId) {
        Preferences root = preferences.node(PREF_ROOT_NODE);
        try {
            if (!root.nodeExists(datumId)) {
                return true;
            }
            Preferences node = root.node(datumId);
            for (String principalId : node.keys()) {
                if (node.get(principalId, "").equals(PREF_APPROVED)) { //$NON-NLS-1$
                    return false;
                }
            }
        } catch (BackingStoreException e) {
            LOG.error("Failed to load preferences", e); //$NON-NLS-1$
        }
        return true;
    }

    @Override
    public void generateUserId() {
        try {
            storeUserIdFile(userHomePath, USER_ID_FILE_NAME);
        } catch (IOException e) {
            LOG.error("Failed to store User ID ", e); //$NON-NLS-1$
        }
    }

    private void storeUserIdFile(String path, String name) throws IOException {
        File file = new File(path, name);
        final FileOutputStream out = new FileOutputStream(file);
        try {
            final String userId = UUID.randomUUID().toString();
            out.write(userId.getBytes());
        } finally {
            out.close();
        }
    }

    @Override
    public UUID getUserId() {
        File file = new File(userHomePath, USER_ID_FILE_NAME);
        if (file.exists()) {
            try {
                return readUserIdFile(userHomePath, USER_ID_FILE_NAME);
            } catch (IOException e) {
                LOG.error("Failed to read User ID ", e); //$NON-NLS-1$
            }
        } else {
            generateUserId();
            try {
                return readUserIdFile(userHomePath, USER_ID_FILE_NAME);
            } catch (IOException e) {
                LOG.error("Failed to read User ID ", e); //$NON-NLS-1$
            }
        }
        return null;
    }

    private UUID readUserIdFile(String path, String name) throws IOException {
        File file = new File(path, name);
        final RandomAccessFile f = new RandomAccessFile(file, "r"); //$NON-NLS-1$
        final byte[] bytes = new byte[(int) f.length()];
        try {
            f.readFully(bytes);
        } finally {
            f.close();
        }
        return UUID.fromString(new String(bytes));
    }
}
