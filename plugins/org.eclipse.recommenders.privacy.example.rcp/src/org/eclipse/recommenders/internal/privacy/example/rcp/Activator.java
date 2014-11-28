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

import static org.eclipse.recommenders.internal.privacy.example.rcp.Constants.*;
import static org.eclipse.recommenders.internal.privacy.example.rcp.dialogs.HeartbeatInterval.*;
import static org.eclipse.recommenders.privacy.rcp.PermissionState.*;

import java.util.Dictionary;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.recommenders.internal.privacy.example.rcp.dialogs.HeartbeatInterval;
import org.eclipse.recommenders.internal.privacy.example.rcp.l10n.Messages;
import org.eclipse.recommenders.privacy.heartbeat.rcp.IHeartbeatService;
import org.eclipse.recommenders.privacy.rcp.IAnonymousIdService;
import org.eclipse.recommenders.privacy.rcp.IPrivacySettingsService;
import org.eclipse.recommenders.privacy.rcp.PermissionState;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.text.MessageFormat;

public class Activator implements BundleActivator {

    private static final Logger LOG = LoggerFactory.getLogger(Activator.class);

    private IPrivacySettingsService settingsService;
    private IAnonymousIdService anonymousIdService;
    private IHeartbeatService heartbeatService;
    private HeartbeatInterval heartbeatInterval;
    private Job heartbeatJob;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
        settingsService = eclipseContext.get(IPrivacySettingsService.class);
        anonymousIdService = eclipseContext.get(IAnonymousIdService.class);
        heartbeatService = eclipseContext.get(IHeartbeatService.class);
        heartbeatInterval = getHeartbeatInterval();
        sendHeartbeat();
    }

    private HeartbeatInterval getHeartbeatInterval() {
        Preferences preferences = InstanceScope.INSTANCE.getNode(BUNDLE_ID);
        String delayName = preferences.get(PREF_DELAY, HOURLY.name());
        return HeartbeatInterval.valueOf(delayName);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        heartbeatJob.cancel();
    }

    private void sendHeartbeat() {
        heartbeatJob = new Job(Messages.JOB_SENDING_HEARTBEAT) {
            @Override
            public IStatus run(IProgressMonitor monitor) {
                PermissionState state = settingsService.getState(PRINCIPAL_ID, HEARTBEAT);
                heartbeatInterval = getHeartbeatInterval();
                if (APPROVED.equals(state)) {
                    LOG.info(MessageFormat.format(Messages.LOG_INFO_SENDING_PERMISSION_APPROVED, "Heartbeat")); //$NON-NLS-1$
                    heartbeatService.sendHeartbeat(URI_PREFIX, getValueFromHeader(BUNDLE_ID, BUNDLE_SYMBOLIC_NAME),
                            getValueFromHeader(BUNDLE_ID, BUNDLE_VERSION), monitor, getAnonymousId());
                    if (ONCE.equals(heartbeatInterval)) {
                        return Status.OK_STATUS;
                    }
                } else if (DISAPPROVED.equals(state)) {
                    LOG.info(MessageFormat.format(Messages.LOG_INFO_SENDING_PERMISSION_DISAPPROVED, "Heartbeat")); //$NON-NLS-1$
                } else {
                    LOG.info(MessageFormat.format(Messages.LOG_INFO_SENDING_PERMISSION_UNKNOWN, "Heartbeat")); //$NON-NLS-1$
                }
                schedule(heartbeatInterval.getDelay());
                return Status.OK_STATUS;
            }
        };
        heartbeatJob.schedule();
    }

    private String getAnonymousId() {
        if (settingsService.isApproved(PRINCIPAL_ID, ANONYMOUS_ID)) {
            LOG.info(MessageFormat.format(Messages.LOG_INFO_SENDING_PERMISSION_APPROVED, "anonymous ID")); //$NON-NLS-1$
            return anonymousIdService.getAnonymousId().toString();
        }
        LOG.info(MessageFormat.format(Messages.LOG_INFO_SENDING_PERMISSION_DISAPPROVED, "anonymous ID")); //$NON-NLS-1$
        return null;
    }

    private String getValueFromHeader(String bundleId, String key) {
        Dictionary<String, String> directory = Platform.getBundle(bundleId).getHeaders();
        String value = directory.get(key);
        int parametersIndex = value.indexOf(';');
        return parametersIndex < 0 ? value : value.substring(0, parametersIndex);
    }
}
