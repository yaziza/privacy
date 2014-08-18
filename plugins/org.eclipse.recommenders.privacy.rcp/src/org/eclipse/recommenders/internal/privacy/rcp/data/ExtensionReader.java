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

import static org.eclipse.recommenders.internal.privacy.rcp.Constants.*;
import static org.eclipse.recommenders.internal.privacy.rcp.data.ApprovalType.INSTALL;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.recommenders.privacy.rcp.IAdvancedPreferencesDialogFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Strings;

public class ExtensionReader {

    private static final Logger LOG = LoggerFactory.getLogger(ExtensionReader.class);

    private static final String DATUM_EXTENSION_POINT_ID = "org.eclipse.recommenders.privacy.rcp.datums"; //$NON-NLS-1$
    private static final String DATUM_ELEMENT = "datum"; //$NON-NLS-1$
    private static final String DATUM_ID_ATTRIBUTE = "datumId"; //$NON-NLS-1$

    private static final String PRINCIPAL_EXTENSION_POINT_ID = "org.eclipse.recommenders.privacy.rcp.principals"; //$NON-NLS-1$
    private static final String PRINCIPAL_ELEMENT = "principal"; //$NON-NLS-1$
    private static final String PRINCIPAL_ID_ATTRIBUTE = "principalId"; //$NON-NLS-1$

    private static final String PERMISSION_EXTENSION_POINT_ID = "org.eclipse.recommenders.privacy.rcp.permissions"; //$NON-NLS-1$
    private static final String PERMISSION_ELEMENT = "permission"; //$NON-NLS-1$

    private static final String ID_ATTRIBUTE = "id"; //$NON-NLS-1$
    private static final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$
    private static final String DESCRIPTION_ATTRIBUTE = "description"; //$NON-NLS-1$
    private static final String ICON_ATTRIBUTE = "icon"; //$NON-NLS-1$
    private static final String PURPOSE_ATTRIBUTE = "purpose"; //$NON-NLS-1$
    private static final String POLICY_URI_ATTRIBUTE = "policyUri"; //$NON-NLS-1$
    private static final String APPROVAL_TYPE_ATTRIBUTE = "askForApproval"; //$NON-NLS-1$
    private static final String ADVANCED_CONFIGURATION_DIALOG_ATTRIBUTE = "advancedPreferencesDialogFactory"; //$NON-NLS-1$

    private Map<String, PrivateDatum> privateDatumMap;
    private Map<String, Principal> principalMap;
    private Map<String, DatumCategory> datumCategoryMap;
    private Map<String, PrincipalCategory> principalCategoryMap;

    private Set<DatumCategory> datumCategorySet;
    private Set<PrincipalCategory> princiaplCategorySet;

    private Map<PrivatePermission, IConfigurationElement> advancedConfigMap;

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
                ImageDescriptor imageDescriptor = getImageDescriptor(pluginId, icon, DEFAULT_DATUM_ICON);

