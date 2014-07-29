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
package org.eclipse.recommenders.internal.privacy.rcp.preferences;

import java.util.Set;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.recommenders.internal.privacy.rcp.data.DatumCategory;
import org.eclipse.recommenders.internal.privacy.rcp.data.ExtensionReader;
import org.eclipse.recommenders.internal.privacy.rcp.data.ICategory;
import org.eclipse.recommenders.internal.privacy.rcp.data.PrincipalCategory;
import org.eclipse.recommenders.internal.privacy.rcp.data.PrivacySettingsSerciveHelper;
import org.eclipse.recommenders.internal.privacy.rcp.data.PrivatePermission;
import org.eclipse.recommenders.internal.privacy.rcp.l10n.Messages;
import org.eclipse.recommenders.internal.privacy.rcp.widgets.PermissionWidget;
import org.eclipse.recommenders.privacy.rcp.IPrivacySettingsService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class PrivacyPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private IPrivacySettingsService service;
    private ExtensionReader extensionReader;
    private PermissionWidget permissionWidget;

    @Override
    public void init(IWorkbench workbench) {
        setMessage(Messages.PRIVACY_PREFPAGE_TITLE);
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
        IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
        service = eclipseContext.get(IPrivacySettingsService.class);
        extensionReader = new ExtensionReader();
    }

    @Override
    protected Control createContents(Composite parent) {
        Set<DatumCategory> datumCategorySet = extensionReader.getDatumCategory();
        Set<PrincipalCategory> principalCategorySet = extensionReader.getPrincipalCategory();

        permissionWidget = new PermissionWidget(datumCategorySet, principalCategorySet);
        permissionWidget.setCheckedPermission(loadPermissions(principalCategorySet));
        permissionWidget.setShownPermission(PrivacySettingsSerciveHelper.getCategoriesPermissions(principalCategorySet));
        permissionWidget.createContents(parent, Messages.PRIVACY_PREFPAGE_DESCRIPTION);

        Dialog.applyDialogFont(parent);
        return parent;
    }

    public void checkElements(Set<PrivatePermission> permissions) {
        permissionWidget.checkElements(permissions);
    }

    private Set<PrivatePermission> loadPermissions(Set<? extends ICategory> input) {
        return PrivacySettingsSerciveHelper.loadApproved(service, input);
    }

    @Override
    public void performApply() {
        PrivacySettingsSerciveHelper.store(service, permissionWidget.getApprovedPermissions(),
                permissionWidget.getDisapprovedPermissions());
    }

    @Override
    public boolean performOk() {
        performApply();
        return super.performOk();
    }
}