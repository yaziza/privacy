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

import java.text.MessageFormat;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.recommenders.privacy.rcp.ICategory;
import org.eclipse.recommenders.privacy.rcp.PrivatePermission;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class DatumLabelProvider extends ColumnLabelProvider {

    @Override
    public String getText(Object element) {
        if (element instanceof ICategory) {
            ICategory datumCategory = (ICategory) element;
            return datumCategory.getText();
        }
        PrivatePermission permision = (PrivatePermission) element;
        return permision.getPrincipalName();
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof ICategory) {
            ICategory datumCategory = (ICategory) element;
            return datumCategory.getImageDescriptor();
        }
        return null;
    }

    @Override
    public String getToolTipText(Object element) {
        if (element instanceof ICategory) {
            ICategory datumCategory = (ICategory) element;
            return datumCategory.getTooltip();
        }
        PrivatePermission permission = (PrivatePermission) element;
        String formatter = "Purpose: {0}\n\nPolicy: <a href=\"{1}\">{1}</a>";
        return MessageFormat.format(formatter, permission.getPurpose(), permission.getPolicyUri());
    }

    @Override
    public Color getForeground(Object element) {
        if (element instanceof ICategory) {
            ICategory datumCategory = (ICategory) element;
            boolean unused = datumCategory.getPermissions().isEmpty();
            Color gray = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
            return unused ? gray : null;
        }
        return null;
    }
}
