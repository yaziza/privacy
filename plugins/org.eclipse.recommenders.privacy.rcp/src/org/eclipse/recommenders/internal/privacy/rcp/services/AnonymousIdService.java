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

import static org.eclipse.recommenders.internal.privacy.rcp.Constants.BUNDLE_ID;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.eclipse.recommenders.internal.privacy.rcp.l10n.Messages;
import org.eclipse.recommenders.privacy.rcp.IAnonymousIdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.ibm.icu.text.MessageFormat;

public class AnonymousIdService implements IAnonymousIdService {

    private static final Logger LOG = LoggerFactory.getLogger(AnonymousIdService.class);

    private static final String USER_HOME = "user.home"; //$NON-NLS-1$
    private static final String DOT_ECLIPSE_DIRECTORY_NAME = ".eclipse"; //$NON-NLS-1$
    private static final String ANONYMOUS_ID_FILE_NAME = "anonymousId"; //$NON-NLS-1$

    private final File anonymousIdFile;
    private volatile UUID anonymousId;

    public AnonymousIdService() {
        this(createAnonymousIdFile());
    }

    @VisibleForTesting
    AnonymousIdService(File anonymousIdFile) {
        this.anonymousIdFile = anonymousIdFile;
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
    public synchronized void generateAnonymousId() {
        anonymousId = generateFreshAnonymousId();
    }

    @Override
    public UUID getAnonymousId() {
        UUID result = anonymousId;
        if (result == null) {
            synchronized (this) {
                result = anonymousId;
                if (result == null) {
                    result = anonymousId = readOrCreateAnonymousId();
                }
            }
        }
        return result;
    }

    private UUID generateFreshAnonymousId() {
        UUID freshAnonymousId;
        do {
            freshAnonymousId = UUID.randomUUID();
        } while (freshAnonymousId.equals(anonymousId));

        try {
            Files.write(freshAnonymousId.toString(), anonymousIdFile, Charsets.UTF_8);
        } catch (IOException e) {
            LOG.error(MessageFormat.format(Messages.LOG_ERROR_ANONYMOUS_ID_FILE_WRITE, anonymousIdFile), e);
        }

        return freshAnonymousId;
    }

    private UUID readOrCreateAnonymousId() {
        if (anonymousIdFile.exists() && anonymousIdFile.canRead()) {
            try {
                return readAnonymousIdFromFile(anonymousIdFile);
            } catch (Exception e) {
                LOG.error(MessageFormat.format(Messages.LOG_ERROR_ANONYMOUS_ID_FILE_READ, anonymousIdFile), e);
                return generateFreshAnonymousId();
            }
        } else {
            return generateFreshAnonymousId();
        }
    }

    private UUID readAnonymousIdFromFile(File file) throws IOException {
        String anonymousIdString = Files.readFirstLine(file, Charsets.UTF_8);
        return UUID.fromString(new String(anonymousIdString));
    }
}
