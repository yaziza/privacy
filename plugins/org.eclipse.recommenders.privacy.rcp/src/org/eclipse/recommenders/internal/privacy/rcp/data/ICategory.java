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

import java.util.Set;

import org.eclipse.swt.graphics.Image;

public interface ICategory {

    Set<PrivatePermission> getPermissions();

    String getText();

    Image getImageDescriptor();

    String getTooltip();
}
