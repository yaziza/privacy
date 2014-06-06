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
package org.eclipse.recommenders.privacy.rcp;

import static com.google.common.collect.Sets.intersection;

import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class PermissionFilter extends ViewerFilter {

    private final Set<PrivatePermission> detectedPermissions;

    public PermissionFilter(Set<PrivatePermission> detectedPermissions) {
        this.detectedPermissions = detectedPermissions;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof ICategory) {
            ICategory category = (ICategory) element;
            return !intersection(detectedPermissions, category.getPermissions()).isEmpty();
        } else {
            return true;
        }
    }
}
