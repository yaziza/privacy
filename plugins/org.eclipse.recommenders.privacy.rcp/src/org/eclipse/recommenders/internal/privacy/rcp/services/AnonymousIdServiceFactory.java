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
package org.eclipse.recommenders.internal.privacy.rcp.services;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

public class AnonymousIdServiceFactory extends ContextFunction {

    private AnonymousIdService anonymousIdService;

    @Override
    public synchronized Object compute(IEclipseContext context) {
        if (anonymousIdService == null) {
            anonymousIdService = ContextInjectionFactory.make(AnonymousIdService.class, context);
        }
        return anonymousIdService;
    }
}
