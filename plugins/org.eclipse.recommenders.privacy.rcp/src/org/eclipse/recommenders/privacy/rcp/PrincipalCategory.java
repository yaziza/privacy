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

import org.eclipse.swt.graphics.Image;

public class PrincipalCategory implements ICategory {

    private Set<PrivatePermission> permissions = new HashSet<PrivatePermission>();;
    private final Principal principal;

    public PrincipalCategory(Principal principal) {
        this.principal = checkNotNull(principal);
    }

    public String getId() {
        return principal.getId();
    }

    @Override
    public String getText() {
        return principal.getId();
    }

    @Override
    public String getTooltip() {
        return principal.getName();
    }

    @Override
    public Image getImageDescriptor() {
        return principal.getIcon().createImage();
    }

    public Set<PrivatePermission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public void addPermission(PrivatePermission permission) {
        permissions.add(permission);
    }

    @Override
    public String toString() {
        return principal.getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PrincipalCategory other = (PrincipalCategory) obj;
        if (!principal.equals(other.principal)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((principal == null) ? 0 : principal.hashCode());
        return result;
    }
}
