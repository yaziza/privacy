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
package org.eclipse.recommenders.privacy.example.rcp;

import static java.util.concurrent.TimeUnit.MINUTES;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.recommenders.privacy.rcp.IPrivacySettingsService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {
    private static final Logger LOG = LoggerFactory.getLogger(Activator.class);

    private static final String PRINCIPAL_ID = "org.eclipse.recommenders.privacy.example.rcp.principals.example";
    private static final String JAVA_VERSION = "org.eclipse.recommenders.privacy.rcp.datums.javaVersion";
    private static final String USER_ID = "org.eclipse.recommenders.privacy.rcp.datums.userId";

    private static final long JOB_DELAY = MINUTES.toMillis(10);

    private Job heartbeatJob;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
        IPrivacySettingsService service = eclipseContext.get(IPrivacySettingsService.class);
        createHeartbeatJob(service);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        heartbeatJob.cancel();
    }

    private void createHeartbeatJob(final IPrivacySettingsService service) {
        heartbeatJob = new Job("Sending Heartbeat to Server") {
            @Override
            public IStatus run(IProgressMonitor monitor) {
                try {
                    if (service.isApproved(PRINCIPAL_ID, JAVA_VERSION, USER_ID)) {
                        LOG.info("sending java version and user ID approved by the user ...");
                        LOG.info("java version: " + System.getProperty("java.version"));
                        LOG.info("user ID: " + service.getUserId());
                    } else {
                        LOG.info("sending java version and/or user ID disapproved by the user.");
                    }
                    return Status.OK_STATUS;
                } finally {
                    schedule(JOB_DELAY);
                }
            }
        };

        heartbeatJob.schedule();
    }
}
