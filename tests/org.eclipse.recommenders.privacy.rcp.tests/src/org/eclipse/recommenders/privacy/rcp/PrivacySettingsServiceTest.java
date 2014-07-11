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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class PrivacySettingsServiceTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public final void testLoadDefaultPreferences() {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        Preferences root = mock(Preferences.class);
        Preferences prefs = mock(Preferences.class);
        when(preferenceMock.node("approval")).thenReturn(root);
        when(root.node("some.id")).thenReturn(prefs);
        when(prefs.get("com.example.test", "")).thenReturn("");

        PrivacySettingsService sut = new PrivacySettingsService(preferenceMock);
        assertThat(sut.getState("com.example.test", "some.id"), is(UNKNOWN));
    }

    @Test
    public final void testUnsavedPreferences() {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        Preferences root = mock(Preferences.class);
        Preferences prefs = mock(Preferences.class);
        when(preferenceMock.node("approval")).thenReturn(root);
        when(root.node("some.id")).thenReturn(prefs);
        when(prefs.get("com.example.test", "")).thenReturn("");

        PrivacySettingsService sut = new PrivacySettingsService(preferenceMock);
        assertThat(sut.getState("com.example.test", "some.id"), is(UNKNOWN));
    }

    @Test
    public final void testAllowPreferences() throws BackingStoreException {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        Preferences root = mock(Preferences.class);
        Preferences someId = mock(Preferences.class);
        Preferences otherId = mock(Preferences.class);
        Preferences third = mock(Preferences.class);

        when(preferenceMock.node("approval")).thenReturn(root);
        when(root.node("some.id")).thenReturn(someId);
        when(root.node("other.id")).thenReturn(otherId);
        when(root.node("third")).thenReturn(third);

        when(someId.get("com.example.test", "")).thenReturn("+");
        when(someId.get("com.example.other", "")).thenReturn("");
        when(otherId.get("com.example.test", "")).thenReturn("-");
        when(third.get("com.example.test", "")).thenReturn("");

        PrivacySettingsService sut = new PrivacySettingsService(preferenceMock);
        sut.approve("some.id", "com.example.test");
        sut.disapprove("other.id", "com.example.test");

        assertThat(sut.getState("com.example.test", "some.id"), is(APPROVED));
        assertThat(sut.getState("com.example.other", "some.id"), is(UNKNOWN));
        assertThat(sut.getState("com.example.test", "other.id"), is(DISAPPROVED));
        assertThat(sut.getState("com.example.test", "third"), is(UNKNOWN));

        verify(preferenceMock, times(2)).flush();
    }

    @Test
    public final void testDisallowPreferences() throws BackingStoreException {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        Preferences root = mock(Preferences.class);
        Preferences someId = mock(Preferences.class);
        Preferences otherId = mock(Preferences.class);

        when(preferenceMock.node("approval")).thenReturn(root);
        when(root.node("some.id")).thenReturn(someId);
        when(root.node("other.id")).thenReturn(otherId);

        when(someId.get("com.example.test", "")).thenReturn("+");
        when(otherId.get("com.example.test", "")).thenReturn("+");

        PrivacySettingsService sut = new PrivacySettingsService(preferenceMock);
        sut.setState("com.example.test", "some.id", APPROVED);
        sut.setState("com.example.test", "other.id", APPROVED);

        assertThat(sut.getState("com.example.test", "some.id"), is(APPROVED));
        assertThat(sut.getState("com.example.test", "other.id"), is(APPROVED));
        assertThat(sut.isApproved("com.example.test", "some.id", "other.id"), is(true));

        when(someId.get("com.example.test", "")).thenReturn("-");
        when(otherId.get("com.example.test", "")).thenReturn("-");

        sut.setState("com.example.test", "some.id", DISAPPROVED);
        sut.setState("com.example.test", "other.id", DISAPPROVED);
        assertThat(sut.getState("com.example.test", "some.id"), is(DISAPPROVED));
        assertThat(sut.getState("com.example.test", "other.id"), is(DISAPPROVED));
        assertThat(sut.isApproved("com.example.test", "some.id", "other.id"), is(false));

        verify(preferenceMock, times(4)).flush();
    }

    @Test
    public final void testAllowAfterDisallow() throws BackingStoreException {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        Preferences root = mock(Preferences.class);
        Preferences someId = mock(Preferences.class);
        Preferences otherId = mock(Preferences.class);

        when(preferenceMock.node("approval")).thenReturn(root);
        when(root.node("some.id")).thenReturn(someId);
        when(root.node("other.id")).thenReturn(otherId);

        when(someId.get("com.example.test", "")).thenReturn("+");
        when(otherId.get("com.example.test", "")).thenReturn("+");

        PrivacySettingsService sut = new PrivacySettingsService(preferenceMock);
        sut.setState("com.example.test", "some.id", APPROVED);
        sut.setState("com.example.test", "other.id", APPROVED);
        assertThat(sut.getState("com.example.test", "some.id"), is(APPROVED));
        assertThat(sut.getState("com.example.test", "other.id"), is(APPROVED));
        assertThat(sut.isApproved("com.example.test", "some.id", "other.id"), is(true));

        when(someId.get("com.example.test", "")).thenReturn("-");
        when(otherId.get("com.example.test", "")).thenReturn("-");

        sut.setState("com.example.test", "some.id", DISAPPROVED);
        sut.setState("com.example.test", "other.id", DISAPPROVED);
        assertThat(sut.getState("com.example.test", "some.id"), is(DISAPPROVED));
        assertThat(sut.getState("com.example.test", "other.id"), is(DISAPPROVED));
        assertThat(sut.isApproved("com.example.test", "some.id", "other.id"), is(false));

        when(someId.get("com.example.test", "")).thenReturn("+");
        when(otherId.get("com.example.test", "")).thenReturn("+");

        sut.setState("com.example.test", "some.id", APPROVED);
        sut.setState("com.example.test", "other.id", APPROVED);
        assertThat(sut.getState("com.example.test", "some.id"), is(APPROVED));
        assertThat(sut.getState("com.example.test", "other.id"), is(APPROVED));
        assertThat(sut.isApproved("com.example.test", "some.id", "other.id"), is(true));

        verify(preferenceMock, times(6)).flush();
    }

    @Test
    public final void testAllowMultiplePrincipalsForSameDatum() {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        Preferences root = mock(Preferences.class);
        Preferences someId = mock(Preferences.class);

        when(preferenceMock.node("approval")).thenReturn(root);
        when(root.node("some.id")).thenReturn(someId);
        when(someId.get("com.example.first", "")).thenReturn("+");
        when(someId.get("com.example.second", "")).thenReturn("-");
        when(someId.get("com.example.third", "")).thenReturn("+");

        PrivacySettingsService sut = new PrivacySettingsService(preferenceMock);
        sut.setState("com.example.first", "some.id", APPROVED);
        sut.setState("com.example.second", "some.id", DISAPPROVED);
        sut.setState("com.example.third", "some.id", APPROVED);
        assertThat(sut.getState("com.example.first", "some.id"), is(APPROVED));
        assertThat(sut.getState("com.example.second", "some.id"), is(DISAPPROVED));
        assertThat(sut.getState("com.example.third", "some.id"), is(APPROVED));

        assertThat(sut.isApproved("com.example.first", "some.id"), is(true));
        assertThat(sut.isApproved("com.example.second", "some.id"), is(false));
        assertThat(sut.isApproved("com.example.third", "some.id"), is(true));

        when(someId.get("com.example.first", "")).thenReturn("-");

        sut.setState("com.example.first", "some.id", DISAPPROVED);
        assertThat(sut.getState("com.example.first", "some.id"), is(DISAPPROVED));
        assertThat(sut.getState("com.example.second", "some.id"), is(DISAPPROVED));
        assertThat(sut.getState("com.example.third", "some.id"), is(APPROVED));

        assertThat(sut.isApproved("com.example.first", "some.id"), is(false));
        assertThat(sut.isApproved("com.example.second", "some.id"), is(false));
        assertThat(sut.isApproved("com.example.third", "some.id"), is(true));

        when(someId.get("com.example.first", "")).thenReturn("+");
        when(someId.get("com.example.second", "")).thenReturn("+");

        sut.setState("com.example.first", "some.id", APPROVED);
        sut.setState("com.example.second", "some.id", APPROVED);
        assertThat(sut.getState("com.example.first", "some.id"), is(APPROVED));
        assertThat(sut.getState("com.example.second", "some.id"), is(APPROVED));
        assertThat(sut.getState("com.example.third", "some.id"), is(APPROVED));

        assertThat(sut.isApproved("com.example.first", "some.id"), is(true));
        assertThat(sut.isApproved("com.example.second", "some.id"), is(true));
        assertThat(sut.isApproved("com.example.third", "some.id"), is(true));
    }

    @Test
    public final void testAllowMultiplePrincipalsForMultipleDatums() {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        Preferences root = mock(Preferences.class);
        Preferences someId = mock(Preferences.class);
        Preferences otherId = mock(Preferences.class);

        when(preferenceMock.node("approval")).thenReturn(root);
        when(root.node("some.id")).thenReturn(someId);
        when(root.node("other.id")).thenReturn(otherId);
        when(someId.get("com.example.first", "")).thenReturn("+");
        when(someId.get("com.example.second", "")).thenReturn("-");
        when(someId.get("com.example.third", "")).thenReturn("+");
        when(someId.get("unknown", "")).thenReturn("");

        when(otherId.get("com.example.first", "")).thenReturn("-");
        when(otherId.get("com.example.second", "")).thenReturn("+");
        when(otherId.get("com.example.third", "")).thenReturn("-");
        when(otherId.get("unknown", "")).thenReturn("");

        PrivacySettingsService sut = new PrivacySettingsService(preferenceMock);
        sut.setState("com.example.first", "some.id", APPROVED);
        sut.setState("com.example.second", "some.id", DISAPPROVED);
        sut.setState("com.example.third", "some.id", APPROVED);

        sut.setState("com.example.first", "other.id", DISAPPROVED);
        sut.setState("com.example.second", "other.id", APPROVED);
        sut.setState("com.example.third", "other.id", DISAPPROVED);

        assertThat(sut.getState("com.example.first", "some.id"), is(APPROVED));
        assertThat(sut.getState("com.example.second", "some.id"), is(DISAPPROVED));
        assertThat(sut.getState("com.example.third", "some.id"), is(APPROVED));
        assertThat(sut.getState("unknown", "some.id"), is(UNKNOWN));

        assertThat(sut.getState("com.example.first", "other.id"), is(DISAPPROVED));
        assertThat(sut.getState("com.example.second", "other.id"), is(APPROVED));
        assertThat(sut.getState("com.example.third", "other.id"), is(DISAPPROVED));
        assertThat(sut.getState("unknown", "other.id"), is(UNKNOWN));

        when(someId.get("com.example.first", "")).thenReturn("-");
        when(otherId.get("com.example.second", "")).thenReturn("-");

        sut.setState("com.example.first", "some.id", DISAPPROVED);
        assertThat(sut.getState("com.example.first", "some.id"), is(DISAPPROVED));
        assertThat(sut.getState("com.example.second", "some.id"), is(DISAPPROVED));
        assertThat(sut.getState("com.example.third", "some.id"), is(APPROVED));

        assertThat(sut.getState("com.example.first", "other.id"), is(DISAPPROVED));
        assertThat(sut.getState("com.example.second", "other.id"), is(DISAPPROVED));
        assertThat(sut.getState("com.example.third", "other.id"), is(DISAPPROVED));

        when(someId.get("com.example.first", "")).thenReturn("+");
        when(someId.get("com.example.second", "")).thenReturn("+");

        when(otherId.get("com.example.first", "")).thenReturn("+");
        when(otherId.get("com.example.second", "")).thenReturn("+");

        sut.setState("com.example.first", "some.id", APPROVED);
        sut.setState("com.example.second", "some.id", APPROVED);
        assertThat(sut.getState("com.example.first", "some.id"), is(APPROVED));
        assertThat(sut.getState("com.example.second", "some.id"), is(APPROVED));
        assertThat(sut.getState("com.example.third", "some.id"), is(APPROVED));
        assertThat(sut.getState("unknown", "some.id"), is(UNKNOWN));

        assertThat(sut.getState("com.example.first", "other.id"), is(APPROVED));
        assertThat(sut.getState("com.example.second", "other.id"), is(APPROVED));
        assertThat(sut.getState("com.example.third", "other.id"), is(DISAPPROVED));
        assertThat(sut.getState("unknown", "other.id"), is(UNKNOWN));

        assertThat(sut.isApproved("com.example.first", "some.id", "other.id"), is(true));
        assertThat(sut.isApproved("com.example.second", "some.id", "other.id"), is(true));
        assertThat(sut.isApproved("com.example.third", "some.id", "other.id"), is(false));
        assertThat(sut.isApproved("unknown", "some.id", "other.id"), is(false));
    }

    @Test
    public final void testIsNeverApproved() throws BackingStoreException {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        Preferences root = mock(Preferences.class);
        Preferences someId = mock(Preferences.class);
        Preferences otherId = mock(Preferences.class);
        Preferences third = mock(Preferences.class);

        when(preferenceMock.node("approval")).thenReturn(root);
        when(root.childrenNames()).thenReturn(new String[] { "some.id", "other.id", "third" });
        when(root.nodeExists("some.id")).thenReturn(true);
        when(root.nodeExists("other.id")).thenReturn(true);
        when(root.nodeExists("third")).thenReturn(true);

        when(root.node("some.id")).thenReturn(someId);
        when(root.node("other.id")).thenReturn(otherId);
        when(root.node("third")).thenReturn(third);

        when(someId.keys()).thenReturn(new String[] { "com.example.first", "com.example.second" });
        when(someId.get("com.example.first", "")).thenReturn("+");
        when(someId.get("com.example.second", "")).thenReturn("+");

        when(otherId.keys()).thenReturn(new String[] { "com.example.first", "com.example.second" });
        when(otherId.get("com.example.first", "")).thenReturn("-");
        when(otherId.get("com.example.second", "")).thenReturn("-");

        when(third.keys()).thenReturn(new String[] { "com.example.first", "com.example.second" });
        when(third.get("com.example.first", "")).thenReturn("+");
        when(third.get("com.example.second", "")).thenReturn("-");

        PrivacySettingsService sut = new PrivacySettingsService(preferenceMock);

        assertThat(sut.isNeverApproved("some.id"), is(false));
        assertThat(sut.isNeverApproved("other.id"), is(true));
        assertThat(sut.isNeverApproved("third"), is(false));
        assertThat(sut.isNeverApproved("non.existing.datum"), is(true));

        assertThat(sut.isApproved("com.example.first", "some.id", "third"), is(true));
        assertThat(sut.isApproved("com.example.first"), is(false));
        assertThat(sut.isAllApproved("com.example.first"), is(false));
        assertThat(sut.isApproved("com.example.second", "some.id", "other.id"), is(false));
        assertThat(sut.isApproved("com.example.second"), is(false));
    }

    @Test
    public final void testIsAllApproved() throws BackingStoreException {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        Preferences root = mock(Preferences.class);
        Preferences someId = mock(Preferences.class);
        Preferences otherId = mock(Preferences.class);
        Preferences third = mock(Preferences.class);

        when(preferenceMock.node("approval")).thenReturn(root);
        when(root.childrenNames()).thenReturn(new String[] { "some.id", "other.id", "third" });
        when(root.nodeExists("some.id")).thenReturn(true);
        when(root.nodeExists("other.id")).thenReturn(true);
        when(root.nodeExists("third")).thenReturn(true);

        when(root.node("some.id")).thenReturn(someId);
        when(root.node("other.id")).thenReturn(otherId);
        when(root.node("third")).thenReturn(third);

        when(someId.keys()).thenReturn(new String[] { "com.example.first", "com.example.second" });
        when(someId.get("com.example.first", "")).thenReturn("+");
        when(someId.get("com.example.second", "")).thenReturn("+");
        when(someId.get("com.example.third", "")).thenReturn("");

        when(otherId.keys()).thenReturn(new String[] { "com.example.first", "com.example.second" });
        when(otherId.get("com.example.first", "")).thenReturn("+");
        when(otherId.get("com.example.second", "")).thenReturn("+");
        when(otherId.get("com.example.third", "")).thenReturn("");

        when(third.keys()).thenReturn(new String[] { "com.example.first", "com.example.second" });
        when(third.get("com.example.first", "")).thenReturn("+");
        when(third.get("com.example.second", "")).thenReturn("-");
        when(third.get("com.example.third", "")).thenReturn("");

        PrivacySettingsService sut = new PrivacySettingsService(preferenceMock);

        assertThat(sut.isAllApproved("com.example.first"), is(true));
        assertThat(sut.isApproved("com.example.first"), is(false));
        assertThat(sut.isApproved("com.example.first", "some.id"), is(true));
        assertThat(sut.isApproved("com.example.first", "some.id", "other.id", "third"), is(true));

        assertThat(sut.isAllApproved("com.example.second"), is(false));
        assertThat(sut.isApproved("com.example.second"), is(false));
        assertThat(sut.isApproved("com.example.second", "some.id"), is(true));
        assertThat(sut.isApproved("com.example.second", "some.id", "other.id"), is(true));
        assertThat(sut.isApproved("com.example.second", "some.id", "other.id", "third"), is(false));

        assertThat(sut.isAllApproved("com.example.third"), is(false));
        assertThat(sut.isApproved("com.example.third"), is(false));
        assertThat(sut.isApproved("com.example.third", "some.id"), is(false));
        assertThat(sut.isApproved("com.example.third", "some.id", "other.id"), is(false));
    }

    @Test
    public final void testGetUserIdIsNotNull() throws IOException {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        PrivacySettingsService sut = new PrivacySettingsService(preferenceMock, folder.newFile());

        UUID userId = sut.getUserId();

        assertThat(userId, is(notNullValue()));
    }

    @Test
    public final void testGetUserIdIsIdempotent() throws IOException {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        PrivacySettingsService sut = new PrivacySettingsService(preferenceMock, folder.newFile());

        UUID firstUserId = sut.getUserId();
        UUID secondUserId = sut.getUserId();

        assertThat(secondUserId, is(equalTo(firstUserId)));
    }

    @Test
    public final void testGenerateUserId() throws IOException {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        PrivacySettingsService sut = new PrivacySettingsService(preferenceMock, folder.newFile());

        UUID firstUserId = sut.getUserId();
        sut.generateUserId();
        UUID secondUserId = sut.getUserId();

        assertThat(secondUserId, is(not(equalTo(firstUserId))));
    }

    @Test
    public final void testGetUserIdWithWriteProtectedUserIdFile() throws IOException {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        File writeProtectedFolder = folder.newFolder();
        writeProtectedFolder.setWritable(false);
        PrivacySettingsService sut = new PrivacySettingsService(preferenceMock, writeProtectedFolder);

        assertThat(writeProtectedFolder.canWrite(), is(false));

        UUID firstUserId = sut.getUserId();

        assertThat(firstUserId, is(notNullValue()));

        UUID secondUserId = sut.getUserId();

        assertThat(secondUserId, is(equalTo(firstUserId)));
    }

    @Test
    public final void testGetUserIdWithReadProtectedUserIdFile() throws IOException {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        File userIdFile = folder.newFile();
        PrivacySettingsService firstSession = new PrivacySettingsService(preferenceMock, userIdFile);

        firstSession.generateUserId();

        userIdFile.setReadable(false);
        assertThat(userIdFile.canRead(), is(false));

        PrivacySettingsService secondSession = new PrivacySettingsService(preferenceMock, userIdFile);

        UUID firstUserId = secondSession.getUserId();

        assertThat(firstUserId, is(notNullValue()));
    }
}
