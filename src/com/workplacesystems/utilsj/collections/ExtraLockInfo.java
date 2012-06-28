/*
 * Copyright 2010 Workplace Systems PLC (http://www.workplacesystems.com/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.workplacesystems.utilsj.collections;

import java.lang.management.LockInfo;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Dave
 */
public class ExtraLockInfo {

    public enum WaitingFor {
        NONE,
        WRITE,
        READ;
    }
    private WaitingFor waitingFor = WaitingFor.NONE;
    private LockInfo waitingForLock = null;
    private final Map<String,LockInfo> holdingWrites = new HashMap<String,LockInfo>();
    private final Map<String,LockInfo> holdingReads = new HashMap<String,LockInfo>();

    void addHoldingWrite(LockInfo lockInfo) {
        this.holdingWrites.put(lockInfo.toString(), lockInfo);
    }

    void addHoldingRead(LockInfo lockInfo) {
        this.holdingReads.put(lockInfo.toString(), lockInfo);
    }

    void setWaiting(WaitingFor waitingFor, LockInfo lockInfo) {
        if (this.waitingFor != WaitingFor.NONE)
            throw new IllegalStateException("waitingFor already set");
        this.waitingFor = waitingFor;
        this.waitingForLock = lockInfo;
    }

    public boolean isWaitingFor(WaitingFor waitingFor, LockInfo waitingForLock) {
        return this.waitingFor == waitingFor && this.waitingForLock != null && this.waitingForLock.toString().equals(waitingForLock.toString());
    }

    public boolean hasHeldLocks() {
        return !holdingReads.isEmpty() || !holdingWrites.isEmpty();
    }

    public boolean heldWritesContains(LockInfo lockInfo) {
        return holdingWrites.containsKey(lockInfo.toString());
    }

    public boolean heldReadsContains(LockInfo lockInfo) {
        return holdingReads.containsKey(lockInfo.toString());
    }

    public Collection<LockInfo> getHeldWriteLocks() {
        return Collections.unmodifiableCollection(holdingWrites.values());
    }

    public Collection<LockInfo> getHeldReadLocks() {
        return Collections.unmodifiableCollection(holdingReads.values());
    }
}
