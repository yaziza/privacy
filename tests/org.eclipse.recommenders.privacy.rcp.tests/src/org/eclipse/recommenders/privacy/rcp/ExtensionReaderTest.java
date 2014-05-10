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

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ExtensionReaderTest {
    private static final String DATUM_EXTENSION = "datum";
    private static final String ATTRIBUTE_ID = "id";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_DESCRIPTION = "description";
    private static final String ATTRIBUTE_ICON = "icon";

    private static final String PERMISSION_EXTENSION = "permission";
    private static final String ATTRIBUTE_DATUM_ID = "datumId";
    private static final String ATTRIBUTE_PURPOSE = "purpose";

    private IConfigurationElement mockConfigElement(String name, ImmutableMap<String, String> map) {
        IConfigurationElement element = mock(IConfigurationElement.class);
        IContributor contributor = mock(IContributor.class);

        when(element.getName()).thenReturn(name);

        for (String key : map.keySet()) {
            when(element.getAttribute(key)).thenReturn(map.get(key));
        }
        when(contributor.getName()).thenReturn("org.example.privacy");
        when(element.getContributor()).thenReturn(contributor);
        return element;
    }

    @Test
    public final void testNoExtensionsFound() {
        ExtensionReader sut = new ExtensionReader();
        Set<DatumCategory> datums = sut.readRegistredDatums(null);
        assertThat(datums.size(), is(0));
        datums = sut.readRegistredDatums(new IConfigurationElement[] {});
        assertThat(datums.size(), is(0));

        Set<PrincipalCategory> principals = sut.readRegistredPrincipals(null);
        assertThat(principals.size(), is(0));
        principals = sut.readRegistredPrincipals(new IConfigurationElement[] {});
        assertThat(principals.size(), is(0));
    }

    @Test
    public final void testDatumExtensionWithoutIcon() {
        IConfigurationElement configElement = mockConfigElement(DATUM_EXTENSION, ImmutableMap.of(ATTRIBUTE_ID,
                "some id", ATTRIBUTE_NAME, "some name", ATTRIBUTE_DESCRIPTION, "some description"));

        ExtensionReader sut = new ExtensionReader();
        Set<DatumCategory> datums = sut.readRegistredDatums(configElement);

        DatumCategory datum = getOnlyElement(datums);
        assertThat(datum.getId(), is("some id"));
        assertThat(datum.getName(), is("some name"));
        assertThat(datum.getDescription(), is("some description"));

        ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin("org.example.privacy",
                ISharedImages.IMG_OBJ_ELEMENT);
        assertThat(datum.getIcon(), is(imageDescriptor));
    }

    @Test
    public final void testDatumExtensionIcon() {
        IConfigurationElement configElement = mockConfigElement(DATUM_EXTENSION, ImmutableMap.of(ATTRIBUTE_ID,
                "some id", ATTRIBUTE_NAME, "some name", ATTRIBUTE_DESCRIPTION, "some description", ATTRIBUTE_ICON,
                "datum.png"));

        ExtensionReader sut = new ExtensionReader();
        Set<DatumCategory> datums = sut.readRegistredDatums(configElement);

        assertThat(datums.size(), is(1));
        DatumCategory datum = getOnlyElement(datums);
        assertThat(datum.getId(), is("some id"));
        assertThat(datum.getName(), is("some name"));
        assertThat(datum.getDescription(), is("some description"));

        ImageDescriptor imageDescriptor = AbstractUIPlugin
                .imageDescriptorFromPlugin("org.example.privacy", "datum.png");
        assertThat(datum.getIcon(), is(imageDescriptor));
    }

    @Test
    public final void testUnkownExtensions() {
        IConfigurationElement configElement = mockConfigElement("unknown",
                ImmutableMap.of(ATTRIBUTE_ID, "some id", ATTRIBUTE_NAME, "some name"));

        ExtensionReader sut = new ExtensionReader();
        Set<DatumCategory> datums = sut.readRegistredDatums(configElement);

        assertThat(datums.size(), is(0));
    }

    @Test
    public final void testDatumExtensionMissingAttributes() {
        IConfigurationElement configElement = mockConfigElement(DATUM_EXTENSION,
                ImmutableMap.of(ATTRIBUTE_ID, "some id"));

        ExtensionReader sut = new ExtensionReader();
        Set<DatumCategory> datums = sut.readRegistredDatums(configElement);
        assertThat(datums.size(), is(0));
    }

    @Test
    public final void testDatumEmptyAttributes() {
        IConfigurationElement configElement = mockConfigElement(DATUM_EXTENSION,
                ImmutableMap.of(ATTRIBUTE_ID, "", ATTRIBUTE_NAME, "", ATTRIBUTE_DESCRIPTION, "", ATTRIBUTE_ICON, ""));

        ExtensionReader sut = new ExtensionReader();
        Set<DatumCategory> datums = sut.readRegistredDatums(configElement);
        assertThat(datums.size(), is(0));
    }

    @Test
    public final void testDuplicatedDatum() {
        IConfigurationElement configElement = mockConfigElement(DATUM_EXTENSION, ImmutableMap.of(ATTRIBUTE_ID,
                "some id", ATTRIBUTE_NAME, "some name", ATTRIBUTE_DESCRIPTION, "some description"));

        IConfigurationElement duplicate = mockConfigElement(DATUM_EXTENSION, ImmutableMap.of(ATTRIBUTE_ID, "some id",
                ATTRIBUTE_NAME, "some name", ATTRIBUTE_DESCRIPTION, "dome description"));

        ExtensionReader sut = new ExtensionReader();
        Set<DatumCategory> datums = sut.readRegistredDatums(configElement, duplicate);
        assertThat(datums.size(), is(1));
    }

    @Test
    public final void testPermissionsWithoutDatums() {
        IConfigurationElement configElement = mockConfigElement(PERMISSION_EXTENSION,
                ImmutableMap.of(ATTRIBUTE_DATUM_ID, "some id", ATTRIBUTE_PURPOSE, "some purpose"));

        ExtensionReader sut = new ExtensionReader();
        Set<PrincipalCategory> principals = sut.readRegistredPrincipals(configElement);

        assertThat(principals.size(), is(0));
    }

    @Test
    public final void testPermissionsExtension() {
        IConfigurationElement configElement = mockConfigElement(DATUM_EXTENSION, ImmutableMap.of(ATTRIBUTE_ID,
                "some id", ATTRIBUTE_NAME, "some name", ATTRIBUTE_DESCRIPTION, "some description", ATTRIBUTE_ICON,
                "datum.png"));

        ExtensionReader sut = new ExtensionReader();
        Set<DatumCategory> datums = sut.readRegistredDatums(configElement);
        assertThat(datums.size(), is(1));

        configElement = mockConfigElement(PERMISSION_EXTENSION,
                ImmutableMap.of(ATTRIBUTE_DATUM_ID, "some id", ATTRIBUTE_PURPOSE, "some purpose"));

        Set<PrincipalCategory> principals = sut.readRegistredPrincipals(configElement);

        assertThat(principals.size(), is(1));
        PrincipalCategory principal = getOnlyElement(principals);

        assertThat(principal.getPermissions().size(), is(1));
        PrivatePermission permission = getOnlyElement(principal.getPermissions());

        assertThat(permission.getDatumId(), is("some id"));
        assertThat(permission.getDatumName(), is("some name"));
        assertThat(permission.getPurpose(), is("some purpose"));
    }

    @Test
    public final void testMultipleUseOfPrivateDatum() {
        IConfigurationElement configElement = mockConfigElement(DATUM_EXTENSION, ImmutableMap.of(ATTRIBUTE_ID,
                "some id", ATTRIBUTE_NAME, "some name", ATTRIBUTE_DESCRIPTION, "some description", ATTRIBUTE_ICON,
                "datum.png"));

        ExtensionReader sut = new ExtensionReader();
        Set<DatumCategory> datums = sut.readRegistredDatums(configElement);
        assertThat(datums.size(), is(1));

        IConfigurationElement firstUse = mockConfigElement(PERMISSION_EXTENSION,
                ImmutableMap.of(ATTRIBUTE_DATUM_ID, "some id", ATTRIBUTE_PURPOSE, "some purpose"));

        IConfigurationElement secondUse = mockConfigElement(PERMISSION_EXTENSION,
                ImmutableMap.of(ATTRIBUTE_DATUM_ID, "some id", ATTRIBUTE_PURPOSE, "other purpose"));

        Set<PrincipalCategory> principals = sut.readRegistredPrincipals(firstUse, secondUse);
        PrincipalCategory principal = getOnlyElement(principals);
        assertThat(principal.getPermissions().size(), is(2));
    }

    @Test
    public final void testPermissionMissingAttributes() {
        IConfigurationElement configElement = mockConfigElement(DATUM_EXTENSION, ImmutableMap.of(ATTRIBUTE_ID,
                "some id", ATTRIBUTE_NAME, "some name", ATTRIBUTE_DESCRIPTION, "some description", ATTRIBUTE_ICON,
                "datum.png"));

        ExtensionReader sut = new ExtensionReader();
        Set<DatumCategory> datums = sut.readRegistredDatums(configElement);
        assertThat(datums.size(), is(1));

        configElement = mockConfigElement(PERMISSION_EXTENSION,
                ImmutableMap.of(ATTRIBUTE_DATUM_ID, "some id"));

        Set<PrincipalCategory> principals = sut.readRegistredPrincipals(configElement);
        assertThat(principals.size(), is(0));
    }
}
