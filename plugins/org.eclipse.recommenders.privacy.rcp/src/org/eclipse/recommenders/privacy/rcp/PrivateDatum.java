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

import org.eclipse.jface.resource.ImageDescriptor;

public class PrivateDatum {

    private final String id;
    private final String name;
    private final String description;
    private final ImageDescriptor icon;

    public PrivateDatum(String id, String name, String description, ImageDescriptor icon) {
        this.id = checkNotNull(id);
        this.name = checkNotNull(name);
        this.description = checkNotNull(description);
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ImageDescriptor getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        PrivateDatum other = (PrivateDatum) obj;
        if (!id.equals(other.getId())) {
            return false;
        }
        if (!name.equals(other.getName()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
}
