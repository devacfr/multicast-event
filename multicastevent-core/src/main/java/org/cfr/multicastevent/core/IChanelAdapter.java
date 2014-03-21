/**
 * Copyright 2014 devacfr<christophefriederich@mac.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cfr.multicastevent.core;

import java.util.Collection;

import org.cfr.multicastevent.core.spi.IMember;

public interface IChanelAdapter {

    /**
     * 
     * @return
     */
    String getClusterName();

    Collection<IMember> getMembers();

    /**
     * Start the provider
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * stop the provider
     * @throws Exception
     */
    void stop() throws Exception;

    /**
     * 
     * @return
     */
    boolean isStarted();

    /**
     * Publish an event to the multicast network.
     * @param event the event
     * @throws Exception 
     */
    void sendNotification(MulticastEvent event) throws Exception;

}