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

import org.eclipse.swt.widgets.Shell;

public interface IAdvancedPreferencesDialogFactory {

    /**
     * Opens a Dialog which display advanced preferences.
     */
    void open(Shell shell);

}
