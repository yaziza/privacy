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

/**
 * Class for storing/loading @link{PrivatePermission} @link{PermissionState}. The Preference String is composed from a
 * datumId{@code datumId} and a principalId{@code principalId} followed by the @link{PermissionState}. The ID's are
 * stored below a root node called "approval".
 *
 * Example:
 *
 * approval/org.eclipse.recommenders.privacy.datums.ipAddress/com.example.first=+;
 * approval/org.eclipse.recommenders.privacy.datums.ipAddress/com.example.second=-;
 *
 * The @link{Principal} with id : "com.example.first" is allowed to send the @link{PrivateDatum with id:
 * "org.eclipse.recommenders.privacy.datums.ipAddress" and "com.example.second" is not.
 *
 * @author Yasser Aziza
 *
 */
public interface IPrivacySettingsService {

    /**
     * Change the @link{PrivateDatum} @link{PermissionState} of the @link{Principal}.
     *
     * @param datumId
     * @param principalId
     * @param state
     */
    void setState(String datumId, String principalId, PermissionState state);

    /**
     * Get the @link{PrivateDatum} @link{PermissionState} of the @link{Principal}.
     *
     * @param datumId
     * @param principalId
     * @return returns the @link{PrivateDatum @link{PermissionState}
     */
    PermissionState getState(String datumId, String principalId);

    /**
     * Allow the @link{Principal} to send the @link{PrivateDatum} with ID{@code datumId}.
     *
     * @param datumId
     * @param principalId
     */
    void approve(String datumId, String principalId);

    /**
     * Disallow the @link{Principal} to send the @link{PrivateDatum} with ID{@code datumId}.
     *
     * @param datumId
     * @param principalId
     */
    void disapprove(String datumId, String principalId);

    /**
     * Tests if sending the {@link PrivateDatum} with ID {@code datumId} by the @link{Principal} is allowed.
     *
     * @param datumId
     * @param principalId
     * @return returns <code>true</code> when the sending the {@link PrivateDatum} is allowed
     */
    boolean isApproved(String datumId, String principalId);
}
