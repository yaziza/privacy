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

import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.recommenders.privacy.rcp.PrivateDatum;

public class DatumContentProvider implements ITreeContentProvider {
    private Set<PrivateDatum> categoryList;

    @SuppressWarnings("unchecked")
    @Override
    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        categoryList = (Set<PrivateDatum>) newInput;
    }

    @Override
    public void dispose() {
    }

    @Override
    public Object[] getElements(Object parent) {
        return categoryList.toArray();
    }

    @Override
    public Object getParent(Object child) {
        return null;
    }

    @Override
    public Object[] getChildren(Object parent) {
        return null;
    }

    @Override
    public boolean hasChildren(Object parent) {
        return false;
    }
}
