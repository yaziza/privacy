/**
 * Copyright (c) 2015 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Yasser Aziza - initial implementation
 */
package org.eclipse.recommenders.privacy.rcp;

public final class DatumIds {

    /**
     * A randomly generated identifier assigned to the user's login.
     */
    public static final String ANONYMOUS_ID = "org.eclipse.recommenders.privacy.rcp.datums.anonymousId"; //$NON-NLS-1$

    /**
     * The Java version as given by the java.version or java.specification.version system properties.
     */
    public static final String JAVA_VERSION = "org.eclipse.recommenders.privacy.rcp.datums.javaVersion"; //$NON-NLS-1$

    /**
     * An IPv4 address assigned to this host. An IPv4 address may disclose (approximate) information about your
     * location.
     */
    public static final String INET4_ADDRESS = "org.eclipse.recommenders.privacy.rcp.datums.inet4Address"; //$NON-NLS-1$

    /**
     * An IPv6 address assigned to this host. An IPv6 address may disclose (approximate) information about your
     * location. It may furthermore disclose information identifying your network interface's make and manufacturer.
     */
    public static final String INET6_ADDRESS = "org.eclipse.recommenders.privacy.rcp.datums.inet6Address"; //$NON-NLS-1$

    /**
     * A user's e-mail address.
     */
    public static final String EMAIL_ADDRESS = "org.eclipse.recommenders.privacy.rcp.datums.email"; //$NON-NLS-1$

    private DatumIds() {
        // Not meant to be instantiated
    }
}
