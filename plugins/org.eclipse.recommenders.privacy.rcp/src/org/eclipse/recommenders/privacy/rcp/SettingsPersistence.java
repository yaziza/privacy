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

import static org.eclipse.recommenders.privacy.rcp.PermissionState.APPROVED;
import static org.eclipse.recommenders.privacy.rcp.PermissionState.DISAPPROVED;

import java.util.HashSet;
import java.util.Set;

public final class SettingsPersistence {

    private SettingsPersistence() { }

    public static Set<PrivatePermission> loadApproved(IPrivacySettingsService service, Set<? extends ICategory> input) {
        Set<PrivatePermission> permissions = new HashSet<PrivatePermission>();

        for (ICategory principal : input) {
            for (PrivatePermission permission : principal.getPermissions()) {
                if (service.isApproved(permission.getDatumId(), permission.getPrincipalId())) {
                    permissions.add(permission);
                }
            }
        }
        return permissions;
    }

    public static void store(IPrivacySettingsService service, Set<PrivatePermission> approvedPermissions, Set<PrivatePermission> disapprovedPermissions) {
        for (PrivatePermission permission : approvedPermissions) {
            service.setState(permission.getDatumId(), permission.getPrincipalId(), APPROVED);
        }
        for (PrivatePermission permission : disapprovedPermissions) {
            service.setState(permission.getDatumId(), permission.getPrincipalId(), DISAPPROVED);
        }
    }
}
