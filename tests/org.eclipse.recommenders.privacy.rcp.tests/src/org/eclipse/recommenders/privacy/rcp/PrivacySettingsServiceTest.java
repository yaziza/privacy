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

public class PrivacySettingsServiceTest {

    @Test
    public final void testLoadDefaultPreferences() {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        when(preferenceMock.get("some.id", "")).thenReturn("");

        PrivacySettingsService sut = new PrivacySettingsService(preferenceMock);
        assertThat(sut.getState("some.id", "com.example.test"), is(UNKNOWN));
    }

    @Test
    public final void testUnsavedPreferences() {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        when(preferenceMock.get("some.id", "")).thenReturn("");

        PrivacySettingsService sut = new PrivacySettingsService(preferenceMock);
        assertThat(sut.getState("some.id", "com.example.test"), is(UNKNOWN));
    }

    @Test
    public final void testAllowPreferences() throws BackingStoreException {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        when(preferenceMock.get("some.id", "")).thenReturn("+com.example.test;");
        when(preferenceMock.get("other.id", "")).thenReturn("-com.example.test;");
        when(preferenceMock.get("third", "")).thenReturn("");

        PrivacySettingsService sut = new PrivacySettingsService(preferenceMock);
        sut.setState("some.id", "com.example.test", APPROVED);
        sut.setState("other.id", "com.example.test", DISAPPROVED);

        assertThat(sut.getState("some.id", "com.example.test"), is(APPROVED));
        assertThat(sut.getState("some.id", "com.example.other"), is(UNKNOWN));
        assertThat(sut.getState("other.id", "com.example.test"), is(DISAPPROVED));
        assertThat(sut.getState("third", "com.example.test"), is(UNKNOWN));

        verify(preferenceMock, times(2)).flush();
    }

    @Test
    public final void testDisallowPreferences() throws BackingStoreException {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        when(preferenceMock.get("some.id", "")).thenReturn("+com.example.test;");
        when(preferenceMock.get("other.id", "")).thenReturn("+com.example.test;");

        PrivacySettingsService sut = new PrivacySettingsService(preferenceMock);
        sut.setState("some.id", "com.example.test", APPROVED);
        sut.setState("other.id", "com.example.test", APPROVED);

        assertThat(sut.getState("some.id", "com.example.test"), is(APPROVED));
        assertThat(sut.getState("other.id", "com.example.test"), is(APPROVED));

        when(preferenceMock.get("some.id", "")).thenReturn("-com.example.test;");
        when(preferenceMock.get("other.id", "")).thenReturn("-com.example.test;");

        sut.setState("some.id", "com.example.test", DISAPPROVED);
        sut.setState("other.id", "com.example.test", DISAPPROVED);
        assertThat(sut.getState("some.id", "com.example.test"), is(DISAPPROVED));
        assertThat(sut.getState("other.id", "com.example.test"), is(DISAPPROVED));

        verify(preferenceMock, times(4)).flush();
    }

    @Test
    public final void testAllowAfterDisallow() throws BackingStoreException {
        IEclipsePreferences preferenceMock = mock(IEclipsePreferences.class);
        when(preferenceMock.get("some.id", "")).thenReturn("+com.example.test;");
        when(preferenceMock.get("other.id", "")).thenReturn("+com.example.test;");

        PrivacySettingsService sut = new PrivacySettingsService(preferenceMock);
        sut.setState("some.id", "com.example.test", APPROVED);
        sut.setState("other.id", "com.example.test", APPROVED);
        assertThat(sut.getState("some.id", "com.example.test"), is(APPROVED));
        assertThat(sut.getState("other.id", "com.example.test"), is(APPROVED));

        when(preferenceMock.get("some.id", "")).thenReturn("-com.example.test;");
        when(preferenceMock.get("other.id", "")).thenReturn("-com.example.test;");

        sut.setState("some.id", "com.example.test", DISAPPROVED);
        sut.setState("other.id", "com.example.test", DISAPPROVED);
        assertThat(sut.getState("some.id", "com.example.test"), is(DISAPPROVED));
        assertThat(sut.getState("other.id", "com.example.test"), is(DISAPPROVED));

        when(preferenceMock.get("some.id", "")).thenReturn("+com.example.test;");
        when(preferenceMock.get("other.id", "")).thenReturn("+com.example.test;");

        sut.setState("some.id", "com.example.test", APPROVED);
        sut.setState("other.id", "com.example.test", APPROVED);
        assertThat(sut.getState("some.id", "com.example.test"), is(APPROVED));
        assertThat(sut.getState("other.id", "com.example.test"), is(APPROVED));

        verify(preferenceMock, times(6)).flush();
    }
}
