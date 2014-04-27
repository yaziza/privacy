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
package org.eclipse.recommenders.privacy.rcp.preferences;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.recommenders.privacy.rcp.PrivateDatum;
import org.eclipse.swt.graphics.Image;

public class DatumLabelProvider extends ColumnLabelProvider {

    @Override
    public String getText(Object obj) {
        return obj.toString();
    }

    @Override
    public Image getImage(Object obj) {
        PrivateDatum item = (PrivateDatum) obj;
        ImageDescriptor imageDescriptor = item.getIcon();
        Image image = imageDescriptor == null ? null : imageDescriptor.createImage();
        return image;
    }

    @Override
    public String getToolTipText(Object element) {
        PrivateDatum item = (PrivateDatum) element;
        return item.getDescription();
    }
}
