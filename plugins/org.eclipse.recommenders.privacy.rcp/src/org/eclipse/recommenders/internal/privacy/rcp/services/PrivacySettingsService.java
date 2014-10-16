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

import static org.eclipse.recommenders.internal.privacy.rcp.Constants.*;
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
import org.eclipse.recommenders.privacy.rcp.IPrivacySettingsService;
import org.eclipse.recommenders.privacy.rcp.PermissionState;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

@SuppressWarnings("restriction")
class PrivacySettingsService implements IPrivacySettingsService {

    private static final Logger LOG = LoggerFactory.getLogger(PrivacySettingsService.class);

    private static final String PREF_ROOT_NODE = "approval"; //$NON-NLS-1$
    private static final String PREF_APPROVED = "+"; //$NON-NLS-1$
    private static final String PREF_DISAPPROVED = "-"; //$NON-NLS-1$

    private static final String USER_HOME = "user.home"; //$NON-NLS-1$

    private static final String DOT_ECLIPSE_DIRECTORY_NAME = ".eclipse"; //$NON-NLS-1$
    private static final String ANONYMOUS_ID_FILE_NAME = "anonymousId"; //$NON-NLS-1$

    private final IEclipsePreferences preferences;
    private final File anonymousIdFile;
    private UUID anonymousId;

    @Inject
    public PrivacySettingsService(
            @Preference(nodePath = PREF_NODE_GLOBAL_PERMISSIONS_PATH, value = PREF_NODE_GLOBAL_PERMISSIONS_VALUE) IEclipsePreferences preferences) {
        this(preferences, createAnonymousIdFile());
    }

    @VisibleForTesting()
    PrivacySettingsService(IEclipsePreferences preferences, File anonymousIdFile) {
        this.preferences = preferences;
        if (anonymousIdFile.isDirectory()) {
            this.anonymousIdFile = new File(anonymousIdFile, ANONYMOUS_ID_FILE_NAME);
        } else {
            this.anonymousIdFile = anonymousIdFile;
        }
    }

    private static File createAnonymousIdFile() {
        File userHome = new File(System.getProperty(USER_HOME));
        File dotEclipseDir = new File(userHome, DOT_ECLIPSE_DIRECTORY_NAME);
        File bundleDir = new File(dotEclipseDir, BUNDLE_ID);

        if (!bundleDir.exists()) {
            bundleDir.mkdirs();
        }

        return new File(bundleDir, ANONYMOUS_ID_FILE_NAME);
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
    public boolean isApproved(String principalId, String... datumsIds) {
        for (String datumId : datumsIds) {
            PermissionState state = getState(principalId, datumId);
            if (state.equals(DISAPPROVED) || state.equals(UNKNOWN)) {
                return false;
            }
        }
        return datumsIds.length > 0;
    }

    @Override
    public boolean isAllApproved(String principalId) {
        String[] datums = getDatumsForPrincipal(principalId);
        return isApproved(principalId, datums);
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

    private String[] getDatumsForPrincipal(String principalId) {
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
    public UUID getAnonymousId() {
        if (anonymousId == null) {
            if (anonymousIdFile.exists()) {
                try {
                    anonymousId = readAnonymousIdFromFile(anonymousIdFile);
                } catch (IOException e) {
                    LOG.error("Failed to read Anonymous ID from file \"{}\"", anonymousIdFile, e); //$NON-NLS-1$
                    generateAnonymousId();
                }
            } else {
                generateAnonymousId();
            }
        }

        return anonymousId;
    }

    private UUID readAnonymousIdFromFile(File file) throws IOException {
        final RandomAccessFile f = new RandomAccessFile(file, "r"); //$NON-NLS-1$
        final byte[] bytes = new byte[(int) f.length()];
        try {
            f.readFully(bytes);
            return UUID.fromString(new String(bytes));
        } catch (IllegalArgumentException e) {
            throw new IOException(e);
        } finally {
            f.close();
        }
    }

    @Override
    public void generateAnonymousId() {
        UUID freshAnonymousId;
        do {
            freshAnonymousId = UUID.randomUUID();
        } while (freshAnonymousId.equals(anonymousId));
        anonymousId = freshAnonymousId;

        try {
            writeAnonymousIdToFile(anonymousIdFile, anonymousId);
        } catch (IOException e) {
            LOG.error("Failed to write Anonymous ID to file \"{}\"", anonymousIdFile, e); //$NON-NLS-1$
        }
    }

    private void writeAnonymousIdToFile(File file, UUID anonymousId) throws IOException {
        final FileOutputStream out = new FileOutputStream(file);
        try {
            out.write(anonymousId.toString().getBytes());
        } finally {
            out.close();
        }
    }
}
