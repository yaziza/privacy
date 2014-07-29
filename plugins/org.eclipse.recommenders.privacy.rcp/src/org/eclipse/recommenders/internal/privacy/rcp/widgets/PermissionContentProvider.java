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

import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.recommenders.internal.privacy.rcp.data.ICategory;

public class PermissionContentProvider implements ITreeContentProvider {

    private Set<? extends ICategory> categorySet;

    @SuppressWarnings("unchecked")
    @Override
    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        categorySet = (Set<ICategory>) newInput;
    }

    @Override
    public void dispose() {
    }

    @Override
    public Object[] getElements(Object parent) {
        return categorySet.toArray();
    }

    @Override
    public Object getParent(Object child) {
        return null;
    }

    @Override
    public Object[] getChildren(Object parent) {
        if (parent instanceof ICategory) {
            ICategory category = (ICategory) parent;
            return category.getPermissions().toArray();
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object parent) {
        if (parent instanceof ICategory) {
            return true;
        }
        return false;
    }
}
