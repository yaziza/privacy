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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.recommenders.internal.privacy.rcp.data.ICategory;
import org.eclipse.recommenders.internal.privacy.rcp.data.PrivatePermission;
import org.eclipse.recommenders.internal.privacy.rcp.l10n.Messages;
import org.eclipse.swt.graphics.Image;

import com.ibm.icu.text.MessageFormat;

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
        PrivatePermission permision = (PrivatePermission) element;
        return permision.getPrincipalIcon();
    }

    @Override
    public String getToolTipText(Object element) {
        if (element instanceof ICategory) {
            ICategory datumCategory = (ICategory) element;
            return datumCategory.getTooltip();
        }
        PrivatePermission permission = (PrivatePermission) element;
        return MessageFormat.format(Messages.TOOLTIP_INTERESTED_PARTY, permission.getPurpose(),
                permission.getPolicyUri());
    }
}
