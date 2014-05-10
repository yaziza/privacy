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

public class PrivatePermission {

    private final String pluginId;
    private final String datumId;
    private final String datumName;
    private final String purpose;

    public PrivatePermission(String pluginId, String datumId, String datumName, String purpose) {
        this.pluginId = checkNotNull(pluginId);
        this.datumId = checkNotNull(datumId);
        this.datumName = checkNotNull(datumName);
        this.purpose = checkNotNull(purpose);
    }

    public String getPluginId() {
        return pluginId;
    }

    public String getDatumId() {
        return datumId;
    }

    public String getDatumName() {
        return datumName;
    }

    public String getPurpose() {
        return purpose;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PrivatePermission other = (PrivatePermission) obj;
        if (!datumId.equals(other.datumId)) {
            return false;
        }
        if (!pluginId.equals(other.pluginId)) {
            return false;
        }
        if (!purpose.equals(other.getPurpose())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((datumId == null) ? 0 : datumId.hashCode());
        result = prime * result + ((pluginId == null) ? 0 : pluginId.hashCode());
        result = prime * result + ((purpose == null) ? 0 : purpose.hashCode());
        return result;
    }
}
