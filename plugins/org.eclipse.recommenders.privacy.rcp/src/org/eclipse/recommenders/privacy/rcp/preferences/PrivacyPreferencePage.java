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

import static org.eclipse.jface.layout.GridDataFactory.fillDefaults;
import static org.eclipse.jface.layout.GridDataFactory.swtDefaults;

import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.recommenders.privacy.rcp.DatumRegistry;
import org.eclipse.recommenders.privacy.rcp.PrivacySettingsService;
import org.eclipse.recommenders.privacy.rcp.PrivateDatum;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PrivacyPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    private CheckboxTreeViewer globalPermissionsViewer;
    private Set<PrivateDatum> globalPermissionsInput;

    private DatumRegistry datumRegistry;
    private PrivacySettingsService globalPreferences;

    @Override
    public void init(IWorkbench workbench) {
        datumRegistry = new DatumRegistry();
        globalPreferences = new PrivacySettingsService();
        setMessage("Privacy Configuration");
    }

    @Override
    protected Control createContents(Composite parent) {
        createDescription(parent);
        createGlobalPermssionsView(parent);
        createButtons(parent);

        return parent;
    }

    protected Label createDescription(Composite parent) {
        Label label = new Label(parent, SWT.WRAP);
        label.setText("Privacy allow you to enable or disable various plug-ins sending usage data to their servers.");
        GridData data = new GridData(SWT.FILL, SWT.NONE, false, false);
        data.widthHint = 275;
        label.setLayoutData(data);
        return label;
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
                for (PrivateDatum item : globalPermissionsInput) {
                    globalPermissionsViewer.setSubtreeChecked(item, true);
                }
            }
        });

        Button disableAll = new Button(composite, SWT.PUSH);
        disableAll.setText("Disable All");
        disableAll.setLayoutData(swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING).create());
        disableAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                for (PrivateDatum item : globalPermissionsInput) {
                    globalPermissionsViewer.setSubtreeChecked(item, false);
                }
            }
        });
    }

    private void createGlobalPermssionsView(Composite parent) {
        Composite composite = createTreeViewLayout(parent, "Global Permissions:");

        globalPermissionsViewer = new CheckboxTreeViewer(composite, SWT.BORDER);
        globalPermissionsViewer.getControl().setLayoutData(
                fillDefaults().hint(SWT.DEFAULT, SWT.DEFAULT).grab(true, false).create());
        globalPermissionsViewer.setLabelProvider(new DatumLabelProvider());
        globalPermissionsViewer.setContentProvider(new DatumContentProvider());
        globalPermissionsInput = datumRegistry.readRegistredDatums();
        globalPermissionsViewer.setInput(globalPermissionsInput);
        globalPermissionsViewer.expandAll();

        for (PrivateDatum datum : globalPermissionsInput) {
            boolean allowed = globalPreferences.isAllowed(datum.getId());
            globalPermissionsViewer.setChecked(datum, allowed);
        }
        ColumnViewerToolTipSupport.enableFor(globalPermissionsViewer);
    }

    private Composite createTreeViewLayout(Composite parent, String title) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = layout.marginWidth = 0;
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        new Label(composite, SWT.NONE).setText(title);
        return composite;
    }

    @Override
    public void performApply() {
        for (PrivateDatum datum : globalPermissionsInput) {
            if (globalPermissionsViewer.getChecked(datum)) {
                globalPreferences.allow(datum.getId());
            } else {
                globalPreferences.disallow(datum.getId());
            }
        }
    }

    @Override
    public void performDefaults() {
        for (PrivateDatum datum : globalPermissionsInput) {
            globalPreferences.disallow(datum.getId());
            globalPermissionsViewer.setSubtreeChecked(datum, false);
        }
    }

    @Override
    public boolean performOk() {
        performApply();
        return super.performOk();
    }
}
