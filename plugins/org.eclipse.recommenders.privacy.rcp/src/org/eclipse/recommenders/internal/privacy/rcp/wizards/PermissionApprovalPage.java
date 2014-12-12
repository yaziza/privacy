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

import static org.eclipse.recommenders.internal.privacy.rcp.Constants.PREF_PAGE_ID;
import static org.eclipse.recommenders.internal.privacy.rcp.widgets.CompositeType.PRINCIPAL;

import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.recommenders.internal.privacy.rcp.Constants;
import org.eclipse.recommenders.internal.privacy.rcp.data.ICategory;
import org.eclipse.recommenders.internal.privacy.rcp.data.PrivatePermission;
import org.eclipse.recommenders.internal.privacy.rcp.l10n.Messages;
import org.eclipse.recommenders.internal.privacy.rcp.preferences.PreferencesHelper;
import org.eclipse.recommenders.internal.privacy.rcp.preferences.PrivacyPreferencePage;
import org.eclipse.recommenders.internal.privacy.rcp.widgets.PermissionWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.ibm.icu.text.MessageFormat;

public class PermissionApprovalPage extends WizardPage {

    private final PermissionWidget permissionWidget;
    private final Set<PrivatePermission> shownPermissions;

    public PermissionApprovalPage(PermissionApprovalWizard permissionApprovalWizard, Set<? extends ICategory> datumSet,
            Set<? extends ICategory> principalSet, Set<PrivatePermission> shownPermissions,
            Set<PrivatePermission> enabledPermissions) {
        super(Messages.WIZARD_PERMISSION_PAGE_TITLE);

        setTitle(Messages.WIZARD_PERMISSION_PAGE_TITLE);
        setDescription(Messages.WIZARD_PERMISSION_PAGE_DESCRIPTION);
        setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Constants.BUNDLE_ID, Constants.BANNER_ICON));

        permissionWidget = createPermissionWidget(datumSet, principalSet, shownPermissions, enabledPermissions);
        this.shownPermissions = shownPermissions;
    }

    private PermissionWidget createPermissionWidget(Set<? extends ICategory> datumSet,
            Set<? extends ICategory> principalSet, Set<PrivatePermission> shownPermissions,
            Set<PrivatePermission> checkedPermissions) {
        PermissionWidget permissionWidget = new PermissionWidget(datumSet, principalSet);
        permissionWidget.setShownPermission(shownPermissions);
        permissionWidget.setCheckedPermission(checkedPermissions);
        permissionWidget.setTopComposite(PRINCIPAL);
        return permissionWidget;
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(container);

        createDescription(container, Messages.APPROVAL_DIALOG_MESSAGE);
        permissionWidget.createContents(container);
        createLinkToPreferencePage(container);

        Dialog.applyDialogFont(container);
    }

    private void createDescription(Composite parent, String message) {
        Label label = new Label(parent, SWT.WRAP);
        label.setText(message);
        GridDataFactory.fillDefaults().hint(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH, SWT.DEFAULT).applyTo(label);
    }

    private void createLinkToPreferencePage(Composite container) {
        Link link = new Link(container, SWT.WRAP);
        final String linkToPreferencePage = PreferencesHelper.createLinkLabelToPreferencePage(PREF_PAGE_ID);
        link.setText(MessageFormat.format(Messages.PREF_LINK_MESSAGE, linkToPreferencePage));

        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(null, PREF_PAGE_ID, null, null);
                PrivacyPreferencePage preferencePage = (PrivacyPreferencePage) dialog.getSelectedPage();
                preferencePage.checkElements(permissionWidget.getAllApprovedPermissions());
                getWizard().performCancel();
                getWizard().getContainer().getShell().close();
                dialog.open();
            }
        });
        setControl(container);
    }

    public Set<PrivatePermission> getApprovedPermissions() {
        return permissionWidget.getApprovedPermissions(shownPermissions);
    }

    public Set<PrivatePermission> getDisapprovedPermissions() {
        return permissionWidget.getDisapprovedPermissions(shownPermissions);
    }
}
