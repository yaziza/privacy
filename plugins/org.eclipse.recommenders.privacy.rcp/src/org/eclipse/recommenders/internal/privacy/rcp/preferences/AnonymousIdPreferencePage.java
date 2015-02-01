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

import static org.eclipse.recommenders.internal.privacy.rcp.Constants.PREF_PAGE_ID;
import static org.eclipse.recommenders.internal.privacy.rcp.data.PrivacySettingsSerciveHelper.loadApproved;

import java.util.Set;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.recommenders.internal.privacy.rcp.data.DatumCategory;
import org.eclipse.recommenders.internal.privacy.rcp.data.ExtensionReader;
import org.eclipse.recommenders.internal.privacy.rcp.l10n.Messages;
import org.eclipse.recommenders.internal.privacy.rcp.widgets.SingleDatumWidget;
import org.eclipse.recommenders.privacy.rcp.IAnonymousIdService;
import org.eclipse.recommenders.privacy.rcp.IPrivacySettingsService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.ibm.icu.text.MessageFormat;

public class AnonymousIdPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private static final String ANONYMOUS_ID_DATUM = "org.eclipse.recommenders.privacy.rcp.datums.anonymousId"; //$NON-NLS-1$

    private IAnonymousIdService anonymousIdService;
    private IPrivacySettingsService settingsService;
    private ExtensionReader extensionReader;
    private Set<DatumCategory> datumCategorySet;
    private SingleDatumWidget anonymousIdWidget;

    @Override
    public void init(IWorkbench workbench) {
        setMessage(Messages.ANONYMOUS_ID_PREFPAGE_TITLE);
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
        IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
        anonymousIdService = eclipseContext.get(IAnonymousIdService.class);
        settingsService = eclipseContext.get(IPrivacySettingsService.class);
        extensionReader = new ExtensionReader();
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);
        GridLayoutFactory.fillDefaults().applyTo(composite);

        createDescription(composite, Messages.ANONYMOUS_ID_PREFPAGE_DESCRIPTION);
        createAnonymousIdLabel(composite);
        createPermissionWidget(composite);

        applyDialogFont(composite);
        return parent;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            anonymousIdWidget.setCheckedPermissions(loadApproved(settingsService, datumCategorySet));
            anonymousIdWidget.refresh();
        }
        super.setVisible(visible);
    }

    private void createPermissionWidget(Composite parent) {
        datumCategorySet = extensionReader.getDatumCategory();

        anonymousIdWidget = new SingleDatumWidget(datumCategorySet, ANONYMOUS_ID_DATUM);
        anonymousIdWidget.createContents(parent);
    }

    private void createDescription(Composite parent, String message) {
        Link link = new Link(parent, SWT.WRAP);
        GridDataFactory.fillDefaults().hint(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH, SWT.DEFAULT).grab(true, false)
                .applyTo(link);
        final String linkToPreferencePage = PreferencesHelper.createLinkLabelToPreferencePage(PREF_PAGE_ID);
        link.setText(MessageFormat.format(Messages.ANONYMOUS_ID_PREFPAGE_DESCRIPTION, linkToPreferencePage));

        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(null, PREF_PAGE_ID, null, null);
                dialog.open();
            }
        });
    }

    private Control createAnonymousIdLabel(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(composite);
        GridDataFactory.fillDefaults().grab(false, false).span(3, 1).applyTo(composite);

        Label label = new Label(composite, SWT.WRAP);
        GridDataFactory.swtDefaults().applyTo(label);
        label.setText(Messages.LABEL_ANONYMOUS_ID);

        final Text text = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER | SWT.READ_ONLY);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(text);
        text.setText(anonymousIdService.getAnonymousId().toString());

        Button uuidButton = new Button(composite, SWT.PUSH);
        GridDataFactory.swtDefaults().applyTo(uuidButton);
        uuidButton.setText(Messages.BUTTON_GENERATE);
        uuidButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                anonymousIdService.generateAnonymousId();
                text.setText(anonymousIdService.getAnonymousId().toString());
            }
        });

        return parent;
    }
}
