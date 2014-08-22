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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class AnonymousIdServiceTest {

    private static final String ANONYMOUS_ID_FILE_NAME = "anonymousId"; //$NON-NLS-1$

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testGetAnonymousIdIsNotNull() throws IOException {
        AnonymousIdService sut = new AnonymousIdService(folder.newFile());
        UUID anonymousId = sut.getAnonymousId();
        assertThat(anonymousId, is(notNullValue()));
    }

    @Test
    public void testGetAnonymousIdIsIdempotent() throws IOException {
        AnonymousIdService sut = new AnonymousIdService(folder.newFile());
        UUID firstAnonymousId = sut.getAnonymousId();
        UUID secondAnonymousId = sut.getAnonymousId();
        assertThat(secondAnonymousId, is(equalTo(firstAnonymousId)));
    }

    @Test
    public void testGenerateAnonymousId() throws IOException {
        AnonymousIdService sut = new AnonymousIdService(folder.newFile());
        UUID firstAnonymousId = sut.getAnonymousId();
        sut.generateAnonymousId();
        UUID secondAnonymousId = sut.getAnonymousId();
        assertThat(secondAnonymousId, is(not(equalTo(firstAnonymousId))));
    }

    @Test
    public void testGetAnonymousIdWithWriteProtectedAnonymousIdFile() throws IOException {
        File writeProtectedFolder = folder.newFolder();
        writeProtectedFolder.setWritable(false);
        AnonymousIdService sut = new AnonymousIdService(new File(writeProtectedFolder, ANONYMOUS_ID_FILE_NAME));

        assertThat(writeProtectedFolder.canWrite(), is(false));

        UUID firstAnonymousId = sut.getAnonymousId();
        assertThat(firstAnonymousId, is(notNullValue()));

        UUID secondAnonymousId = sut.getAnonymousId();
        assertThat(secondAnonymousId, is(equalTo(firstAnonymousId)));
    }

    @Test
    public void testGetAnonymousIdWithReadProtectedAnonymousIdFile() throws IOException {
        File anonymousIdFile = folder.newFile();
        AnonymousIdService firstSession = new AnonymousIdService(anonymousIdFile);

        firstSession.generateAnonymousId();

        anonymousIdFile.setReadable(false);
        assertThat(anonymousIdFile.canRead(), is(false));

        AnonymousIdService secondSession = new AnonymousIdService(anonymousIdFile);

        UUID firstAnonymousId = secondSession.getAnonymousId();
        assertThat(firstAnonymousId, is(notNullValue()));
    }
}
