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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class PrincipalCategory implements ICategory {

    private final Set<PrivatePermission> permissions = new HashSet<PrivatePermission>();;
    private final String pluginId;

    public PrincipalCategory(String pluginId) {
        this.pluginId = checkNotNull(pluginId);
    }

    public String getId() {
        return pluginId;
    }

    @Override
    public String getText() {
        return getId();
    }

    @Override
    public String getTooltip() {
        return getId();
    }

    @Override
    public Image getImageDescriptor() {
        ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(Constants.BUNDLE_ID,
                ISharedImages.IMG_OBJ_FOLDER);
        return imageDescriptor.createImage();
    }

    public Set<PrivatePermission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public void addPermission(PrivatePermission permission) {
        permissions.add(permission);
    }

    @Override
    public String toString() {
        return pluginId;
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
        if (!pluginId.equals(other.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pluginId == null) ? 0 : pluginId.hashCode());
        return result;
    }
}
