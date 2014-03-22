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
package org.cfr.multicastevent.core.spi;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.cfr.multicastevent.core.MulticastEvent;

/**
 * This is a protocol interface between the api and specific spi implementation.
 * @author devacfr<christophefriederich@mac.com>
 * @since 1.0
 */
public interface IEndPointDelegator {

    /**
     * Gets the name of current cluster.
     * @return Returns a string representing the name of cluster.
     */
    @Nonnull
    String getClusterName();

    /**
     * Allows to forward a received event to the event publisher.
     * @param event event to forward.
     * @see org.cfr.multicastevent.event.MulticastEventPublisher
     */
    void receiveEvent(@Nonnull MulticastEvent event);

    /**
     * Allows to forward event indicating that member left the cluster to the event publisher.
     * @param member the member has left.
     */
    void memberLeft(@Nonnull IMember member);

    /**
     * Allows to forward event indicating that member joined the cluster to the event publisher.
     * @param member the member has joined.
     */
    void memberJoined(@Nonnull IMember member);

    /**
     * Allows to forward event indicating the list of members in the cluster has changed
     * to the event publisher.
     * @param members the new list of members.
     */
    void membersChanged(@Nonnull Collection<IMember> members);

}
