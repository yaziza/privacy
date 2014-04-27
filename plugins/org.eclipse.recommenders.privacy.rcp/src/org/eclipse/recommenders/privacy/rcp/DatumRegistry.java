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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

public class DatumRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(DatumRegistry.class);

    private static final String EXTENSION_POINT = "org.eclipse.recommenders.privacy.rcp.datums";
    private static final String DATUM = "datum";
    private static final String ATTRIBUTE_ID = "id";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_DESCRIPTION = "description";
    private static final String ATTRIBUTE_ICON = "icon";

    public Set<PrivateDatum> readRegistredDatums() {
        final IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(EXTENSION_POINT);
        return readRegistredDatums(configurationElements);
    }

    @VisibleForTesting
    Set<PrivateDatum> readRegistredDatums(IConfigurationElement... configurationElements) {
        Set<PrivateDatum> globalPermissions = new HashSet<PrivateDatum>();
        if (configurationElements == null) {
            return globalPermissions;
        }
        for (final IConfigurationElement configurationElement : configurationElements) {
            if (configurationElement.getName().equals(DATUM)) {
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
                    PrivateDatum item = new PrivateDatum(datumId, datumName, datumDescription, imageDescriptor);
                    globalPermissions.add(item);
                } else {
                    LOG.error("Failed to read private datum, invalid or missing attribute");
                }
            }
        }
        return globalPermissions;
    }

    private boolean isValidAttribute(String attribute) {
        return !(attribute == null) && !attribute.isEmpty();
    }
}
