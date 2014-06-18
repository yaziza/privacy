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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.recommenders.privacy.rcp.l10n.Messages;
import org.eclipse.ui.progress.UIJob;

public class ApprovalDialogJob extends UIJob {

    private PrivacySettingsService settingsService = new PrivacySettingsService();

    private final ExtensionReader extensionReader;

    private Set<PrivatePermission> detectedPermissions;

    public ApprovalDialogJob(ExtensionReader extensionReader) {
        super(Messages.JOB_APPROVAL_DIALOG);
        this.extensionReader = extensionReader;
        this.detectedPermissions = getDetectedPermission();
    }

    @Override
    public boolean shouldRun() {
        return !detectedPermissions.isEmpty();
    }

    @Override
    public IStatus runInUIThread(IProgressMonitor monitor) {
        PermissionApprovalDialog approvalDialog = new PermissionApprovalDialog(getDisplay().getActiveShell(),
                extensionReader.getDatumCategory(), extensionReader.getPrincipalCategory(), detectedPermissions);
        approvalDialog.open();

        return Status.OK_STATUS;
    }

    private Set<PrivatePermission> getDetectedPermission() {
        Set<PrivatePermission> detectedPermissions = new HashSet<PrivatePermission>();
        for (PrincipalCategory principalCategory : extensionReader.getPrincipalCategory()) {
            for (PrivatePermission permission : principalCategory.getPermissions()) {
                PermissionState state = settingsService.getState(permission.getDatumId(), principalCategory.getId());
                if (state.equals(PermissionState.UNKNOWN)) {
                    detectedPermissions.add(permission);
                }
            }
        }
        return detectedPermissions;
    }
}
