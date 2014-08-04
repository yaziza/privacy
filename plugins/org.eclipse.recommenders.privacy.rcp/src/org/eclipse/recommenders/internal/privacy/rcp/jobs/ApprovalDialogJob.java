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
package org.eclipse.recommenders.internal.privacy.rcp.jobs;

import static org.eclipse.recommenders.internal.privacy.rcp.Constants.PREF_NODE_GLOBAL_ACTIVATION_VALUE;
import static org.eclipse.recommenders.internal.privacy.rcp.widgets.CompositeType.PRINCIPAL;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.recommenders.internal.privacy.rcp.data.ApprovalType;
import org.eclipse.recommenders.internal.privacy.rcp.data.ExtensionReader;
import org.eclipse.recommenders.internal.privacy.rcp.data.ICategory;
import org.eclipse.recommenders.internal.privacy.rcp.data.PrincipalCategory;
import org.eclipse.recommenders.internal.privacy.rcp.data.PrivacySettingsSerciveHelper;
import org.eclipse.recommenders.internal.privacy.rcp.data.PrivatePermission;
import org.eclipse.recommenders.internal.privacy.rcp.dialogs.PermissionApprovalDialog;
import org.eclipse.recommenders.internal.privacy.rcp.l10n.Messages;
import org.eclipse.recommenders.internal.privacy.rcp.widgets.PermissionWidget;
import org.eclipse.recommenders.internal.privacy.rcp.wizards.PermissionApprovalWizard;
import org.eclipse.recommenders.privacy.rcp.IPrivacySettingsService;
import org.eclipse.recommenders.privacy.rcp.PermissionState;
import org.eclipse.ui.progress.UIJob;

public class ApprovalDialogJob extends UIJob {

    public static final String PREF_FIRST_ACTIVATION = "activated"; //$NON-NLS-1$

    private final IPrivacySettingsService service;
    private final ExtensionReader extensionReader;
    private final IEclipsePreferences preferences;

    @SuppressWarnings("restriction")
    @Inject
    public ApprovalDialogJob(@Preference(value = PREF_NODE_GLOBAL_ACTIVATION_VALUE) IEclipsePreferences preferences,
            IPrivacySettingsService privacySettingsService, IEclipseContext eclipseContext) {
        super(Messages.JOB_APPROVAL_DIALOG);
        this.preferences = preferences;
        this.service = privacySettingsService;
        this.extensionReader = new ExtensionReader();
    }

    @Override
    public boolean shouldRun() {
        return !getDetectedPermission().isEmpty();
    }

    @Override
    public IStatus runInUIThread(IProgressMonitor monitor) {
        PermissionWidget permissionWidget = new PermissionWidget(extensionReader.getDatumCategory(),
                extensionReader.getPrincipalCategory());
        permissionWidget.setCheckedPermission(loadPermissions(extensionReader.getPrincipalCategory()));
        permissionWidget.setShownPermission(getDetectedPermission());
        permissionWidget.setTopComposite(PRINCIPAL);

        if (!isActivated()) {
            PermissionApprovalWizard permissionWizard = new PermissionApprovalWizard(service,
                    extensionReader.getDatumCategory(), extensionReader.getPrincipalCategory(), getDetectedPermission());
            WizardDialog wizardDialog = new WizardDialog(getDisplay().getActiveShell(), permissionWizard);
            wizardDialog.setHelpAvailable(false);
            wizardDialog.open();
            firstTimeActivation();
        } else {
            PermissionApprovalDialog approvalDialog = new PermissionApprovalDialog(getDisplay().getActiveShell(),
                    service, extensionReader.getDatumCategory(), extensionReader.getPrincipalCategory(),
                    getDetectedPermission());
            approvalDialog.open();
        }
        return Status.OK_STATUS;
    }

    private Set<PrivatePermission> getDetectedPermission() {
        Set<PrivatePermission> detectedPermissions = new HashSet<PrivatePermission>();
        for (PrincipalCategory principalCategory : extensionReader.getPrincipalCategory()) {
            for (PrivatePermission permission : principalCategory.getPermissions()) {
                PermissionState state = service.getState(principalCategory.getId(), permission.getDatumId());
                ApprovalType type = permission.getApprovalType();
                if (shouldAskForApproval(state, type)) {
                    detectedPermissions.add(permission);
                }
            }
        }
        return detectedPermissions;
    }

    private boolean shouldAskForApproval(PermissionState state, ApprovalType type) {
        return state.equals(PermissionState.UNKNOWN) && type.equals(ApprovalType.INSTALL);
    }

    private Set<PrivatePermission> loadPermissions(Set<? extends ICategory> input) {
        return PrivacySettingsSerciveHelper.suggestApproved(service, input);
    }

    private void firstTimeActivation() {
        preferences.putBoolean(PREF_FIRST_ACTIVATION, true);
    }

    private boolean isActivated() {
        return preferences.getBoolean(PREF_FIRST_ACTIVATION, false);
    }
}
