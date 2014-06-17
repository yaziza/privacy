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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.recommenders.privacy.rcp.l10n.Messages;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.recommenders.privacy.rcp.preferences.PermissionWidget;
import org.eclipse.recommenders.privacy.rcp.preferences.PrivacyPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.ibm.icu.text.MessageFormat;

public class PermissionApprovalDialog extends Dialog {

    private PrivacySettingsService settingsService = new PrivacySettingsService();

    private final PermissionWidget permissionWidget;

    protected PermissionApprovalDialog(Shell parentShell, Set<? extends ICategory> datumSet,
            Set<? extends ICategory> principalSet, Set<PrivatePermission> detectedPermissions) {
        super(parentShell);
        this.permissionWidget = new PermissionWidget(datumSet, principalSet, loadPermissions(principalSet),
                new PermissionFilter(detectedPermissions));
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        permissionWidget.createContents(container, Messages.APPROVAL_DIALOG_MESSAGE);

        Link link = new Link(container, SWT.NONE);
        final String linkToPreferencePage = PreferencesHelper.createLinkLabelToPreferencePage(Constants.PREF_PAGE_ID);
        link.setText(MessageFormat.format(Messages.PREF_LINK_MESSAGE, linkToPreferencePage));

        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(null, Constants.PREF_PAGE_ID, null,
                        null);
                PrivacyPreferencePage preferencePage = (PrivacyPreferencePage) dialog.getSelectedPage();
                preferencePage.checkElements(permissionWidget.getApprovedPermissions());
                cancelPressed();
                dialog.open();
            }
        });
        return container;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.APPROVAL_DIALOG_TITLE);
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
    protected void okPressed() {
        for (PrivatePermission permission : permissionWidget.getApprovedPermissions()) {
            settingsService.setState(permission.getDatumId(), permission.getPrincipalId(), PermissionState.APPROVED);
        }
        for (PrivatePermission permission : permissionWidget.getDispprovedPermissions()) {
            settingsService.setState(permission.getDatumId(), permission.getPrincipalId(), PermissionState.DISAPPROVED);
        }
        super.okPressed();
    }

    @Override
    protected void cancelPressed() {
        super.cancelPressed();
    }
}
