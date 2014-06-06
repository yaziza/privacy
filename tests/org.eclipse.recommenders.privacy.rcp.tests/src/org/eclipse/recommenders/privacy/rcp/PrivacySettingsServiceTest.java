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

import org.junit.Test;

public class PrivacySettingsServiceTest {

    @Test
    public final void testLoadDefaultPreferences() {
        PrivacySettingsService sut = new PrivacySettingsService("testLoadDefaultPreferences");
        assertThat(sut.getState("some.id", "com.example.test"), is(UNKNOWN));
    }

    @Test
    public final void testUnsavedPreferences() {
        PrivacySettingsService sut = new PrivacySettingsService("testUnsavedPreferences");
        assertThat(sut.getState("some.id", "com.example.test"), is(UNKNOWN));
        assertThat(sut.getState("some.id", "com.example.test"), is(UNKNOWN));
    }

    @Test
    public final void testAllowPreferences() {
        PrivacySettingsService sut = new PrivacySettingsService("testAllowPreferences");
        sut.setState("some.id", "com.example.test", APPROVED);
        sut.setState("other.id", "com.example.test", DISAPPROVED);
        assertThat(sut.getState("some.id", "com.example.test"), is(APPROVED));
        assertThat(sut.getState("other.id", "com.example.test"), is(DISAPPROVED));
        assertThat(sut.getState("some.id", "com.example.other"), is(UNKNOWN));
        assertThat(sut.getState("third", "com.example.test"), is(UNKNOWN));
    }

    @Test
    public final void testDisallowPreferences() {
        PrivacySettingsService sut = new PrivacySettingsService("testDisallowPreferences");
        sut.setState("some.id", "com.example.test", APPROVED);
        assertThat(sut.getState("some.id", "com.example.test"), is(APPROVED));
        sut.setState("some.id", "com.example.test", DISAPPROVED);
        assertThat(sut.getState("some.id", "com.example.test"), is(DISAPPROVED));

        sut.setState("other.id", "com.example.test", APPROVED);
        assertThat(sut.getState("other.id", "com.example.test"), is(APPROVED));
        sut.setState("other.id", "com.example.test", DISAPPROVED);
        assertThat(sut.getState("other.id", "com.example.test"), is(DISAPPROVED));
    }

    @Test
    public final void testAllowAfterDisallow() {
        PrivacySettingsService sut = new PrivacySettingsService("testAllowAfterDisallow");
        sut.setState("some.id", "com.example.test", APPROVED);
        sut.setState("other.id", "com.example.test", APPROVED);
        assertThat(sut.getState("some.id", "com.example.test"), is(APPROVED));
        assertThat(sut.getState("other.id", "com.example.test"), is(APPROVED));

        sut.setState("some.id", "com.example.test", DISAPPROVED);
        sut.setState("other.id", "com.example.test", DISAPPROVED);
        assertThat(sut.getState("some.id", "com.example.test"), is(DISAPPROVED));
        assertThat(sut.getState("other.id", "com.example.test"), is(DISAPPROVED));

        sut.setState("some.id", "com.example.test", APPROVED);
        sut.setState("other.id", "com.example.test", APPROVED);
        assertThat(sut.getState("some.id", "com.example.test"), is(APPROVED));
        assertThat(sut.getState("other.id", "com.example.test"), is(APPROVED));
    }
}
