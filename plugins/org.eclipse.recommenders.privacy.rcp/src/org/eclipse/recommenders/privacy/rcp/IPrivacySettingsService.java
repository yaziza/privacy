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
     * Tests if sending the {@link PrivateDatum} with ID {@code datumId} by the principal is allowed.
     * 
     * @param datumId
     * @param principal
     * @return returns <code>true</code> when the sending the {@link PrivateDatum} is allowed
     */
    boolean isAllowed(String datumId, String principal);

    /**
     * Allow the principal to send the {@link PrivateDatum} with ID {@code datumId}.
     * 
     * @param datumId
     * @param principal
     */
    void allow(String datumId, String principal);

    /**
     * Disallow the principal to send the {@link PrivateDatum} with ID {@code datumId}.
     * 
     * @param datumId
     * @param principal
     */
    void disallow(String datumId, String principal);
}