                if (isValidAttribute(datumId) && isValidAttribute(datumName) && isValidAttribute(datumDescription)) {
                    PrivateDatum datum = new PrivateDatum(datumId, datumName, datumDescription, imageDescriptor);
                    privateDatumMap.put(datumId, datum);
                    DatumCategory datumCategory = new DatumCategory(datum);
                    datumCategorySet.add(datumCategory);
                    datumCategoryMap.put(datumId, datumCategory);
                } else {
                    LOG.error("Failed to read private datum, invalid or missing attribute"); //$NON-NLS-1$
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
                ImageDescriptor imageDescriptor = getImageDescriptor(pluginId, icon, DEFAULT_PRINCIPAL_ICON);

                if (isValidAttribute(principalId) && isValidAttribute(principalName)
                        && isValidAttribute(principalDescription)) {
                    Principal principal = new Principal(principalId, principalName, principalDescription,
                            imageDescriptor);
                    principalMap.put(principalId, principal);
                    PrincipalCategory principalCategory = new PrincipalCategory(principal);
                    princiaplCategorySet.add(principalCategory);
                    principalCategoryMap.put(principalId, principalCategory);
                } else {
                    LOG.error("Failed to read principal, invalid or missing attribute"); //$NON-NLS-1$
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
        Map<PrivatePermission, IConfigurationElement> advancedConfigMap = this.advancedConfigMap = new HashMap<PrivatePermission, IConfigurationElement>();
        if (configurationElements == null) {
            return;
        }
        for (final IConfigurationElement configurationElement : configurationElements) {
            if (configurationElement.getName().equals(PERMISSION_ELEMENT)) {
                final String datumId = configurationElement.getAttribute(DATUM_ID_ATTRIBUTE);
                final String principalId = configurationElement.getAttribute(PRINCIPAL_ID_ATTRIBUTE);
                final String purpose = configurationElement.getAttribute(PURPOSE_ATTRIBUTE);
                final String policy = configurationElement.getAttribute(POLICY_URI_ATTRIBUTE);

                final String type = configurationElement.getAttribute(APPROVAL_TYPE_ATTRIBUTE);
                ApprovalType approvalType = getApprovalType(type);

                try {
                    PrivatePermission permission = null;
                    if (isValidAttribute(datumId) && isValidAttribute(principalId) && isValidAttribute(purpose)
                            && isValidAttribute(policy) && approvalType != null) {
                        permission = new PrivatePermission(privateDatumMap.get(datumId), principalMap.get(principalId),
                                purpose, policy, approvalType, this);

                        advancedConfigMap.put(permission, configurationElement);
                        datumCategoryMap.get(datumId).addPermissions(permission);
                        if (principalCategoryMap.containsKey(principalId)) {
                            principalCategoryMap.get(principalId).addPermissions(permission);
                        }
                    }
                    if (permission == null) {
                        LOG.error("Failed to read permission, invalid or missing attribute"); //$NON-NLS-1$
                    }
                } catch (Exception e) {
                    LOG.error("Error while reading permission", e); //$NON-NLS-1$
                }
            }
        }
        this.advancedConfigMap = advancedConfigMap;
    }

    public Set<DatumCategory> getDatumCategory() {
        return datumCategorySet;
    }

    public Set<PrincipalCategory> getPrincipalCategory() {
        return princiaplCategorySet;
    }

    public boolean isAdvancedPreferencesSupported(PrivatePermission permission) {
        IConfigurationElement configElement = advancedConfigMap.get(permission);
        String advancedConfigurationDialog = configElement.getAttribute(ADVANCED_CONFIGURATION_DIALOG_ATTRIBUTE);
        return !Strings.isNullOrEmpty(advancedConfigurationDialog);
    }

    public Optional<IAdvancedPreferencesDialogFactory> getAdvancedConfigurationDialog(PrivatePermission permission) {
        IConfigurationElement configElement = advancedConfigMap.get(permission);

        try {
            Object callback = configElement.createExecutableExtension(ADVANCED_CONFIGURATION_DIALOG_ATTRIBUTE);
            if (callback instanceof IAdvancedPreferencesDialogFactory) {
                IAdvancedPreferencesDialogFactory factory = (IAdvancedPreferencesDialogFactory) callback;
                return Optional.of(factory);
            }
        } catch (CoreException e) {
            LOG.info("Permission does not support advanced Configuration Mechanism"); //$NON-NLS-1$
        }
        return Optional.fromNullable(null);
    }

    private ImageDescriptor getImageDescriptor(String pluginId, String icon, String defaultIcon) {
        ImageDescriptor imageDescriptor;
        if (icon == null) {
            imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(BUNDLE_ID, defaultIcon);
        } else {
            imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, icon);
        }
        return imageDescriptor;
    }

    private ApprovalType getApprovalType(String type) {
        ApprovalType approvalType;
        if (type == null) {
            approvalType = INSTALL;
        } else if (!ApprovalType.isValidType(type.toUpperCase())) {
            approvalType = null;
        } else {
            approvalType = ApprovalType.valueOf(type.toUpperCase());
        }
        return approvalType;
    }

    private boolean isValidAttribute(String attribute) {
        return !Strings.isNullOrEmpty(attribute);
    }
}
