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
import com.google.common.base.Strings;

public class ExtensionReader {

    private static final Logger LOG = LoggerFactory.getLogger(ExtensionReader.class);

    private static final String DATUM_EXTENSION_POINT_ID = "org.eclipse.recommenders.privacy.rcp.datums";
    private static final String DATUM_ELEMENT = "datum";
    private static final String DATUM_ID_ATTRIBUTE = "datumId";

    private static final String PRINCIPAL_EXTENSION_POINT_ID = "org.eclipse.recommenders.privacy.rcp.principals";
    private static final String PRINCIPAL_ELEMENT = "principal";
    private static final String PRINCIPAL_ID_ATTRIBUTE = "principalId";

    private static final String PERMISSION_EXTENSION_POINT_ID = "org.eclipse.recommenders.privacy.rcp.permissions";
    private static final String PERMISSION_ELEMENT = "permission";

    private static final String ID_ATTRIBUTE = "id";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String DESCRIPTION_ATTRIBUTE = "description";
    private static final String ICON_ATTRIBUTE = "icon";
    private static final String PURPOSE_ATTRIBUTE = "purpose";
    private static final String POLICY_URI_ATTRIBUTE = "policyUri";

    private Map<String, PrivateDatum> privateDatumMap;
    private Map<String, Principal> principalMap;
    private Map<String, DatumCategory> datumCategoryMap;
    private Map<String, PrincipalCategory> principalCategoryMap;

    private Set<DatumCategory> datumCategorySet;
    private Set<PrincipalCategory> princiaplCategorySet;

    public ExtensionReader() {
        readRegisteredExtensions();
    }

    private void readRegisteredExtensions() {
        readRegisteredDatums();
        readRegisteredPrincipals();
        readRegisteredPermissions();
    }

    private void readRegisteredDatums() {
        final IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(DATUM_EXTENSION_POINT_ID);
        readRegisteredDatums(configurationElements);
    }

    @VisibleForTesting
    void readRegisteredDatums(IConfigurationElement... configurationElements) {
        Map<String, PrivateDatum> privateDatumMap = this.privateDatumMap = new HashMap<String, PrivateDatum>();
        Map<String, DatumCategory> datumCategoryMap = this.datumCategoryMap = new HashMap<String, DatumCategory>();
        Set<DatumCategory> datumCategorySet = this.datumCategorySet = new HashSet<DatumCategory>();

        if (configurationElements == null) {
            return;
        }
        for (final IConfigurationElement configurationElement : configurationElements) {
            if (configurationElement.getName().equals(DATUM_ELEMENT)) {
                final String pluginId = configurationElement.getContributor().getName();
                final String datumId = configurationElement.getAttribute(ID_ATTRIBUTE);
                final String datumName = configurationElement.getAttribute(NAME_ATTRIBUTE);
                final String datumDescription = configurationElement.getAttribute(DESCRIPTION_ATTRIBUTE);
                String icon = configurationElement.getAttribute(ICON_ATTRIBUTE);

                ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId,
                        icon == null ? ISharedImages.IMG_OBJ_ELEMENT : icon);

                if (isValidAttribute(datumId) && isValidAttribute(datumName) && isValidAttribute(datumDescription)) {
                    PrivateDatum datum = new PrivateDatum(datumId, datumName, datumDescription, imageDescriptor);
                    privateDatumMap.put(datumId, datum);
                    DatumCategory datumCategory = new DatumCategory(datum);
                    datumCategorySet.add(datumCategory);
                    datumCategoryMap.put(datumId, datumCategory);
                } else {
                    LOG.error("Failed to read private datum, invalid or missing attribute");
                }
            }
        }
        this.privateDatumMap = privateDatumMap;
        this.datumCategoryMap = datumCategoryMap;
        this.datumCategorySet = datumCategorySet;
    }

    private void readRegisteredPrincipals() {
        final IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(PRINCIPAL_EXTENSION_POINT_ID);
        readRegisteredPrincipals(configurationElements);
    }

    @VisibleForTesting
    void readRegisteredPrincipals(IConfigurationElement... configurationElements) {
        Map<String, Principal> principalMap = this.principalMap = new HashMap<String, Principal>();
        Map<String, PrincipalCategory> principalCategoryMap = this.principalCategoryMap = new HashMap<String, PrincipalCategory>();
        Set<PrincipalCategory> princiaplCategorySet = this.princiaplCategorySet = new HashSet<PrincipalCategory>();

        if (configurationElements == null) {
            return;
        }
        for (final IConfigurationElement configurationElement : configurationElements) {
            if (configurationElement.getName().equals(PRINCIPAL_ELEMENT)) {
                final String pluginId = configurationElement.getContributor().getName();
                final String principalId = configurationElement.getAttribute(ID_ATTRIBUTE);
                final String principalName = configurationElement.getAttribute(NAME_ATTRIBUTE);
                final String principalDescription = configurationElement.getAttribute(DESCRIPTION_ATTRIBUTE);
                String icon = configurationElement.getAttribute(ICON_ATTRIBUTE);

                ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
                        icon == null ? Constants.BUNDLE_ID : pluginId, icon == null ? Constants.PREF_FEATURE_ICON
                                : icon);

                if (isValidAttribute(principalId) && isValidAttribute(principalName)
                        && isValidAttribute(principalDescription)) {
                    Principal principal = new Principal(principalId, principalName, principalDescription,
                            imageDescriptor);
                    principalMap.put(principalId, principal);
                    PrincipalCategory principalCategory = new PrincipalCategory(principal);
                    princiaplCategorySet.add(principalCategory);
                    principalCategoryMap.put(principalId, principalCategory);
                } else {
                    LOG.error("Failed to read principal, invalid or missing attribute");
                }
            }
        }
        this.principalMap = principalMap;
        this.principalCategoryMap = principalCategoryMap;
        this.princiaplCategorySet = princiaplCategorySet;
    }

    private void readRegisteredPermissions() {
        final IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(PERMISSION_EXTENSION_POINT_ID);
        readRegisteredPermissions(configurationElements);
    }

    @VisibleForTesting
    void readRegisteredPermissions(IConfigurationElement... configurationElements) {
        if (configurationElements == null) {
            return;
        }
        for (final IConfigurationElement configurationElement : configurationElements) {
            if (configurationElement.getName().equals(PERMISSION_ELEMENT)) {
                final String datumId = configurationElement.getAttribute(DATUM_ID_ATTRIBUTE);
                final String principalId = configurationElement.getAttribute(PRINCIPAL_ID_ATTRIBUTE);
                final String purpose = configurationElement.getAttribute(PURPOSE_ATTRIBUTE);
                final String policy = configurationElement.getAttribute(POLICY_URI_ATTRIBUTE);

                if (isValidAttribute(datumId) && isValidAttribute(principalId) && isValidAttribute(purpose)
                        && isValidAttribute(policy)) {
                    PrivatePermission permission = new PrivatePermission(privateDatumMap.get(datumId),
                            principalMap.get(principalId), purpose, policy);
                    datumCategoryMap.get(datumId).addPermission(permission);
                    if (principalCategoryMap.containsKey(principalId)) {
                        principalCategoryMap.get(principalId).addPermission(permission);
                    }
                } else {
                    LOG.error("Failed to read permissions, invalid or missing attribute");
                }
            }
        }
    }

    public Set<DatumCategory> getDatumCategory() {
        return datumCategorySet;
    }

    public Set<PrincipalCategory> getPrincipalCategory() {
        return princiaplCategorySet;
    }

    private boolean isValidAttribute(String attribute) {
        return !Strings.isNullOrEmpty(attribute);
    }
}
