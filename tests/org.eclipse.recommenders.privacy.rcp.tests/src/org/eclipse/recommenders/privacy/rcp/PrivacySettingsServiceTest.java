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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.junit.Test;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class PrivacySettingsServiceTest {

    @Test
    public final void testLoadDefaultPreferences() {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        Preferences root = mock(Preferences.class);
        Preferences prefs = mock(Preferences.class);
        when(preferenceMock.node("approval")).thenReturn(root);
        when(root.node("some.id")).thenReturn(prefs);
        when(prefs.get("com.example.test", "")).thenReturn("");

        PrivacySettingsService sut = new PrivacySettingsService(preferenceMock);
        assertThat(sut.getState("some.id", "com.example.test"), is(UNKNOWN));
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
        assertThat(sut.getState("some.id", "com.example.test"), is(UNKNOWN));
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

        assertThat(sut.getState("some.id", "com.example.test"), is(APPROVED));
        assertThat(sut.getState("some.id", "com.example.other"), is(UNKNOWN));
        assertThat(sut.getState("other.id", "com.example.test"), is(DISAPPROVED));
        assertThat(sut.getState("third", "com.example.test"), is(UNKNOWN));

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
        sut.setState("some.id", "com.example.test", APPROVED);
        sut.setState("other.id", "com.example.test", APPROVED);

        assertThat(sut.getState("some.id", "com.example.test"), is(APPROVED));
        assertThat(sut.getState("other.id", "com.example.test"), is(APPROVED));

        when(someId.get("com.example.test", "")).thenReturn("-");
        when(otherId.get("com.example.test", "")).thenReturn("-");

        sut.setState("some.id", "com.example.test", DISAPPROVED);
        sut.setState("other.id", "com.example.test", DISAPPROVED);
        assertThat(sut.getState("some.id", "com.example.test"), is(DISAPPROVED));
        assertThat(sut.getState("other.id", "com.example.test"), is(DISAPPROVED));

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
        sut.setState("some.id", "com.example.test", APPROVED);
        sut.setState("other.id", "com.example.test", APPROVED);
        assertThat(sut.getState("some.id", "com.example.test"), is(APPROVED));
        assertThat(sut.getState("other.id", "com.example.test"), is(APPROVED));

        when(someId.get("com.example.test", "")).thenReturn("-");
        when(otherId.get("com.example.test", "")).thenReturn("-");

        sut.setState("some.id", "com.example.test", DISAPPROVED);
        sut.setState("other.id", "com.example.test", DISAPPROVED);
        assertThat(sut.getState("some.id", "com.example.test"), is(DISAPPROVED));
        assertThat(sut.getState("other.id", "com.example.test"), is(DISAPPROVED));

        when(someId.get("com.example.test", "")).thenReturn("+");
        when(otherId.get("com.example.test", "")).thenReturn("+");

        sut.setState("some.id", "com.example.test", APPROVED);
        sut.setState("other.id", "com.example.test", APPROVED);
        assertThat(sut.getState("some.id", "com.example.test"), is(APPROVED));
        assertThat(sut.getState("other.id", "com.example.test"), is(APPROVED));

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
        sut.setState("some.id", "com.example.first", APPROVED);
        sut.setState("some.id", "com.example.second", DISAPPROVED);
        sut.setState("some.id", "com.example.third", APPROVED);
        assertThat(sut.getState("some.id", "com.example.first"), is(APPROVED));
        assertThat(sut.getState("some.id", "com.example.second"), is(DISAPPROVED));
        assertThat(sut.getState("some.id", "com.example.third"), is(APPROVED));

        when(someId.get("com.example.first", "")).thenReturn("-");

        sut.setState("some.id", "com.example.first", DISAPPROVED);
        assertThat(sut.getState("some.id", "com.example.first"), is(DISAPPROVED));
        assertThat(sut.getState("some.id", "com.example.second"), is(DISAPPROVED));
        assertThat(sut.getState("some.id", "com.example.third"), is(APPROVED));

        when(someId.get("com.example.first", "")).thenReturn("+");
        when(someId.get("com.example.second", "")).thenReturn("+");

        sut.setState("some.id", "com.example.first", APPROVED);
        sut.setState("some.id", "com.example.second", APPROVED);
        assertThat(sut.getState("some.id", "com.example.first"), is(APPROVED));
        assertThat(sut.getState("some.id", "com.example.second"), is(APPROVED));
        assertThat(sut.getState("some.id", "com.example.third"), is(APPROVED));
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
        sut.setState("some.id", "com.example.first", APPROVED);
        sut.setState("some.id", "com.example.second", DISAPPROVED);
        sut.setState("some.id", "com.example.third", APPROVED);

        sut.setState("other.id", "com.example.first", DISAPPROVED);
        sut.setState("other.id", "com.example.second", APPROVED);
        sut.setState("other.id", "com.example.third", DISAPPROVED);

        assertThat(sut.getState("some.id", "com.example.first"), is(APPROVED));
        assertThat(sut.getState("some.id", "com.example.second"), is(DISAPPROVED));
        assertThat(sut.getState("some.id", "com.example.third"), is(APPROVED));
        assertThat(sut.getState("some.id", "unknown"), is(UNKNOWN));

        assertThat(sut.getState("other.id", "com.example.first"), is(DISAPPROVED));
        assertThat(sut.getState("other.id", "com.example.second"), is(APPROVED));
        assertThat(sut.getState("other.id", "com.example.third"), is(DISAPPROVED));
        assertThat(sut.getState("other.id", "unknown"), is(UNKNOWN));

        when(someId.get("com.example.first", "")).thenReturn("-");
        when(otherId.get("com.example.second", "")).thenReturn("-");

        sut.setState("some.id", "com.example.first", DISAPPROVED);
        assertThat(sut.getState("some.id", "com.example.first"), is(DISAPPROVED));
        assertThat(sut.getState("some.id", "com.example.second"), is(DISAPPROVED));
        assertThat(sut.getState("some.id", "com.example.third"), is(APPROVED));

        assertThat(sut.getState("other.id", "com.example.first"), is(DISAPPROVED));
        assertThat(sut.getState("other.id", "com.example.second"), is(DISAPPROVED));
        assertThat(sut.getState("other.id", "com.example.third"), is(DISAPPROVED));

        when(someId.get("com.example.first", "")).thenReturn("+");
        when(someId.get("com.example.second", "")).thenReturn("+");

        when(otherId.get("com.example.first", "")).thenReturn("+");
        when(otherId.get("com.example.second", "")).thenReturn("+");

        sut.setState("some.id", "com.example.first", APPROVED);
        sut.setState("some.id", "com.example.second", APPROVED);
        assertThat(sut.getState("some.id", "com.example.first"), is(APPROVED));
        assertThat(sut.getState("some.id", "com.example.second"), is(APPROVED));
        assertThat(sut.getState("some.id", "com.example.third"), is(APPROVED));
        assertThat(sut.getState("some.id", "unknown"), is(UNKNOWN));

        assertThat(sut.getState("other.id", "com.example.first"), is(APPROVED));
        assertThat(sut.getState("other.id", "com.example.second"), is(APPROVED));
        assertThat(sut.getState("other.id", "com.example.third"), is(DISAPPROVED));
        assertThat(sut.getState("other.id", "unknown"), is(UNKNOWN));
    }

    @Test
    public final void testIsNeverApproved() throws BackingStoreException {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        Preferences root = mock(Preferences.class);
        Preferences someId = mock(Preferences.class);
        Preferences otherId = mock(Preferences.class);
        Preferences third = mock(Preferences.class);

        when(preferenceMock.node("approval")).thenReturn(root);
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
    }
}
