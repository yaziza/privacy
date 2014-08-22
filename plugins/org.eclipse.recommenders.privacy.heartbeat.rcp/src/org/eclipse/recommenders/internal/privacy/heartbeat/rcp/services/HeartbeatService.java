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
package org.eclipse.recommenders.internal.privacy.heartbeat.rcp.services;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.equinox.internal.p2.repository.AuthenticationFailedException;
import org.eclipse.equinox.internal.p2.transport.ecf.RepositoryTransport;
import org.eclipse.recommenders.privacy.heartbeat.rcp.IHeartbeatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("restriction")
public class HeartbeatService implements IHeartbeatService {

    private static final Logger LOG = LoggerFactory.getLogger(HeartbeatService.class);

    private static final String PATH_FORMATTER = "%s-%s"; //$NON-NLS-1$

    @Override
    public void sendHeartbeat(String uriPrefix, String bundleName, String bundleVersion, IProgressMonitor monitor) {
        RepositoryTransport transport = new RepositoryTransport();
        URI uri = createURI(uriPrefix, bundleName, bundleVersion);
        try {
            transport.getLastModified(uri, monitor);
        } catch (AuthenticationFailedException e) {
            LOG.error("Authentication with specified URI failed", e); //$NON-NLS-1$
        } catch (FileNotFoundException e) {
            // ignore because it is expected that the URI doesn't represent a resource.
        } catch (CoreException e) {
            LOG.error("Sending Heartbeat failed", e); //$NON-NLS-1$
        }
        LOG.info("Heartbeat successfuly sent to specified URI"); //$NON-NLS-1$
    }

    private URI createURI(String uriPrefix, String bundleName, String bundleVersion) {
        URI uri = null;
        try {
            String extension = String.format(PATH_FORMATTER, bundleName, bundleVersion);
            uri = URIUtil.append(new URI(uriPrefix), extension);
            uri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), null,
                    uri.getFragment());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        LOG.info("URI created: " + uri); //$NON-NLS-1$
        return uri;
    }
}
