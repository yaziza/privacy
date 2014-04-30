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
        PrivacySettingsService sut = new PrivacySettingsService();
        assertThat(sut.isAllowed("some id"), is(false));
    }

    @Test
    public final void testAllowPreferences() {
        PrivacySettingsService sut = new PrivacySettingsService();
        sut.allow("some id");
        sut.allow("other id");
        assertThat(sut.isAllowed("some id"), is(true));
        assertThat(sut.isAllowed("other id"), is(true));
        assertThat(sut.isAllowed("third"), is(false));
    }

    @Test
    public final void testDisallowPreferences() {
        PrivacySettingsService sut = new PrivacySettingsService();
        sut.allow("some id");
        assertThat(sut.isAllowed("some id"), is(true));
        sut.disallow("some id");
        assertThat(sut.isAllowed("some id"), is(false));

        sut.allow("other id");
        assertThat(sut.isAllowed("other id"), is(true));
        sut.disallow("other id");
        assertThat(sut.isAllowed("other id"), is(false));
    }

    @Test
    public final void testAllowAfterDisallow() {
        PrivacySettingsService sut = new PrivacySettingsService();
        sut.allow("some id");
        sut.allow("other id");
        assertThat(sut.isAllowed("some id"), is(true));
        assertThat(sut.isAllowed("other id"), is(true));

        sut.disallow("some id");
        sut.disallow("other id");
        assertThat(sut.isAllowed("some id"), is(false));
        assertThat(sut.isAllowed("other id"), is(false));

        sut.allow("some id");
        sut.allow("other id");
        assertThat(sut.isAllowed("some id"), is(true));
        assertThat(sut.isAllowed("other id"), is(true));
    }
}
