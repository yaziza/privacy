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

import java.util.UUID;

/**
 * Class for storing/loading of anonymous ID's.The anonymous ID is a randomly generated identifier assigned to a user
 * login.
 *
 * You can obtain an instance of the {@link IAnonymousIdService} from the Eclipse context:
 *
 * <pre>
 * IEclipseContext context = EclipseContextFactory.getServiceContext(bundleContext);
 * IAnonymousIdService service = context.get(IAnonymousIdService.class);
 * </pre>
 */
public interface IAnonymousIdService {

    /**
     * Generates and persists an anonymous ID.
     */
    void generateAnonymousId();

    /**
     * Retrieves the persistent anonymous ID.
     *
     * The anonymous ID is purely random. It only serves to uniquely identify a user; no additional information about
     * the user (e.g., IP address or local information) is leaked.
     *
     * @return the anonymous ID. Never {@code null}.
     */
    UUID getAnonymousId();
}
