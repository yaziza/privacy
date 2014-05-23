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

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.all;
import static org.eclipse.jface.layout.GridDataFactory.fillDefaults;
import static org.eclipse.jface.layout.GridDataFactory.swtDefaults;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.recommenders.privacy.rcp.ExtensionReader;
import org.eclipse.recommenders.privacy.rcp.ICategory;
import org.eclipse.recommenders.privacy.rcp.PrivacySettingsService;
import org.eclipse.recommenders.privacy.rcp.PrivatePermission;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.google.common.base.Predicate;

public class PrivacyPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private CheckboxTreeViewer datumPermissionsViewer;
    private Set<? extends ICategory> datumPermissionsInput;
    private CheckboxTreeViewer principalPermissionsViewer;
    private Set<? extends ICategory> principalPermissionsInput;

    private StackLayout treeViewerStack;
    private Composite stackComposite;
    private Composite datumComposite;
    private Composite principalComposite;

    private ExtensionReader extensionReader;
    private PrivacySettingsService globalPreferences;

    @Override
    public void init(IWorkbench workbench) {
        extensionReader = new ExtensionReader();
        extensionReader.readRegisteredExtensions();
        globalPreferences = new PrivacySettingsService();
        datumPermissionsInput = extensionReader.getDatumCategory();
        principalPermissionsInput = extensionReader.getPrincipalCategory();
        setMessage("Privacy Configuration");
    }

    @Override
    protected Control createContents(Composite parent) {
        createDescription(parent);
        createRadioButtons(parent);

        stackComposite = new Composite(parent, SWT.NONE);
        treeViewerStack = new StackLayout();
        stackComposite.setLayout(treeViewerStack);
        stackComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        datumComposite = createTreeViewLayout(stackComposite);
        datumPermissionsViewer = new CheckboxTreeViewer(datumComposite, SWT.BORDER);

        principalComposite = createTreeViewLayout(stackComposite);
        principalPermissionsViewer = new CheckboxTreeViewer(principalComposite, SWT.BORDER);

        createPermssionsView(datumPermissionsViewer, datumPermissionsInput, principalPermissionsViewer,
                new DatumLabelProvider());
        createPermssionsView(principalPermissionsViewer, principalPermissionsInput, datumPermissionsViewer,
                new PrincipalLabelProvider());

        treeViewerStack.topControl = datumComposite;
        stackComposite.layout();
        createButtons(parent);
        return parent;
    }

    private void createRadioButtons(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.marginHeight = layout.marginWidth = 0;
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        composite.setLayout(layout);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 2;
        composite.setLayoutData(data);

        Label permissionLabel = new Label(composite, SWT.NONE);
        permissionLabel.setText("Permissions by");
        permissionLabel.setFont(Display.getCurrent().getSystemFont());

        Button permissionByDatums = new Button(composite, SWT.RADIO);
        permissionByDatums.setText("datum");
        permissionByDatums.setFont(Display.getCurrent().getSystemFont());
        permissionByDatums.setSelection(true);
        permissionByDatums.setLayoutData(swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING).create());
        permissionByDatums.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                treeViewerStack.topControl = datumComposite;
                stackComposite.layout();
            }
        });

        Button permissionByInterestedParty = new Button(composite, SWT.RADIO);
        permissionByInterestedParty.setText("interested party");
        permissionByInterestedParty.setFont(Display.getCurrent().getSystemFont());
        permissionByInterestedParty.setLayoutData(swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING).create());
        permissionByInterestedParty.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                treeViewerStack.topControl = principalComposite;
                stackComposite.layout();
            }
        });
    }

    protected Label createDescription(Composite parent) {
        Label label = new Label(parent, SWT.WRAP);
        label.setText("Privacy allow you to enable or disable various plug-ins sending usage data to their servers.");
        GridData data = new GridData(SWT.FILL, SWT.NONE, false, false);
        data.widthHint = 275;
        label.setLayoutData(data);
        return label;
    }

    private void createPermssionsView(final CheckboxTreeViewer sourceViewer, final Set<? extends ICategory> input,
            final CheckboxTreeViewer targetViewer, ColumnLabelProvider labelProvider) {

        sourceViewer.getControl()
                .setLayoutData(fillDefaults().hint(SWT.DEFAULT, SWT.DEFAULT).grab(true, true).create());
        sourceViewer.setLabelProvider(labelProvider);
        sourceViewer.setContentProvider(new PermissionContentProvider());
        sourceViewer.setInput(input);
        PrivacyTooltipSupport.enableFor(sourceViewer, ToolTip.NO_RECREATE);
        sourceViewer.expandAll();
        sourceViewer.setCheckedElements(loadPermissions(input).toArray());
        updateAncestors(sourceViewer);

        sourceViewer.addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                if (event.getElement() instanceof ICategory) {
                    ICategory category = (ICategory) event.getElement();
                    for (PrivatePermission permission : category.getPermissions()) {
                        sourceViewer.setChecked(permission, event.getChecked());
                        targetViewer.setChecked(permission, event.getChecked());
                    }
                } else {
                    sourceViewer.setChecked(event.getElement(), event.getChecked());
                    targetViewer.setChecked(event.getElement(), event.getChecked());
                }
                updateAncestors(sourceViewer);
                updateAncestors(targetViewer);
            }
        });
    }

    private void createButtons(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = layout.marginWidth = 0;
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        composite.setLayout(layout);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 2;
        composite.setLayoutData(data);

        Button enableAll = new Button(composite, SWT.PUSH);
        enableAll.setText("Enable All");
        enableAll.setLayoutData(swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING).create());
        enableAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                for (ICategory datum : datumPermissionsInput) {
                    datumPermissionsViewer.setSubtreeChecked(datum, true);
                    principalPermissionsViewer.setGrayed(datum, false);
                }
                for (ICategory principal : principalPermissionsInput) {
                    principalPermissionsViewer.setSubtreeChecked(principal, true);
                    principalPermissionsViewer.setGrayed(principal, false);
                }
            }
        });

        Button disableAll = new Button(composite, SWT.PUSH);
        disableAll.setText("Disable All");
        disableAll.setLayoutData(swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING).create());
        disableAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                for (ICategory datum : datumPermissionsInput) {
                    datumPermissionsViewer.setSubtreeChecked(datum, false);
                    datumPermissionsViewer.setGrayed(datum, false);
                }
                for (ICategory principal : principalPermissionsInput) {
                    principalPermissionsViewer.setSubtreeChecked(principal, false);
                    principalPermissionsViewer.setGrayed(principal, false);
                }
            }
        });
    }

    private Composite createTreeViewLayout(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = layout.marginWidth = 0;
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        return composite;
    }

    private void updateAncestors(final CheckboxTreeViewer viewer) {
        @SuppressWarnings("unchecked")
        Set<? extends ICategory> input = (Set<? extends ICategory>) viewer.getInput();
        Predicate<PrivatePermission> permissionsCheckedPredicate = new PermissionsCheckedPredicate(viewer);
        for (ICategory category : input) {
            boolean allChecked = all(category.getPermissions(), permissionsCheckedPredicate);
            boolean noneChecked = all(category.getPermissions(), not(permissionsCheckedPredicate));
            viewer.setChecked(category, !noneChecked);
            viewer.setGrayed(category, !allChecked);
        }
    }

    private final class PermissionsCheckedPredicate implements Predicate<PrivatePermission> {

        private final CheckboxTreeViewer viewer;

        private PermissionsCheckedPredicate(CheckboxTreeViewer viewer) {
            this.viewer = viewer;
        }

        @Override
        public boolean apply(PrivatePermission permission) {
            return Arrays.asList(viewer.getCheckedElements()).contains(permission);
        }
    }

    private Set<PrivatePermission> loadPermissions(Set<? extends ICategory> input) {
        Set<PrivatePermission> permissions = new HashSet<PrivatePermission>();

        for (ICategory principal : input) {
            for (PrivatePermission permission : principal.getPermissions()) {
                if (globalPreferences.isAllowed(permission.getDatumId(), permission.getPrincipalId())) {
                    permissions.add(permission);
                }
            }
        }
        return permissions;
    }

    @Override
    public void performApply() {
        for (ICategory principal : principalPermissionsInput) {
            for (PrivatePermission permission : principal.getPermissions()) {
                if (principalPermissionsViewer.getChecked(permission)) {
                    globalPreferences.allow(permission.getDatumId(), permission.getPrincipalId());
                } else {
                    globalPreferences.disallow(permission.getDatumId(), permission.getPrincipalId());
                }
            }
        }
    }

    @Override
    public void performDefaults() {
        for (ICategory datum : datumPermissionsInput) {
            datumPermissionsViewer.setSubtreeChecked(datum, false);
        }

        for (ICategory principal : principalPermissionsInput) {
            principalPermissionsViewer.setSubtreeChecked(principal, false);
        }
    }

    @Override
    public boolean performOk() {
        performApply();
        return super.performOk();
    }
}
