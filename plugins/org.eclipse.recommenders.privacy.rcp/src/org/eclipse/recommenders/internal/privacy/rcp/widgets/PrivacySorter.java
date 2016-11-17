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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.recommenders.internal.privacy.rcp.data.ICategory;
import org.eclipse.recommenders.internal.privacy.rcp.data.PrivatePermission;

import com.ibm.icu.text.Collator;

public class PrivacySorter extends ViewerComparator {

    @Override
    public int compare(Viewer viewer, Object first, Object second) {
        Collator collator = Collator.getInstance();
        if (first instanceof ICategory) {
            ICategory firstCategory = (ICategory) first;
            ICategory secondCategory = (ICategory) second;
            return collator.compare(firstCategory.getText(), secondCategory.getText());
        } else {
            PrivatePermission firstPermission = (PrivatePermission) first;
            PrivatePermission secondPermission = (PrivatePermission) second;
            return collator.compare(firstPermission.getDatumName(), secondPermission.getDatumName());
        }
    }
}
