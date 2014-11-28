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

import org.eclipse.recommenders.internal.privacy.example.rcp.l10n.Messages;

public enum HeartbeatInterval {
    ONCE(0, Messages.HEARTBEAT_INTERVAL_ONCE),
    HOURLY(TimeUnit.HOURS.toMillis(1), Messages.HEARTBEAT_INTERVAL_HOURLY),
    DAILY(TimeUnit.DAYS.toMillis(1), Messages.HEARTBEAT_INTERVAL_DAILY);

    private final long delay;
    private final String description;

    HeartbeatInterval(long delay, String description) {
        this.delay = delay;
        this.description = description;
    }

    public long getDelay() {
        return delay;
    }

    @Override
    public String toString() {
        return description;
    }
}
