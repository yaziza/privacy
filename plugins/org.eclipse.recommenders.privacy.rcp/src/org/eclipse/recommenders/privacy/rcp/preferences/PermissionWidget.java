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
import static org.eclipse.jface.layout.GridDataFactory.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.recommenders.privacy.rcp.DatumCategory;
import org.eclipse.recommenders.privacy.rcp.ICategory;
import org.eclipse.recommenders.privacy.rcp.PrincipalCategory;
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

import com.google.common.base.Predicate;

public class PermissionWidget {

    private CheckboxTreeViewer datumPermissionsViewer;
    private Set<? extends ICategory> datumPermissionsInput;
    private CheckboxTreeViewer principalPermissionsViewer;
    private Set<? extends ICategory> principalPermissionsInput;
    private Set<PrivatePermission> approvedPermissions;

    private ViewerFilter filter;
    private StackLayout treeViewerStack;
    private Composite stackComposite;
    private Composite datumComposite;
    private Composite principalComposite;

    public PermissionWidget(Set<? extends ICategory> datumSet, Set<? extends ICategory> principalSet,
            Set<PrivatePermission> permissionSet, ViewerFilter filter) {
        datumPermissionsInput = datumSet;
        principalPermissionsInput = principalSet;
        approvedPermissions = permissionSet;
        this.filter = filter;
    }

    public PermissionWidget(Set<DatumCategory> datumSet, Set<PrincipalCategory> principalSet,
            Set<PrivatePermission> approvedPermissions) {
        this(datumSet, principalSet, approvedPermissions, new ViewerFilter() {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                return true;
            }

        });
    }

    public Control createContents(Composite parent, String message) {
        createDescription(parent, message);
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

    public void dispose() {
        stackComposite.dispose();
    }

    private void createRadioButtons(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.marginHeight = layout.marginWidth = 0;
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
        permissionByInterestedParty.setText("interested party:");
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

    private void createDescription(Composite parent, String message) {
        Label label = new Label(parent, SWT.WRAP);
        label.setText(message);
        GridData data = new GridData(SWT.FILL, SWT.NONE, false, false);
        data.widthHint = IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH;
        label.setLayoutData(data);
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
        sourceViewer.setCheckedElements(approvedPermissions.toArray());
        updateAncestors(sourceViewer);
        sourceViewer.addFilter(filter);
        sourceViewer.setSorter(new PrivacySorter());

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
                    if (!datum.getPermissions().isEmpty()) {
                        datumPermissionsViewer.setSubtreeChecked(datum, true);
                        principalPermissionsViewer.setGrayed(datum, false);
                    }
                }
                for (ICategory principal : principalPermissionsInput) {
                    principalPermissionsViewer.setSubtreeChecked(principal, true);
                    principalPermissionsViewer.setGrayed(principal, false);
                }
                updateAncestors(principalPermissionsViewer);
                updateAncestors(datumPermissionsViewer);
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
                updateAncestors(principalPermissionsViewer);
                updateAncestors(datumPermissionsViewer);
            }
        });
    }

    private Composite createTreeViewLayout(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = layout.marginWidth = 0;
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

    public Set<PrivatePermission> getApprovedPermissions() {
        return getPermissions(true);
    }

    public Set<PrivatePermission> getDispprovedPermissions() {
        return getPermissions(false);
    }

    private Set<PrivatePermission> getPermissions(boolean approved) {
        Set<PrivatePermission> permissions = new HashSet<PrivatePermission>();

        for (ICategory principal : principalPermissionsInput) {
            for (PrivatePermission permission : principal.getPermissions()) {
                if (approved == principalPermissionsViewer.getChecked(permission)) {
                    permissions.add(permission);
                }
            }
        }
        return permissions;
    }
}
