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

public class DatumRegistryTest {
    private static final String DATUM = "datum";
    private static final String ATTRIBUTE_ID = "id";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_DESCRIPTION = "description";
    private static final String ATTRIBUTE_ICON = "icon";

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
        DatumRegistry sut = new DatumRegistry();
        Set<PrivateDatum> datums = sut.readRegistredDatums(null);
        assertThat(datums.size(), is(0));
        datums = sut.readRegistredDatums(new IConfigurationElement[] {});
        assertThat(datums.size(), is(0));
    }

    @Test
    public final void testExtensionPointWithoutIcon() {
        IConfigurationElement configElement = mockConfigElement(DATUM, ImmutableMap.of(ATTRIBUTE_ID, "some id",
                ATTRIBUTE_NAME, "some name", ATTRIBUTE_DESCRIPTION, "some description"));

        DatumRegistry sut = new DatumRegistry();
        Set<PrivateDatum> datums = sut.readRegistredDatums(configElement);

        PrivateDatum datum = getOnlyElement(datums);
        assertThat(datum.getId(), is("some id"));
        assertThat(datum.getName(), is("some name"));
        assertThat(datum.getDescription(), is("some description"));

        ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin("org.example.privacy",
                ISharedImages.IMG_OBJ_ELEMENT);
        assertThat(datum.getIcon(), is(imageDescriptor));
    }

    @Test
    public final void testExtensionPointIcon() {
        IConfigurationElement configElement = mockConfigElement(DATUM, ImmutableMap.of(ATTRIBUTE_ID, "some id",
                ATTRIBUTE_NAME, "some name", ATTRIBUTE_DESCRIPTION, "some description", ATTRIBUTE_ICON, "datum.png"));

        DatumRegistry sut = new DatumRegistry();
        Set<PrivateDatum> datums = sut.readRegistredDatums(configElement);

        assertThat(datums.size(), is(1));
        PrivateDatum datum = datums.iterator().next();
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

        DatumRegistry sut = new DatumRegistry();
        Set<PrivateDatum> datums = sut.readRegistredDatums(configElement);

        assertThat(datums.size(), is(0));
    }

    @Test
    public final void testMissingAttributes() {
        IConfigurationElement configElement = mockConfigElement(DATUM, ImmutableMap.of(ATTRIBUTE_ID, "some id"));

        DatumRegistry sut = new DatumRegistry();
        Set<PrivateDatum> datums = sut.readRegistredDatums(configElement);
        assertThat(datums.size(), is(0));
    }

    @Test
    public final void testEmptyAttributes() {
        IConfigurationElement configElement = mockConfigElement(DATUM,
                ImmutableMap.of(ATTRIBUTE_ID, "", ATTRIBUTE_NAME, "", ATTRIBUTE_DESCRIPTION, "", ATTRIBUTE_ICON, ""));

        DatumRegistry sut = new DatumRegistry();
        Set<PrivateDatum> datums = sut.readRegistredDatums(configElement);
        assertThat(datums.size(), is(0));
    }

    @Test
    public final void testDuplicatedDatum() {
        IConfigurationElement configElement = mockConfigElement(DATUM, ImmutableMap.of(ATTRIBUTE_ID, "some id",
                ATTRIBUTE_NAME, "some name", ATTRIBUTE_DESCRIPTION, "some description"));

        IConfigurationElement duplicate = mockConfigElement(DATUM, ImmutableMap.of(ATTRIBUTE_ID, "some id",
                ATTRIBUTE_NAME, "some name", ATTRIBUTE_DESCRIPTION, "dome description"));

        DatumRegistry sut = new DatumRegistry();
        Set<PrivateDatum> datums = sut.readRegistredDatums(configElement, duplicate);
        assertThat(datums.size(), is(1));
    }
}
