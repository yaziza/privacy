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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.recommenders.internal.privacy.rcp.Constants;
import org.eclipse.recommenders.internal.privacy.rcp.l10n.Messages;
import org.eclipse.recommenders.internal.privacy.rcp.preferences.PreferencesHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.ibm.icu.text.MessageFormat;

public class IntroPage extends WizardPage {

    private GridDataFactory gdf;
    private GridLayoutFactory glf;

    protected IntroPage() {
        super(Messages.WIZARD_INTRO_PAGE_TITLE);
        setTitle(Messages.WIZARD_INTRO_PAGE_TITLE);
        setDescription(Messages.WIZARD_INTRO_PAGE_DESCRIPTION);
        setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Constants.BUNDLE_ID, Constants.BANNER_ICON));
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.WRAP);
        glf = GridLayoutFactory.fillDefaults();
        glf.numColumns(2).applyTo(container);
        gdf = GridDataFactory.fillDefaults().grab(true, false)
                .hint(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH, SWT.DEFAULT);

        createDescription(container);
        createPreferenceLink(container);
        createWebsiteLink(container);
        setControl(container);

        Dialog.applyDialogFont(container);
    }

    private void createDescription(Composite parent) {
        Label label = new Label(parent, SWT.WRAP);
        gdf.applyTo(label);
        label.setText(Messages.WIZARD_INTRO_PAGE_CONTENT);
    }

    private void createPreferenceLink(Composite parent) {
        Composite container = new Composite(parent, SWT.WRAP);
        glf.numColumns(1).applyTo(container);

        Link link = new Link(parent, SWT.WRAP);
        gdf.applyTo(link);
        final String linkToPreferencePage = PreferencesHelper.createLinkLabelToPreferencePage(PREF_PAGE_ID);
        link.setText(MessageFormat.format(Messages.WIZARD_INTRO_PAGE_PREFS_LINK_DESCRIPTION, linkToPreferencePage));
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(null, PREF_PAGE_ID, null, null);
                getWizard().performCancel();
                getWizard().getContainer().getShell().close();
                dialog.open();
            }
        });
    }

    private void createWebsiteLink(Composite parent) {
        Composite container = new Composite(parent, SWT.WRAP);
        glf.numColumns(1).applyTo(container);

        Link link = new Link(parent, SWT.WRAP);
        gdf.applyTo(link);
        link.setText(MessageFormat.format(Messages.WIZARD_INTRO_PAGE_WEBSITE_LINK_DESCRIPTION,
                Messages.WIZARD_INTRO_PAGE_LINK_URI));
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                try {
                    PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(event.text));
                } catch (PartInitException ex) {
                    ex.printStackTrace();
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
