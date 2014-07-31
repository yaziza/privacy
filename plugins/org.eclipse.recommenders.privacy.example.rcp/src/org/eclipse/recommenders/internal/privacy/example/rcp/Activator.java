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
package org.eclipse.recommenders.internal.privacy.example.rcp;

import static java.util.concurrent.TimeUnit.MINUTES;

import java.util.Dictionary;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.recommenders.internal.privacy.example.rcp.l10n.Messages;
import org.eclipse.recommenders.privacy.heartbeat.rcp.IHeartbeatService;
import org.eclipse.recommenders.privacy.rcp.IPrivacySettingsService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

    private static final Logger LOG = LoggerFactory.getLogger(Activator.class);

    private static final String URI_PREFIX = "http://recommenders.eclipse.org/heartbeat"; //$NON-NLS-1$
    private static final String BUNDLE_ID = "org.eclipse.recommenders.privacy.example.rcp"; //$NON-NLS-1$

    private static final String PRINCIPAL_ID = "org.eclipse.recommenders.privacy.example.rcp.principals.example"; //$NON-NLS-1$
    private static final String HEARTBEAT = "org.eclipse.recommenders.privacy.rcp.datums.heartbeat"; //$NON-NLS-1$
    private static final String BUNDLE_VERSION = "Bundle-Version"; //$NON-NLS-1$
    private static final String BUNDLE_NAME = "Bundle-Name"; //$NON-NLS-1$

    private static final long JOB_DELAY = MINUTES.toMillis(60);

    private IPrivacySettingsService settingsService;
    private IHeartbeatService heartbeatService;
    private Job heartbeatJob;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
        settingsService = eclipseContext.get(IPrivacySettingsService.class);
        heartbeatService = eclipseContext.get(IHeartbeatService.class);
        sendHeartbeat();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        heartbeatJob.cancel();
    }

    private void sendHeartbeat() {
        heartbeatJob = new Job(Messages.JOB_SENDING_HEARTBEAT) {
            @Override
            public IStatus run(IProgressMonitor monitor) {
                if (settingsService.isApproved(PRINCIPAL_ID, HEARTBEAT)) {
                    LOG.info("Sending Heartbeat approved by the user."); //$NON-NLS-1$
                    heartbeatService.sendHeartbeat(URI_PREFIX, getValueFromHeader(BUNDLE_ID, BUNDLE_NAME),
                            getValueFromHeader(BUNDLE_ID, BUNDLE_VERSION), monitor);
                } else {
                    LOG.info("Sending Heartbeat disapproved by the user."); //$NON-NLS-1$
                }
                schedule(JOB_DELAY);
                return Status.OK_STATUS;
            }
        };
        heartbeatJob.schedule();
    }

    private String getValueFromHeader(String bundleId, String key) {
        Dictionary<String, String> directory = Platform.getBundle(bundleId).getHeaders();
        return directory.get(key);
    }
}
