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
package org.eclipse.recommenders.internal.privacy.example.rcp.dialogs;

import java.util.concurrent.TimeUnit;

public enum HeartbeatInterval {
    ONCE(0),
    HOURLY(TimeUnit.HOURS.toMillis(1)),
    DAILY(TimeUnit.DAYS.toMillis(1));

    private final long delay;

    HeartbeatInterval(long delay) {
        this.delay = delay;
    }

    public long getDelay() {
        return delay;
    }
}
