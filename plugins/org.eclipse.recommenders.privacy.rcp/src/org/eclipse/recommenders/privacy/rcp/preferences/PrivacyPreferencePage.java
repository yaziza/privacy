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
package org.eclipse.recommenders.privacy.rcp.preferences;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.recommenders.privacy.rcp.Constants;
import org.eclipse.recommenders.privacy.rcp.DatumCategory;
import org.eclipse.recommenders.privacy.rcp.ExtensionReader;
import org.eclipse.recommenders.privacy.rcp.ICategory;
import org.eclipse.recommenders.privacy.rcp.PermissionState;
import org.eclipse.recommenders.privacy.rcp.PrincipalCategory;
import org.eclipse.recommenders.privacy.rcp.PrivacySettingsService;
import org.eclipse.recommenders.privacy.rcp.PrivatePermission;
import org.eclipse.recommenders.privacy.rcp.l10n.Messages;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PrivacyPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private PrivacySettingsService settingsService;

    private ExtensionReader extensionReader;

    private PermissionWidget permissionWidget;

    @Override
    public void init(IWorkbench workbench) {
        settingsService = new PrivacySettingsService();
        extensionReader = new ExtensionReader();
        Set<DatumCategory> datumCategorySet = extensionReader.getDatumCategory();
        Set<PrincipalCategory> principalCategorySet = extensionReader.getPrincipalCategory();
        permissionWidget = new PermissionWidget(datumCategorySet, principalCategorySet,
                loadPermissions(principalCategorySet));
        setMessage(Messages.PREFPAGE_TITLE);
    }

    @Override
    protected Control createContents(Composite parent) {
        return permissionWidget.createContents(parent, Messages.PREFPAGE_DESCRIPTION);
    }

    public void checkElements(Set<PrivatePermission> permissions) {
        permissionWidget.checkElements(permissions);
    }

    private Set<PrivatePermission> loadPermissions(Set<? extends ICategory> input) {
        Set<PrivatePermission> permissions = new HashSet<PrivatePermission>();

        for (ICategory principal : input) {
            for (PrivatePermission permission : principal.getPermissions()) {
                if (settingsService.isApproved(permission.getDatumId(), permission.getPrincipalId())) {
                    permissions.add(permission);
                }
            }
        }
        return permissions;
    }

    @Override
    public void performApply() {
        for (PrivatePermission permission : permissionWidget.getApprovedPermissions()) {
            settingsService.setState(permission.getDatumId(), permission.getPrincipalId(), PermissionState.APPROVED);
        }
        for (PrivatePermission permission : permissionWidget.getDispprovedPermissions()) {
            settingsService.setState(permission.getDatumId(), permission.getPrincipalId(), PermissionState.DISAPPROVED);
        }
    }

    @Override
    public boolean performOk() {
        performApply();
        return super.performOk();
    }
}
