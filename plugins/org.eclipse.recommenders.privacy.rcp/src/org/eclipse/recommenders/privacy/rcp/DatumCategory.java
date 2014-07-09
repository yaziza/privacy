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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public class DatumCategory implements ICategory {

    private final Set<PrivatePermission> permissionSet = new HashSet<PrivatePermission>();
    private final PrivateDatum datum;

    public DatumCategory(PrivateDatum datum) {
        this.datum = checkNotNull(datum);
    }

    public String getId() {
        return datum.getId();
    }

    public String getDescription() {
        return datum.getDescription();
    }

    public ImageDescriptor getIcon() {
        return datum.getIcon();
    }

    public String getName() {
        return datum.getName();
    }

    @Override
    public String getText() {
        return getName();
    }

    @Override
    public String getTooltip() {
        return getDescription();
    }

    @Override
    public Image getImageDescriptor() {
        return datum.getIcon().createImage();
    }

    @Override
    public Set<PrivatePermission> getPermissions() {
        return Collections.unmodifiableSet(permissionSet);
    }

    public void addPermissions(PrivatePermission... permissions) {
        for (PrivatePermission permission : permissions) {
            permissionSet.add(permission);
        }
    }

    @Override
    public String toString() {
        return datum.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DatumCategory other = (DatumCategory) obj;
        if (!datum.equals(other.datum)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (datum == null ? 0 : datum.hashCode());
        return result;
    }
}
