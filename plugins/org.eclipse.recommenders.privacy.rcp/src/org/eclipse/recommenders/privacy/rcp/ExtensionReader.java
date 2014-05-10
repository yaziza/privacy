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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

public class ExtensionReader {

    private static final Logger LOG = LoggerFactory.getLogger(ExtensionReader.class);

    private static final String DATUM_EXTENSION_POINT_ID = "org.eclipse.recommenders.privacy.rcp.datums";
    private static final String DATUM_EXTENSION = "datum";
    private static final String ATTRIBUTE_ID = "id";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_DESCRIPTION = "description";
    private static final String ATTRIBUTE_ICON = "icon";

    private static final String PERMISSION_EXTENSION_POINT_ID = "org.eclipse.recommenders.privacy.rcp.permissions";
    private static final String PERMISSION_EXTENSION = "permission";
    private static final String ATTRIBUTE_DATUM_ID = "datumId";
    private static final String ATTRIBUTE_PURPOSE = "purpose";

    private Set<DatumCategory> datumCategorySet = new HashSet<DatumCategory>();
    private Set<PrincipalCategory> principalCategorySet = new HashSet<PrincipalCategory>();

    private Map<String, PrincipalCategory> principalCategoryMap = new HashMap<String, PrincipalCategory>();

    public Set<DatumCategory> readRegistredDatums() {
        final IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(DATUM_EXTENSION_POINT_ID);
        return readRegistredDatums(configurationElements);
    }

    @VisibleForTesting
    Set<DatumCategory> readRegistredDatums(IConfigurationElement... configurationElements) {
        if (configurationElements == null) {
            return datumCategorySet;
        }
        for (final IConfigurationElement configurationElement : configurationElements) {
            if (configurationElement.getName().equals(DATUM_EXTENSION)) {
                final String pluginId = configurationElement.getContributor().getName();
                final String datumId = configurationElement.getAttribute(ATTRIBUTE_ID);
                final String datumName = configurationElement.getAttribute(ATTRIBUTE_NAME);
                final String datumDescription = configurationElement.getAttribute(ATTRIBUTE_DESCRIPTION);

                String icon = configurationElement.getAttribute(ATTRIBUTE_ICON);
                if (icon == null) {
                    icon = ISharedImages.IMG_OBJ_ELEMENT;
                }

                ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, icon);

                if (isValidAttribute(datumId) && isValidAttribute(datumName) && isValidAttribute(datumDescription)) {
                    PrivateDatum datum = new PrivateDatum(datumId, datumName, datumDescription, imageDescriptor);
                    datumCategorySet.add(new DatumCategory(datum));
                } else {
                    LOG.error("Failed to read private datum, invalid or missing attribute");
                }
            }
        }
        return datumCategorySet;
    }

    public Set<PrincipalCategory> readRegistredPrincipals() {
        final IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(PERMISSION_EXTENSION_POINT_ID);
        return readRegistredPrincipals(configurationElements);
    }

    @VisibleForTesting
    Set<PrincipalCategory> readRegistredPrincipals(IConfigurationElement... configurationElements) {
        if (configurationElements == null || datumCategorySet == null) {
            return principalCategorySet;
        }
        for (final IConfigurationElement configurationElement : configurationElements) {
            if (configurationElement.getName().equals(PERMISSION_EXTENSION)) {
                final String pluginId = configurationElement.getContributor().getName();
                final String datumId = configurationElement.getAttribute(ATTRIBUTE_DATUM_ID);
                final String purpose = configurationElement.getAttribute(ATTRIBUTE_PURPOSE);

                if (isValidAttribute(datumId) && isValidAttribute(purpose)) {
                    PrincipalCategory principal;
                    if (principalCategoryMap.containsKey(pluginId)) {
                        principal = principalCategoryMap.get(pluginId);
                    } else {
                        principal = new PrincipalCategory(pluginId);
                        principalCategoryMap.put(pluginId, principal);
                    }
                    Optional<DatumCategory> optionalDatum = find(datumCategorySet, datumId);
                    if (optionalDatum.isPresent()) {
                        DatumCategory datum = optionalDatum.get();
                        PrivatePermission permission = new PrivatePermission(principal.getId(), datum.getId(),
                                datum.getName(), purpose);
                        principal.addPermission(permission);
                        datum.addPermission(permission);
                        principalCategorySet.add(principal);
                    }
                } else {
                    LOG.error("Failed to read principal permissions, invalid or missing attribute");
                }
            }
        }
        return principalCategorySet;
    }

    private boolean isValidAttribute(String attribute) {
        return !(attribute == null) && !attribute.isEmpty();
    }

    private Optional<DatumCategory> find(Set<DatumCategory> datums, String id) {
        for (DatumCategory datum : datums) {
            if (id.equals(datum.getId())) {
                return Optional.fromNullable(datum);
            }
        }
        return Optional.fromNullable(null);
    }
}
