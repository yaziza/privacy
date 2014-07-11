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

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.recommenders.privacy.rcp.Constants;
import org.eclipse.recommenders.privacy.rcp.IPrivacySettingsService;
import org.eclipse.recommenders.privacy.rcp.PreferencesHelper;
import org.eclipse.recommenders.privacy.rcp.Startup;
import org.eclipse.recommenders.privacy.rcp.l10n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.ibm.icu.text.MessageFormat;

public class UserIdPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private IPrivacySettingsService service;

    @Override
    public void init(IWorkbench workbench) {
        setMessage(Messages.USERID_PREFPAGE_TITLE);
        BundleContext bundleContext = FrameworkUtil.getBundle(Startup.class).getBundleContext();
        IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
        service = eclipseContext.get(IPrivacySettingsService.class);
    }

    @Override
    protected Control createContents(Composite parent) {
        createDescription(parent, Messages.USERID_PREFPAGE_DESCRIPTION);
        return createUserIdLabel(parent);
    }

    private void createDescription(Composite parent, String message) {
        Link link = new Link(parent, SWT.WRAP);
        GridDataFactory.fillDefaults().hint(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH, SWT.DEFAULT).grab(true, false)
        .applyTo(link);
        final String linkToPreferencePage = PreferencesHelper.createLinkLabelToPreferencePage(Constants.PREF_PAGE_ID);
        link.setText(MessageFormat.format(Messages.USERID_PREFPAGE_DESCRIPTION, linkToPreferencePage));

        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(null, Constants.PREF_PAGE_ID, null,
                        null);
                dialog.open();
            }
        });
    }

    private Control createUserIdLabel(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(composite);
        GridDataFactory.fillDefaults().grab(false, true).span(2, 1).applyTo(composite);

        Button uuidButton = new Button(composite, SWT.PUSH);
        GridDataFactory.swtDefaults().applyTo(uuidButton);
        uuidButton.setText(Messages.BUTTON_GENERATE);

        final Text text = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER | SWT.READ_ONLY);
        GridDataFactory.swtDefaults().applyTo(text);
        text.setText(service.getUserId().toString());

        uuidButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                service.generateUserId();
                text.setText(service.getUserId().toString());
            }
        });
        return parent;
    }
}
