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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class PrivacySettingsServiceTest {

    @Test
    public final void testLoadDefaultPreferences() {
        PrivacySettingsService sut = new PrivacySettingsService("testLoadDefaultPreferences");
        assertThat(sut.isAllowed("some.id", "com.example.test"), is(false));
    }

    @Test
    public final void testUnsavedPreferences() {
        PrivacySettingsService sut = new PrivacySettingsService("testUnsavedPreferences");
        assertThat(sut.isAllowed("some.id", "com.example.test"), is(false));
        assertThat(sut.isAllowed("some.id", "com.example.test"), is(false));
    }

    @Test
    public final void testAllowPreferences() {
        PrivacySettingsService sut = new PrivacySettingsService("testAllowPreferences");
        sut.allow("some.id", "com.example.test");
        sut.allow("other.id", "com.example.test");
        assertThat(sut.isAllowed("some.id", "com.example.test"), is(true));
        assertThat(sut.isAllowed("other.id", "com.example.test"), is(true));
        assertThat(sut.isAllowed("some.id", "com.example.other"), is(false));
        assertThat(sut.isAllowed("third", "com.example.test"), is(false));
    }

    @Test
    public final void testDisallowPreferences() {
        PrivacySettingsService sut = new PrivacySettingsService("testDisallowPreferences");
        sut.allow("some.id", "com.example.test");
        assertThat(sut.isAllowed("some.id", "com.example.test"), is(true));
        sut.disallow("some.id", "com.example.test");
        assertThat(sut.isAllowed("some.id", "com.example.test"), is(false));

        sut.allow("other.id", "com.example.test");
        assertThat(sut.isAllowed("other.id", "com.example.test"), is(true));
        sut.disallow("other.id", "com.example.test");
        assertThat(sut.isAllowed("other.id", "com.example.test"), is(false));
    }

    @Test
    public final void testAllowAfterDisallow() {
        PrivacySettingsService sut = new PrivacySettingsService("testAllowAfterDisallow");
        sut.allow("some.id", "com.example.test");
        sut.allow("other.id", "com.example.test");
        assertThat(sut.isAllowed("some.id", "com.example.test"), is(true));
        assertThat(sut.isAllowed("other.id", "com.example.test"), is(true));

        sut.disallow("some.id", "com.example.test");
        sut.disallow("other.id", "com.example.test");
        assertThat(sut.isAllowed("some.id", "com.example.test"), is(false));
        assertThat(sut.isAllowed("other.id", "com.example.test"), is(false));

        sut.allow("some.id", "com.example.test");
        sut.allow("other.id", "com.example.test");
        assertThat(sut.isAllowed("some.id", "com.example.test"), is(true));
        assertThat(sut.isAllowed("other.id", "com.example.test"), is(true));
    }
}
