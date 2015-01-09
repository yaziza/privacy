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

import static org.eclipse.recommenders.internal.privacy.rcp.data.PrivacySettingsSerciveHelper.*;

import java.util.Set;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.recommenders.internal.privacy.rcp.data.ExtensionReader;
import org.eclipse.recommenders.internal.privacy.rcp.data.PrivatePermission;
import org.eclipse.recommenders.internal.privacy.rcp.dialogs.PermissionApprovalDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class PermissionApprovalHelper {

    private static IPrivacySettingsService settingsService;
    private static ExtensionReader extensionReader;

    static {
        BundleContext bundleContext = FrameworkUtil.getBundle(PermissionApprovalHelper.class).getBundleContext();
        IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
        settingsService = eclipseContext.get(IPrivacySettingsService.class);
        extensionReader = new ExtensionReader();
    }

    public static void showDefaultApprovalDialog(Shell shell, String principalId, String... datumsIds) {
        showDialog(shell, null, null, principalId, datumsIds);
    }

    public static void showCustomApprovedDialog(Shell shell, String title, String description, String principalId,
            String... datumsIds) {
        showDialog(shell, title, description, principalId, datumsIds);
    }

    private static void showDialog(Shell shell, String title, String description, String principalId,
            String... datumsIds) {
        Set<PrivatePermission> permissionSet = getPermissions(
                getCategoriesPermissions(extensionReader.getDatumCategory()), principalId, datumsIds);

        PermissionApprovalDialog approvalDialog = new PermissionApprovalDialog(shell, settingsService,
                extensionReader.getDatumCategory(), extensionReader.getPrincipalCategory(), permissionSet);

        approvalDialog.setTitle(title);
        approvalDialog.setDescription(description);
        approvalDialog.open();
    }
}
