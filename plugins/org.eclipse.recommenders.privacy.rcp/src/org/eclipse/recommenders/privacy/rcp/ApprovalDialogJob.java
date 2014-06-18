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

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.recommenders.privacy.rcp.l10n.Messages;
import org.eclipse.ui.progress.UIJob;

public class ApprovalDialogJob extends UIJob {

    private final IPrivacySettingsService privacySettingsService;
    private final ExtensionReader extensionReader;

    @Inject
    public ApprovalDialogJob(IPrivacySettingsService privacySettingsService, IEclipseContext eclipseContext) {
        super(Messages.JOB_APPROVAL_DIALOG);
        this.privacySettingsService = privacySettingsService;
        this.extensionReader = new ExtensionReader();
    }

    @Override
    public boolean shouldRun() {
        return !getDetectedPermission().isEmpty();
    }

    @Override
    public IStatus runInUIThread(IProgressMonitor monitor) {
        PermissionApprovalDialog approvalDialog = new PermissionApprovalDialog(getDisplay().getActiveShell(),
                privacySettingsService, extensionReader.getDatumCategory(), extensionReader.getPrincipalCategory(),
                getDetectedPermission());
        approvalDialog.open();
        return Status.OK_STATUS;
    }

    private Set<PrivatePermission> getDetectedPermission() {
        Set<PrivatePermission> detectedPermissions = new HashSet<PrivatePermission>();
        for (PrincipalCategory principalCategory : extensionReader.getPrincipalCategory()) {
            for (PrivatePermission permission : principalCategory.getPermissions()) {
                PermissionState state = privacySettingsService.getState(permission.getDatumId(),
                        principalCategory.getId());
                if (state.equals(PermissionState.UNKNOWN)) {
                    detectedPermissions.add(permission);
                }
            }
        }
        return detectedPermissions;
    }
}
