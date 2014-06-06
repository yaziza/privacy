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

import org.eclipse.ui.IStartup;

public class Startup implements IStartup {

    @Override
    public void earlyStartup() {
        ExtensionReader extensionReader = new ExtensionReader();
        ApprovalDialogJob job = new ApprovalDialogJob(extensionReader);
        job.schedule();
    }
}
