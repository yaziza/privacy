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
package org.eclipse.recommenders.internal.privacy.rcp.data;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.eclipse.jface.resource.ImageDescriptor;

public final class PrivateDatum {

    private final String id;
    private final String name;
    private final String description;
    private final ImageDescriptor icon;

    public PrivateDatum(String id, String name, String description, ImageDescriptor icon) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        this.description = requireNonNull(description);
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
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (getClass() != other.getClass()) {
            return false;
        }

        PrivateDatum that = (PrivateDatum) other;
        return Objects.equals(this.id, that.id) && Objects.equals(this.name, that.name)
                && Objects.equals(this.description, that.description) && Objects.equals(this.icon, that.icon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, icon);
    }
}
