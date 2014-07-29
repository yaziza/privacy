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
package org.eclipse.recommenders.internal.privacy.rcp.wizards;

import java.util.Set;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.recommenders.internal.privacy.rcp.data.ICategory;
import org.eclipse.recommenders.internal.privacy.rcp.data.PrivacySettingsSerciveHelper;
import org.eclipse.recommenders.internal.privacy.rcp.data.PrivatePermission;
import org.eclipse.recommenders.internal.privacy.rcp.l10n.Messages;
import org.eclipse.recommenders.privacy.rcp.IPrivacySettingsService;

public class PermissionApprovalWizard extends Wizard {

    private final IPrivacySettingsService service;
    private final Set<? extends ICategory> datumSet;
    private final Set<? extends ICategory> principalSet;
    private final Set<PrivatePermission> detectedPermissions;

    private PermissionApprovalPage permissionApprovalPage;

    public PermissionApprovalWizard(IPrivacySettingsService service, Set<? extends ICategory> datumSet,
            Set<? extends ICategory> principalSet, Set<PrivatePermission> detectedPermissions) {
        this.service = service;
        this.datumSet = datumSet;
        this.principalSet = principalSet;
        this.detectedPermissions = detectedPermissions;
    }

    @Override
    public String getWindowTitle() {
        return Messages.APPROVAL_WIZARD_TITLE;
    }

    @Override
    public void addPages() {
        addPage(new IntroPage());
        permissionApprovalPage = new PermissionApprovalPage(this, datumSet, principalSet, detectedPermissions,
                loadPermissions(principalSet));
        addPage(permissionApprovalPage);
    }

    @Override
    public boolean performFinish() {
        PrivacySettingsSerciveHelper.store(service, permissionApprovalPage.getApprovedPermissions(),
                permissionApprovalPage.getDisapprovedPermissions());
        return true;
    }

    private Set<PrivatePermission> loadPermissions(Set<? extends ICategory> input) {
        return PrivacySettingsSerciveHelper.suggestApproved(service, input);
    }
}
