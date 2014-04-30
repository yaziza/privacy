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

public interface IPrivacySettingsService {

    /**
     * Test if the {@link PrivateDatum} with the given id is allowed.
     * 
     * @param datumId
     * @return returns <code>true</code> when the {@link PrivateDatum} is allowed
     */
    boolean isAllowed(String datumId);

    /**
     * Allow the {@link PrivateDatum} with the given id.
     * 
     * @param datumId
     */
    void allow(String datumId);

    /**
     * Disallow the {@link PrivateDatum} with the given id.
     * 
     * @param datumId
     */
    void disallow(String datumId);
}
