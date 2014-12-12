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
import static org.eclipse.recommenders.internal.privacy.rcp.data.ApprovalType.INSTALL;
import static org.eclipse.recommenders.internal.privacy.rcp.widgets.CompositeType.PRINCIPAL;
import static org.eclipse.recommenders.privacy.rcp.PermissionState.UNKNOWN;

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
import org.osgi.service.prefs.BackingStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@SuppressWarnings("restriction")
public class ApprovalDialogJob extends UIJob {

    private static final Logger LOG = LoggerFactory.getLogger(ApprovalDialogJob.class);

    public static final String PREF_FIRST_ACTIVATION = "activated"; //$NON-NLS-1$

    private final IPrivacySettingsService service;
    private final ExtensionReader extensionReader;
    private final IEclipsePreferences preferences;

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
        return Iterables.tryFind(getDetectedPermission(), new ContainInstallPredicate()).isPresent();
    }

    private static final class ContainInstallPredicate implements Predicate<PrivatePermission> {

        @Override
        public boolean apply(PrivatePermission permission) {
            return INSTALL.equals(permission.getApprovalType());
        }
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
                if (UNKNOWN.equals(state)) {
                    detectedPermissions.add(permission);
                }
            }
        }
        return detectedPermissions;
    }

    private Set<PrivatePermission> loadPermissions(Set<? extends ICategory> input) {
        return PrivacySettingsSerciveHelper.suggestApproved(service, input);
    }

    private void firstTimeActivation() {
        preferences.putBoolean(PREF_FIRST_ACTIVATION, true);
        try {
            preferences.flush();
        } catch (BackingStoreException e) {
            LOG.error(Messages.LOG_ERROR_SAVING_PREFERENCES, e);
        }
    }

    private boolean isActivated() {
        return preferences.getBoolean(PREF_FIRST_ACTIVATION, false);
    }
}
