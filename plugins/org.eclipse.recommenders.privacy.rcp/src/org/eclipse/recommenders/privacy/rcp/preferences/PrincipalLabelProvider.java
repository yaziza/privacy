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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.recommenders.privacy.rcp.ICategory;
import org.eclipse.recommenders.privacy.rcp.PrivatePermission;
import org.eclipse.recommenders.privacy.rcp.l10n.Messages;
import org.eclipse.swt.graphics.Image;

import com.ibm.icu.text.MessageFormat;

public class PrincipalLabelProvider extends ColumnLabelProvider {

    @Override
    public String getText(Object element) {
        if (element instanceof ICategory) {
            ICategory principalCategory = (ICategory) element;
            return principalCategory.getText();
        }
        PrivatePermission permission = (PrivatePermission) element;
        return permission.getDatumName();
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof ICategory) {
            ICategory principalCategory = (ICategory) element;
            return principalCategory.getImageDescriptor();
        }
        PrivatePermission permision = (PrivatePermission) element;
        return permision.getDatumIcon();
    }

    @Override
    public String getToolTipText(Object element) {
        if (element instanceof ICategory) {
            ICategory principalCategory = (ICategory) element;
            return principalCategory.getTooltip();
        }
        PrivatePermission permission = (PrivatePermission) element;
        String formatter = Messages.TOOLTIP_INTERESTED_PARTY;
        return MessageFormat.format(formatter, permission.getPurpose(), permission.getPolicyUri());
    }
}
