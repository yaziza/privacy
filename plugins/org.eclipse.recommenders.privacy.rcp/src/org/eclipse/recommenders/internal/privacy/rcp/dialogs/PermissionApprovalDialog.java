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
package org.eclipse.recommenders.internal.privacy.rcp.dialogs;

import static org.eclipse.recommenders.internal.privacy.rcp.Constants.PREF_PAGE_ID;
import static org.eclipse.recommenders.internal.privacy.rcp.widgets.CompositeType.PRINCIPAL;

import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.recommenders.internal.privacy.rcp.data.ICategory;
import org.eclipse.recommenders.internal.privacy.rcp.data.PrivacySettingsSerciveHelper;
import org.eclipse.recommenders.internal.privacy.rcp.data.PrivatePermission;
import org.eclipse.recommenders.internal.privacy.rcp.l10n.Messages;
import org.eclipse.recommenders.internal.privacy.rcp.preferences.PreferencesHelper;
import org.eclipse.recommenders.internal.privacy.rcp.preferences.PrivacyPreferencePage;
import org.eclipse.recommenders.internal.privacy.rcp.widgets.PermissionWidget;
import org.eclipse.recommenders.privacy.rcp.IPrivacySettingsService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.ibm.icu.text.MessageFormat;

public class PermissionApprovalDialog extends Dialog {

    private final IPrivacySettingsService service;
    private final Set<? extends ICategory> datumSet;
    private final Set<? extends ICategory> principalSet;
    private final Set<PrivatePermission> detectedPermissions;

    private PermissionWidget permissionWidget;

    public PermissionApprovalDialog(Shell parentShell, IPrivacySettingsService service,
            Set<? extends ICategory> datumSet, Set<? extends ICategory> principalSet,
            Set<PrivatePermission> detectedPermissions) {
        super(parentShell);
        this.service = service;
        this.datumSet = datumSet;
        this.principalSet = principalSet;
        this.detectedPermissions = detectedPermissions;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        permissionWidget = new PermissionWidget(datumSet, principalSet);
        permissionWidget.setCheckedPermission(loadPermissions(principalSet));
        permissionWidget.setShownPermission(detectedPermissions);
        permissionWidget.setTopComposite(PRINCIPAL);
        permissionWidget.createContents(container, Messages.APPROVAL_DIALOG_MESSAGE);

        Link link = new Link(container, SWT.NONE);
        final String linkToPreferencePage = PreferencesHelper.createLinkLabelToPreferencePage(PREF_PAGE_ID);
        link.setText(MessageFormat.format(Messages.PREF_LINK_MESSAGE, linkToPreferencePage));

        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(null, PREF_PAGE_ID, null, null);
                PrivacyPreferencePage preferencePage = (PrivacyPreferencePage) dialog.getSelectedPage();
                preferencePage.checkElements(permissionWidget.getApprovedPermissions());
                cancelPressed();
                dialog.open();
            }
        });

        Dialog.applyDialogFont(container);
        return container;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);

        Button ok = getButton(IDialogConstants.OK_ID);
        ok.setText(Messages.BUTTON_OK);
        setButtonLayoutData(ok);

        Button cancel = getButton(IDialogConstants.CANCEL_ID);
        cancel.setText(Messages.BUTTON_NOT_NOW);
        setButtonLayoutData(cancel);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.APPROVAL_DIALOG_TITLE);
    }

    private Set<PrivatePermission> loadPermissions(Set<? extends ICategory> input) {
        return PrivacySettingsSerciveHelper.suggestApproved(service, input);
    }

    @Override
    protected void okPressed() {
        PrivacySettingsSerciveHelper.store(service, permissionWidget.getApprovedPermissions(),
                permissionWidget.getDisapprovedPermissions());
        super.okPressed();
    }
}
