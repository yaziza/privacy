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

    private static final String PRINCIPAL_ID = "org.eclipse.recommenders.privacy.example";
    private static final String DATUM_ID = "org.eclipse.recommenders.privacy.datums.javaVersion";

    private static final int JOB_DELAY = 600000;

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
                    if (service.isApproved(DATUM_ID, PRINCIPAL_ID)) {
                        LOG.info("sending JVM version approved by the user ...");
                        LOG.info("Java version: " + System.getProperty("java.version"));
                    } else {
                        LOG.info("sending JVM disapproved by the user.");
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
