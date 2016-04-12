/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.carbondata.core.locks;

import org.carbondata.core.constants.CarbonCommonConstants;
import org.carbondata.core.datastorage.store.impl.FileFactory;
import org.carbondata.core.datastorage.store.impl.FileFactory.FileType;
import org.carbondata.core.util.CarbonProperties;

/**
 * @author Ravikiran
 *         This class is a Lock factory class which is used to provide lock objects.
 *         Using this lock object client can request the lock and unlock.
 */
public class CarbonLockFactory {

    /**
     * isZookeeperEnabled to check if zookeeper feature is enabled or not for carbon.
     */
    private static boolean isZookeeperEnabled;

    static {
        CarbonLockFactory.updateZooKeeperLockingStatus();
    }

    /**
     * This method will determine the lock type.
     *
     * @param Location
     * @param lockType
     * @return
     */
    public static ICarbonLock getCarbonLockObj(String location, LockType lockType) {
        if (FileFactory.getFileType(location) == FileType.LOCAL) {
            return new LocalFileLock(location, lockType);
        } else if (isZookeeperEnabled) {

            return new ZooKeeperLocking(lockType);
        } else {
            return new HdfsFileLock(location, lockType);
        }

    }

    /**
     * This method will set the zookeeper status whether zookeeper to be used for locking or not.
     */
    private static void updateZooKeeperLockingStatus() {
        isZookeeperEnabled = CarbonCommonConstants.ZOOKEEPER_ENABLE_DEFAULT;
        if (CarbonProperties.getInstance().getProperty(CarbonCommonConstants.ZOOKEEPER_ENABLE_LOCK)
                .equalsIgnoreCase("false")) {
            isZookeeperEnabled = false;
        } else if (CarbonProperties.getInstance()
                .getProperty(CarbonCommonConstants.ZOOKEEPER_ENABLE_LOCK)
                .equalsIgnoreCase("true")) {
            isZookeeperEnabled = true;
        }

    }

}
