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

import static com.google.common.collect.Sets.intersection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.recommenders.internal.privacy.rcp.data.ICategory;
import org.eclipse.recommenders.internal.privacy.rcp.data.PrivatePermission;
import org.eclipse.recommenders.internal.privacy.rcp.l10n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class SingleDatumWidget {

    private TreeViewer permissionsViewer;
    private final Set<? extends ICategory> permissionsInput;
    private final String datumId;
    private Set<PrivatePermission> checkedPermissions = Collections.emptySet();

    private Composite principalComposite;

    public SingleDatumWidget(Set<? extends ICategory> permissionSet, String datumId) {
        permissionsInput = permissionSet;
        this.datumId = datumId;
    }

    public void setCheckedPermissions(Set<PrivatePermission> checkedPermissions) {
        this.checkedPermissions = checkedPermissions;
    }

    public Control createContents(Composite parent) {
        createPermissionLabel(parent);

        principalComposite = createTreeViewLayout(parent);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(principalComposite);
        permissionsViewer = new TreeViewer(principalComposite, SWT.BORDER);

        createPermssionsView(permissionsViewer, permissionsInput);
        createButtons(parent);

        Dialog.applyDialogFont(parent);
        return parent;
    }

    public void refresh() {
        permissionsViewer.refresh();
    }

    public void dispose() {
        principalComposite.dispose();
    }

    private void createPermissionLabel(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(composite);
        GridDataFactory.fillDefaults().span(2, 1).applyTo(composite);

        Label permissionLabel = new Label(composite, SWT.NONE);
        permissionLabel.setText(Messages.LABEL_APPROVED_INTERESTED_PARTIES);
    }

    private void createPermssionsView(final TreeViewer sourceViewer, final Set<? extends ICategory> input) {
        GridDataFactory.fillDefaults().hint(SWT.DEFAULT, SWT.DEFAULT).grab(true, true)
                .applyTo(sourceViewer.getControl());
        sourceViewer.setLabelProvider(new DatumLabelProvider());
        sourceViewer.setContentProvider(new PermissionContentProvider());
        sourceViewer.setInput(getShownPermissions(input));
        PrivacyTooltipSupport.enableFor(sourceViewer, ToolTip.NO_RECREATE);
        sourceViewer.expandAll();
        sourceViewer.addFilter(getFilter());
        sourceViewer.setSorter(new PrivacySorter());
    }

    private void createButtons(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(4).applyTo(composite);
        GridDataFactory.fillDefaults().span(4, 1).applyTo(composite);
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
                    return !intersection(checkedPermissions, category.getPermissions()).isEmpty();
                }
                PrivatePermission privatePermission = (PrivatePermission) element;
                return checkedPermissions.contains(privatePermission);
            }
        };
    }

    private Set<PrivatePermission> getShownPermissions(Set<? extends ICategory> categorySet) {
        Set<PrivatePermission> permissions = new HashSet<PrivatePermission>();

        for (ICategory datum : categorySet) {
            for (PrivatePermission permission : datum.getPermissions()) {
                if (datumId.equals(permission.getDatumId())) {
                    permissions.add(permission);
                }
            }
        }
        return permissions;
    }
}
