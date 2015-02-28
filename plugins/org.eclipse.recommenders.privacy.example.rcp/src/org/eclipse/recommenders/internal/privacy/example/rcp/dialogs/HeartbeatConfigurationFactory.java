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
package org.eclipse.recommenders.internal.privacy.example.rcp.dialogs;

import static org.eclipse.recommenders.internal.privacy.example.rcp.Constants.*;

import java.io.IOException;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.recommenders.internal.privacy.example.rcp.l10n.Messages;
import org.eclipse.recommenders.privacy.rcp.IConfigurationDialogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.service.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartbeatConfigurationFactory implements IConfigurationDialogFactory {

    private static final Logger LOG = LoggerFactory.getLogger(HeartbeatConfigurationFactory.class);

    @Override
    public void open(Shell shell) {
        ConfigurationDialog configurationDialog = new ConfigurationDialog(shell);
        configurationDialog.open();
    }

    private class ConfigurationDialog extends TrayDialog {

        private ComboViewer comboViewer;
        private HeartbeatInterval heartbeatDelay;
        private ScopedPreferenceStore prefStore;

        protected ConfigurationDialog(Shell parentShell) {
            super(parentShell);
            Preferences rootNode = InstanceScope.INSTANCE.getNode(BUNDLE_ID);
            this.prefStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, rootNode.node(PREF_NODE_ADVANCED)
                    .absolutePath());
            setHelpAvailable(false);
            setShellStyle(getShellStyle() | SWT.SHEET);
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite composite = (Composite) super.createDialogArea(parent);
            createDescriptionLabel(composite);
            createSelectionLabel(composite);

            Dialog.applyDialogFont(parent);
            return composite;
        }

        private void createDescriptionLabel(Composite parent) {
            Label label = new Label(parent, SWT.WRAP);
            GridDataFactory.fillDefaults().hint(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH, SWT.DEFAULT)
                    .applyTo(label);
            label.setText(Messages.CONFIGURATION_DIALOG_DESCRIPTION);
        }

        private void createSelectionLabel(Composite parent) {
            comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
            GridDataFactory.fillDefaults().grab(true, false)
                    .hint(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH, SWT.DEFAULT).applyTo(comboViewer.getControl());
            comboViewer.setContentProvider(ArrayContentProvider.getInstance());
            comboViewer.setLabelProvider(new LabelProvider() {
                @Override
                public String getText(Object element) {
                    HeartbeatInterval delay = (HeartbeatInterval) element;
                    return delay.toString();
                };
            });
            comboViewer.setInput(HeartbeatInterval.values());
            setComboSelection();
        }

        private void setComboSelection() {
            String selection = prefStore.getString(PREF_DELAY);
            HeartbeatInterval delay;
            try {
                delay = HeartbeatInterval.valueOf(selection);
            } catch (IllegalArgumentException ex) {
                delay = HeartbeatInterval.HOURLY;
            }
            comboViewer.setSelection(new StructuredSelection(delay));
        }

        @Override
        protected void okPressed() {
            persistComboSelection();
            super.okPressed();
        }

        private void persistComboSelection() {
            StructuredSelection selection = (StructuredSelection) comboViewer.getSelection();
            heartbeatDelay = (HeartbeatInterval) selection.getFirstElement();
            prefStore.setValue(PREF_DELAY, heartbeatDelay.name());
            try {
                prefStore.save();
            } catch (IOException e) {
                LOG.error("Failed to flush preferences", e); //$NON-NLS-1$
            }
        }

        @Override
        protected boolean isResizable() {
            return true;
        }
    }
}
