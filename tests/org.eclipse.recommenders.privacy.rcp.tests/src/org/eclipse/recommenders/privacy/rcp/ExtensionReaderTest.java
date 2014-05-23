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

    private static final String DATUM_ELEMENT = "datum";
    private static final String DATUM_ID_ATTRIBUTE = "datumId";

    private static final String PRINCIPAL_ELEMENT = "principal";
    private static final String PRINCIPAL_ID_ATTRIBUTE = "principalId";

    private static final String PERMISSION_ELEMENT = "permission";

    private static final String ID_ATTRIBUTE = "id";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String DESCRIPTION_ATTRIBUTE = "description";
    private static final String ICON_ATTRIBUTE = "icon";
    private static final String PURPOSE_ATTRIBUTE = "purpose";
    private static final String POLICY_URI_ATTRIBUTE = "policyUri";

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
        sut.readRegisteredDatums(null);
        Set<DatumCategory> datums = sut.getDatumCategory();
        assertThat(datums.size(), is(0));
        sut.readRegisteredDatums(new IConfigurationElement[] {});
        assertThat(datums.size(), is(0));

        sut.readRegisteredPrincipals(null);
        Set<PrincipalCategory> plugins = sut.getPrincipalCategory();
        assertThat(plugins.size(), is(0));
        sut.readRegisteredPrincipals(new IConfigurationElement[] {});
        plugins = sut.getPrincipalCategory();
        assertThat(plugins.size(), is(0));
    }

    @Test
    public final void testDatumExtensionWithoutIcon() {
        IConfigurationElement configElement = mockConfigElement(DATUM_ELEMENT, ImmutableMap.of(ID_ATTRIBUTE, "some id",
                NAME_ATTRIBUTE, "some name", DESCRIPTION_ATTRIBUTE, "some description"));

        ExtensionReader sut = new ExtensionReader();
        sut.readRegisteredDatums(configElement);
        Set<DatumCategory> datums = sut.getDatumCategory();

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
        IConfigurationElement configElement = mockConfigElement(DATUM_ELEMENT, ImmutableMap.of(ID_ATTRIBUTE, "some id",
                NAME_ATTRIBUTE, "some name", DESCRIPTION_ATTRIBUTE, "some description", ICON_ATTRIBUTE, "datum.png"));

        ExtensionReader sut = new ExtensionReader();
        sut.readRegisteredDatums(configElement);
        Set<DatumCategory> datums = sut.getDatumCategory();

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
                ImmutableMap.of(ID_ATTRIBUTE, "some id", NAME_ATTRIBUTE, "some name"));

        ExtensionReader sut = new ExtensionReader();
        sut.readRegisteredDatums(configElement);
        Set<DatumCategory> datums = sut.getDatumCategory();
        assertThat(datums.size(), is(0));

        sut.readRegisteredPrincipals(configElement);
        Set<PrincipalCategory> plugins = sut.getPrincipalCategory();
        assertThat(plugins.size(), is(0));
    }

    @Test
    public final void testDatumExtensionMissingAttributes() {
        IConfigurationElement configElement = mockConfigElement(DATUM_ELEMENT, ImmutableMap.of(ID_ATTRIBUTE, "some id"));

        ExtensionReader sut = new ExtensionReader();
        sut.readRegisteredDatums(configElement);
        Set<DatumCategory> datums = sut.getDatumCategory();
        assertThat(datums.size(), is(0));
    }

    @Test
    public final void testDatumEmptyAttributes() {
        IConfigurationElement configElement = mockConfigElement(DATUM_ELEMENT,
                ImmutableMap.of(ID_ATTRIBUTE, "", NAME_ATTRIBUTE, "", DESCRIPTION_ATTRIBUTE, "", ICON_ATTRIBUTE, ""));

        ExtensionReader sut = new ExtensionReader();
        sut.readRegisteredDatums(configElement);
        Set<DatumCategory> datums = sut.getDatumCategory();
        assertThat(datums.size(), is(0));
    }

    @Test
    public final void testDuplicatedDatum() {
        IConfigurationElement configElement = mockConfigElement(DATUM_ELEMENT, ImmutableMap.of(ID_ATTRIBUTE, "some id",
                NAME_ATTRIBUTE, "some name", DESCRIPTION_ATTRIBUTE, "some description"));

        IConfigurationElement duplicate = mockConfigElement(DATUM_ELEMENT, ImmutableMap.of(ID_ATTRIBUTE, "some id",
                NAME_ATTRIBUTE, "some name", DESCRIPTION_ATTRIBUTE, "dome description"));

        ExtensionReader sut = new ExtensionReader();
        sut.readRegisteredDatums(configElement, duplicate);
        Set<DatumCategory> datums = sut.getDatumCategory();
        assertThat(datums.size(), is(1));
    }

    @Test
    public final void testPermissionsWithoutDatums() {
        IConfigurationElement configElement = mockConfigElement(PRINCIPAL_ELEMENT,
                ImmutableMap.of(ID_ATTRIBUTE, "principal id", NAME_ATTRIBUTE, "principal name"));

        ExtensionReader sut = new ExtensionReader();
        sut.readRegisteredPrincipals(configElement);
        Set<PrincipalCategory> principals = sut.getPrincipalCategory();

        assertThat(principals.size(), is(1));
    }

    @Test
    public final void testPermissionExtension() {
        IConfigurationElement configElement = mockConfigElement(DATUM_ELEMENT, ImmutableMap.of(ID_ATTRIBUTE, "some id",
                NAME_ATTRIBUTE, "some name", DESCRIPTION_ATTRIBUTE, "some description", ICON_ATTRIBUTE, "datum.png"));

        ExtensionReader sut = new ExtensionReader();
        sut.readRegisteredDatums(configElement);
        Set<DatumCategory> datums = sut.getDatumCategory();
        assertThat(datums.size(), is(1));

        configElement = mockConfigElement(PRINCIPAL_ELEMENT, ImmutableMap.of(ID_ATTRIBUTE, "principal id",
                NAME_ATTRIBUTE, "principal name", ICON_ATTRIBUTE, "principal.png"));

        sut.readRegisteredPrincipals(configElement);

        configElement = mockConfigElement(PERMISSION_ELEMENT, ImmutableMap.of(DATUM_ID_ATTRIBUTE, "some id",
                PRINCIPAL_ID_ATTRIBUTE, "principal id", PURPOSE_ATTRIBUTE, "some purpose", POLICY_URI_ATTRIBUTE,
                "some policy"));

        sut.readRegisteredPermissions(configElement);
        Set<PrincipalCategory> plugins = sut.getPrincipalCategory();

        assertThat(plugins.size(), is(1));
        PrincipalCategory principal = getOnlyElement(plugins);

        assertThat(principal.getPermissions().size(), is(1));
        PrivatePermission permission = getOnlyElement(principal.getPermissions());

        assertThat(permission.getDatumId(), is("some id"));
        assertThat(permission.getDatumName(), is("some name"));
        assertThat(permission.getPurpose(), is("some purpose"));
    }

    @Test
    public final void testMultipleUseOfPrivateDatum() {
        IConfigurationElement configElement = mockConfigElement(DATUM_ELEMENT, ImmutableMap.of(ID_ATTRIBUTE, "some id",
                NAME_ATTRIBUTE, "some name", DESCRIPTION_ATTRIBUTE, "some description", ICON_ATTRIBUTE, "datum.png"));

        ExtensionReader sut = new ExtensionReader();
        sut.readRegisteredDatums(configElement);
        Set<DatumCategory> datums = sut.getDatumCategory();
        assertThat(datums.size(), is(1));

        configElement = mockConfigElement(PRINCIPAL_ELEMENT, ImmutableMap.of(ID_ATTRIBUTE, "principal id",
                NAME_ATTRIBUTE, "principal name", ICON_ATTRIBUTE, "principal.png"));

        sut.readRegisteredPrincipals(configElement);

        IConfigurationElement firstUse = mockConfigElement(PERMISSION_ELEMENT, ImmutableMap.of(DATUM_ID_ATTRIBUTE,
                "some id", PRINCIPAL_ID_ATTRIBUTE, "principal id", PURPOSE_ATTRIBUTE, "some purpose",
                POLICY_URI_ATTRIBUTE, "some policy"));

        IConfigurationElement secondUse = mockConfigElement(PERMISSION_ELEMENT, ImmutableMap.of(DATUM_ID_ATTRIBUTE,
                "some id", PRINCIPAL_ID_ATTRIBUTE, "principal id", PURPOSE_ATTRIBUTE, "other purpose",
                POLICY_URI_ATTRIBUTE, "other policy"));

        sut.readRegisteredPermissions(firstUse, secondUse);
        Set<PrincipalCategory> principals = sut.getPrincipalCategory();
        PrincipalCategory principal = getOnlyElement(principals);
        assertThat(principal.getPermissions().size(), is(2));
    }

    @Test
    public final void testMultiplePrincipale() {
        IConfigurationElement configElement = mockConfigElement(DATUM_ELEMENT, ImmutableMap.of(ID_ATTRIBUTE, "some id",
                NAME_ATTRIBUTE, "some name", DESCRIPTION_ATTRIBUTE, "some description", ICON_ATTRIBUTE, "datum.png"));

        ExtensionReader sut = new ExtensionReader();
        sut.readRegisteredDatums(configElement);
        Set<DatumCategory> datums = sut.getDatumCategory();
        assertThat(datums.size(), is(1));

        IConfigurationElement firstPrincipale = mockConfigElement(PRINCIPAL_ELEMENT, ImmutableMap.of(ID_ATTRIBUTE,
                "principal id", NAME_ATTRIBUTE, "principal name", ICON_ATTRIBUTE, "principal.png"));

        IConfigurationElement secondPrincipale = mockConfigElement(PRINCIPAL_ELEMENT,
                ImmutableMap.of(ID_ATTRIBUTE, "other id", NAME_ATTRIBUTE, "other name", ICON_ATTRIBUTE, "other.png"));

        sut.readRegisteredPrincipals(firstPrincipale, secondPrincipale);

        IConfigurationElement firstUse = mockConfigElement(PERMISSION_ELEMENT, ImmutableMap.of(DATUM_ID_ATTRIBUTE,
                "some id", PRINCIPAL_ID_ATTRIBUTE, "principal id", PURPOSE_ATTRIBUTE, "some purpose",
                POLICY_URI_ATTRIBUTE, "some policy"));

        IConfigurationElement secondUse = mockConfigElement(PERMISSION_ELEMENT, ImmutableMap.of(DATUM_ID_ATTRIBUTE,
                "some id", PRINCIPAL_ID_ATTRIBUTE, "other id", PURPOSE_ATTRIBUTE, "other purpose",
                POLICY_URI_ATTRIBUTE, "other policy"));

        sut.readRegisteredPermissions(firstUse, secondUse);
        Set<PrincipalCategory> principals = sut.getPrincipalCategory();
        assertThat(principals.size(), is(2));

        PrincipalCategory principal = principals.iterator().next();
        assertThat(principal.getPermissions().size(), is(1));
        PrivatePermission permission = getOnlyElement(principal.getPermissions());
        assertThat(permission.getDatumId(), is("some id"));
    }

    @Test
    public final void testPermissionMissingAttributes() {
        IConfigurationElement configElement = mockConfigElement(DATUM_ELEMENT, ImmutableMap.of(ID_ATTRIBUTE, "some id",
                NAME_ATTRIBUTE, "some name", DESCRIPTION_ATTRIBUTE, "some description", ICON_ATTRIBUTE, "datum.png"));

        ExtensionReader sut = new ExtensionReader();
        sut.readRegisteredDatums(configElement);
        Set<DatumCategory> datums = sut.getDatumCategory();
        assertThat(datums.size(), is(1));

        configElement = mockConfigElement(PRINCIPAL_ELEMENT, ImmutableMap.of(ID_ATTRIBUTE, "principal id",
                NAME_ATTRIBUTE, "principal name", ICON_ATTRIBUTE, "principal.png"));

        sut.readRegisteredPrincipals(configElement);

        configElement = mockConfigElement(PERMISSION_ELEMENT, ImmutableMap.of(DATUM_ID_ATTRIBUTE, "some id"));

        sut.readRegisteredPermissions(configElement);
        Set<PrincipalCategory> principals = sut.getPrincipalCategory();
        assertThat(principals.size(), is(1));

        PrincipalCategory principal = getOnlyElement(principals);
        assertThat(principal.getPermissions().size(), is(0));
    }
}
