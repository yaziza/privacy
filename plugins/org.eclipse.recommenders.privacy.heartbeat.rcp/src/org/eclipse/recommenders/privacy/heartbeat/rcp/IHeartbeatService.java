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
package org.eclipse.recommenders.privacy.heartbeat.rcp;

import java.net.URI;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Service for sending heartbeats. A heartbeat is a simple, unidirectional network request.
 *
 * Before sending a heartbeat, clients should check with the
 * {@link org.eclipse.recommenders.privacy.rcp.IPrivacySettingsService} whether the users has approved sending a
 * heartbeat to a principal.
 *
 * Depending on what private information the heartbeat conveys explicitly as part of its URI and implicitly as part of
 * the fact that it was sent, different private datums should be registered with the privacy framework.
 *
 * At the moment, the following private datums have been registered:
 *
 * <dl>
 * <dt>org.eclipse.recommenders.privacy.rcp.datums.bundleActivation</dt>
 * <dd>A bundle has been activated. The URI encodes the name and version of the bundles.</dd>
 * </dl>
 *
 * You can obtain an instance of the {@link IHeartbeatService} from the Eclipse context:
 *
 * <pre>
 * IEclipseContext context = EclipseContextFactory.getServiceContext(bundleContext);
 * IPrivacySettingsService service = context.get(IHeartbeatService.class);
 * </pre>
 */
public interface IHeartbeatService {

    /**
     * Sends a heartbeat to a specified {@link URI}.
     *
     * It is up to the caller to decide what conditions (e.g., bundle activation) cause a heartbeat to be send.
     */
    void sendHeartbeat(String uriPrefix, String bundleName, String bundleVersion, IProgressMonitor monitor, String query);
}
