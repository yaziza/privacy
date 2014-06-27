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
     * Changes the @link{PrivateDatum} @link{PermissionState} of the @link{Principal}.
     *
     */
    void setState(String principalId, String datumId, PermissionState state);

    /**
     * Gets the @link{PrivateDatum} @link{PermissionState} of the @link{Principal}.
     *
     * @return returns the @link{PrivateDatum @link{PermissionState}
     */
    PermissionState getState(String principalId, String datumId);

    /**
     * Allows the @link{Principal} to send the @link{PrivateDatum} with ID{@code datumId}.
     *
     */
    void approve(String principalId, String datumId);

    /**
     * Disallows the @link{Principal} to send the @link{PrivateDatum} with ID{@code datumId}.
     *
     */
    void disapprove(String principalId, String datumId);

    /**
     * Tests if all data access requested by the principals with ID{@code principalId} has been allowed.
     *
     * @returns <code>true</code> if all data access are allowed.
     */
    boolean isAllApproved(String principalId);

    /**
     * Tests if sending the {@link PrivateDatum} with ID {@code datumId} by the @link{Principal} is allowed.
     *
     * @return returns <code>true</code> when the sending the {@link PrivateDatum} is allowed
     */
    boolean isApproved(String principalId, String... datumsIds);

    /**
     * Tests if sending the {@link PrivateDatum} with ID {@code datumId} is never approved for all @link{Principal}.
     *
     * @return returns <code>true</code> when the sending the {@link PrivateDatum} is never approved.
     */
    boolean isNeverApproved(String datumId);

    /**
     * Generates and store a User ID into a file.
     */
    void generateUserId();

    /**
     * Retrieves the User ID from a file
     *
     * @return: @link{UUID} the User ID
     */
    UUID getUserId();
}
