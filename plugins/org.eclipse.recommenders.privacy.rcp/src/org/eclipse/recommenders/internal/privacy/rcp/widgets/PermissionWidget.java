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
package org.eclipse.recommenders.internal.privacy.rcp.widgets;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.all;
import static com.google.common.collect.Sets.intersection;
import static org.eclipse.recommenders.internal.privacy.rcp.widgets.CompositeType.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.recommenders.internal.privacy.rcp.data.ICategory;
import org.eclipse.recommenders.internal.privacy.rcp.data.PrivatePermission;
import org.eclipse.recommenders.internal.privacy.rcp.l10n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.google.common.base.Predicate;

public class PermissionWidget {
    private static final String SWT_ID = "id"; //$NON-NLS-1$
    private static final String GROUP_BY_INTERESTED_PARTY_BUTTON_ID = "org.eclipse.recommenders.privacy.rcp.preferences.groupByInterestedParty"; //$NON-NLS-1$
    private static final String GROUP_BY_INFORMATION_BUTTON_ID = "org.eclipse.recommenders.privacy.rcp.preferences.groupByInformation"; //$NON-NLS-1$
    private static final String DISABLE_ALL_BUTTON_ID = "org.eclipse.recommenders.privacy.rcp.preferences.disableAll"; //$NON-NLS-1$
    private static final String ENABLE_ALL_BUTTON_ID = "org.eclipse.recommenders.privacy.rcp.preferences.enableAll"; //$NON-NLS-1$

    private CheckboxTreeViewer datumPermissionsViewer;
    private Set<? extends ICategory> datumPermissionsInput;
    private CheckboxTreeViewer principalPermissionsViewer;
    private Set<? extends ICategory> principalPermissionsInput;
    private Set<PrivatePermission> checkedPermissions = Collections.emptySet();
    private Set<PrivatePermission> shownPermissions = Collections.emptySet();

    private StackLayout treeViewerStack;
    private Composite stackComposite;
    private Composite datumComposite;
    private Composite principalComposite;
    private CompositeType topComposite = DATUM;

    public PermissionWidget(Set<? extends ICategory> datumSet, Set<? extends ICategory> principalSet) {
        datumPermissionsInput = datumSet;
        principalPermissionsInput = principalSet;
    }

    public void setCheckedPermission(Set<PrivatePermission> checkedPermissions) {
        this.checkedPermissions = checkedPermissions;
    }

    public void setShownPermission(final Set<PrivatePermission> shownPermissions) {
        this.shownPermissions = shownPermissions;
    }

    public void setTopComposite(CompositeType topComposite) {
        this.topComposite = topComposite;
    }

    public Control createContents(Composite parent, String message) {
        createDescription(parent, message);
        createPermissionLabel(parent);

        stackComposite = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(stackComposite);
        treeViewerStack = new StackLayout();
        stackComposite.setLayout(treeViewerStack);

        datumComposite = createTreeViewLayout(stackComposite);
        datumPermissionsViewer = new CheckboxTreeViewer(datumComposite, SWT.BORDER);

        principalComposite = createTreeViewLayout(stackComposite);
        principalPermissionsViewer = new CheckboxTreeViewer(principalComposite, SWT.BORDER);

        createPermssionsView(datumPermissionsViewer, datumPermissionsInput, principalPermissionsViewer,
                new DatumLabelProvider());
        createPermssionsView(principalPermissionsViewer, principalPermissionsInput, datumPermissionsViewer,
                new PrincipalLabelProvider());

        updateStackTopControl();
        createButtons(parent);

        Dialog.applyDialogFont(parent);
        return parent;
    }

    public void dispose() {
        stackComposite.dispose();
    }

    private void createDescription(Composite parent, String message) {
        Label label = new Label(parent, SWT.WRAP);
        label.setText(message);
        GridDataFactory.fillDefaults().hint(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH, SWT.DEFAULT).applyTo(label);
    }

    private void createPermissionLabel(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(composite);
        GridDataFactory.fillDefaults().span(2, 1).applyTo(composite);

        Label permissionLabel = new Label(composite, SWT.NONE);
        permissionLabel.setText(Messages.LABEL_GROUP_BY);

        createRadioButton(composite, Messages.LABEL_INFORMATION, GROUP_BY_INFORMATION_BUTTON_ID,
                topComposite.equals(DATUM));
        createRadioButton(composite, Messages.LABEL_INTERESTED_PARTY, GROUP_BY_INTERESTED_PARTY_BUTTON_ID,
                topComposite.equals(PRINCIPAL));
    }

