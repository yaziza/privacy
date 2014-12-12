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

import static com.google.common.collect.Sets.filter;
import static org.eclipse.recommenders.privacy.rcp.PermissionState.*;

import java.util.Set;

import org.eclipse.recommenders.privacy.rcp.IPrivacySettingsService;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

public final class PrivacySettingsSerciveHelper {

    public static Set<PrivatePermission> loadApproved(IPrivacySettingsService service,
            Set<? extends ICategory> categories) {
        return filter(getCategoriesPermissions(categories), new LoadApprovedPredicate(service));
    }

    public static Set<PrivatePermission> suggestApproved(IPrivacySettingsService service,
            Set<? extends ICategory> categories) {
        return filter(getCategoriesPermissions(categories), new SuggestApprovedPredicate(service));
    }

    public static void store(IPrivacySettingsService service, Set<PrivatePermission> approvedPermissions,
            Set<PrivatePermission> disapprovedPermissions) {
        for (PrivatePermission permission : approvedPermissions) {
            service.setState(permission.getPrincipalId(), permission.getDatumId(), APPROVED);
        }
        for (PrivatePermission permission : disapprovedPermissions) {
            service.setState(permission.getPrincipalId(), permission.getDatumId(), DISAPPROVED);
        }
    }

    public static Set<PrivatePermission> getCategoriesPermissions(Set<? extends ICategory> categories) {
        Set<PrivatePermission> permissions = Sets.newHashSet();

        for (ICategory principal : categories) {
            for (PrivatePermission permission : principal.getPermissions()) {
                permissions.add(permission);
            }
        }
        return permissions;
    }

    public static Set<PrivatePermission> getPermissions(Set<PrivatePermission> permissionSet, String principalId,
            String... datumsIds) {
        return filter(permissionSet, new ExtractPermissionsPredicate(principalId, datumsIds));
    }

    public static final class LoadApprovedPredicate implements Predicate<PrivatePermission> {

        private final IPrivacySettingsService service;

        private LoadApprovedPredicate(IPrivacySettingsService service) {
            this.service = service;
        }

        @Override
        public boolean apply(PrivatePermission permission) {
            return service.isApproved(permission.getPrincipalId(), permission.getDatumId());
        }
    }

    public static final class SuggestApprovedPredicate implements Predicate<PrivatePermission> {

        private final IPrivacySettingsService service;

        private SuggestApprovedPredicate(IPrivacySettingsService service) {
            this.service = service;
        }

        @Override
        public boolean apply(PrivatePermission permission) {
            return !service.isNeverApproved(permission.getDatumId());
        }
    }

    public static final class ExtractPermissionsPredicate implements Predicate<PrivatePermission> {

        private final String principalId;
        private final String[] datumIds;

        private ExtractPermissionsPredicate(String principalId, String[] datumIds) {
            this.principalId = principalId;
            this.datumIds = datumIds;
        }

        @Override
        public boolean apply(PrivatePermission permission) {
            for (String datumId : datumIds) {
                if (permission.getPrincipalId().equals(principalId) && permission.getDatumId().equals(datumId)) {
                    return true;
                }
            }
            return false;
        }
    }

    private PrivacySettingsSerciveHelper() {
        // Not meant to be instantiated
    }
}
