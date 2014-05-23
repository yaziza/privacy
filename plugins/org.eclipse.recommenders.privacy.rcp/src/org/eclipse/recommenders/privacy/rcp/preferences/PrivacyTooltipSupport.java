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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class PrivacyTooltipSupport extends ColumnViewerToolTipSupport {

    protected PrivacyTooltipSupport(ColumnViewer viewer, int style, boolean manualActivation) {
        super(viewer, style, manualActivation);
    }

    @Override
    protected Composite createToolTipContentArea(Event event, Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = layout.marginHeight = 0;
        layout.verticalSpacing = layout.horizontalSpacing = 0;
        composite.setLayout(layout);

        Link link = new Link(composite, SWT.NONE);
        link.setText(getText(event));
        link.setLayoutData(GridDataFactory.fillDefaults()
                .hint(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH, SWT.DEFAULT).create());
        link.setBackground(event.widget.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        link.setForeground(event.widget.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(e.text));
                } catch (PartInitException ex) {
                    ex.printStackTrace();
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        return composite;
    }

    @Override
    public boolean isHideOnMouseDown() {
        return false;
    }

    public static final void enableFor(ColumnViewer viewer, int style) {
        new PrivacyTooltipSupport(viewer, style, false);
    }
}