    private void createRadioButton(Composite parent, final String text, String id, boolean selected) {
        Button button = new Button(parent, SWT.RADIO);
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(button);
        button.setText(text);
        button.setData(SWT_ID, id);
        button.setSelection(selected);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                topComposite = text.equals(Messages.LABEL_INFORMATION) ? DATUM : PRINCIPAL;
                updateStackTopControl();
            }
        });
    }

    private void createPermssionsView(final CheckboxTreeViewer sourceViewer, final Set<? extends ICategory> input,
            final CheckboxTreeViewer targetViewer, ColumnLabelProvider labelProvider) {

        GridDataFactory.fillDefaults().hint(SWT.DEFAULT, SWT.DEFAULT).grab(true, true)
        .applyTo(sourceViewer.getControl());
        sourceViewer.setLabelProvider(labelProvider);
        sourceViewer.setContentProvider(new PermissionContentProvider());
        sourceViewer.setInput(input);
        PrivacyTooltipSupport.enableFor(sourceViewer, ToolTip.NO_RECREATE);
        sourceViewer.expandAll();
        sourceViewer.setCheckedElements(checkedPermissions.toArray());
        updateAncestors(sourceViewer);
        sourceViewer.addFilter(getFilter());
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
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(composite);
        GridDataFactory.fillDefaults().span(2, 1).applyTo(composite);

        createChangeAllButton(composite, Messages.BUTTON_ENABLE_ALL, ENABLE_ALL_BUTTON_ID, true);
        createChangeAllButton(composite, Messages.BUTTON_DISABLE_ALL, DISABLE_ALL_BUTTON_ID, false);
    }

    private void createChangeAllButton(Composite composite, String label, String id, final boolean checkedState) {
        Button button = new Button(composite, SWT.PUSH);
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(button);
        button.setText(label);
        button.setData(SWT_ID, id);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                for (ICategory datum : datumPermissionsInput) {
                    if (!datum.getPermissions().isEmpty()) {
                        datumPermissionsViewer.setSubtreeChecked(datum, checkedState);
                        principalPermissionsViewer.setGrayed(datum, false);
                    }
                }
                for (ICategory principal : principalPermissionsInput) {
                    principalPermissionsViewer.setSubtreeChecked(principal, checkedState);
                    principalPermissionsViewer.setGrayed(principal, false);
                }
                updateAncestors();
            }
        });
    }

    private Composite createTreeViewLayout(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(composite);
        GridDataFactory.fillDefaults().applyTo(composite);
        return composite;
    }

    private ViewerFilter getFilter() {
        return new ViewerFilter() {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (element instanceof ICategory) {
                    ICategory category = (ICategory) element;
                    return !intersection(shownPermissions, category.getPermissions()).isEmpty();
                }
                PrivatePermission privatePermission = (PrivatePermission) element;
                return shownPermissions.contains(privatePermission);
            }
        };
    }

    private void updateStackTopControl() {
        if (topComposite.equals(DATUM)) {
            treeViewerStack.topControl = datumComposite;
        } else if (topComposite.equals(PRINCIPAL)) {
            treeViewerStack.topControl = principalComposite;
        }

        stackComposite.layout();
    }

    private void updateAncestors(final CheckboxTreeViewer viewer) {
        @SuppressWarnings("unchecked")
        Set<? extends ICategory> input = (Set<? extends ICategory>) viewer.getInput();
        Predicate<PrivatePermission> permissionsCheckedPredicate = new PermissionsCheckedPredicate(viewer);
        for (ICategory category : input) {
            Set<PrivatePermission> permissions = intersection(shownPermissions, category.getPermissions());
            boolean allChecked = all(permissions, permissionsCheckedPredicate);
            boolean noneChecked = all(permissions, not(permissionsCheckedPredicate));
            viewer.setChecked(category, !noneChecked);
            viewer.setGrayed(category, !allChecked);
        }
    }

    private void updateAncestors() {
        updateAncestors(principalPermissionsViewer);
        updateAncestors(datumPermissionsViewer);
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

    public Set<PrivatePermission> getDisapprovedPermissions() {
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

    public void checkElements(Set<PrivatePermission> permissions) {
        for (PrivatePermission permission : permissions) {
            datumPermissionsViewer.setChecked(permission, true);
            principalPermissionsViewer.setChecked(permission, true);
        }
        updateAncestors();
    }
}
