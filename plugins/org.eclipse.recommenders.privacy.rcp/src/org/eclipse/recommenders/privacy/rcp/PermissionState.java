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

public enum PermissionState {

    /**
     * The user has explicitly approved the permission in question.
     * A private datum may be send to the principal that requested the permission.
     */
    APPROVED,

    /**
     * The user has explicitly disapproved the permission in question.
     * A private datum may <em>not</em> be send to the principal that requested the permission.
     */
    DISAPPROVED,

    /**
     * The user has not yet approved or disapproved the permission in question.
     * A private datum may <em>not</em> be send to the principal that requested the permission unless an explicit approval
     * has been given.
     */
    UNKNOWN;
}
