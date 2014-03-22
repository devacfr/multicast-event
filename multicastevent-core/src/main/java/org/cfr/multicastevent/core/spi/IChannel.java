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
import javax.annotation.Nullable;

import org.cfr.multicastevent.core.MulticastEvent;

/**
 * This class represents the channel of communication (Unicast, Multicast or Broadcast).
 * @author devacfr<christophefriederich@mac.com>
 * @since 1.0
 * 
 */
public interface IChannel {

    /**
     * Attaches the chanel to a end point delegator.
     * @param endPoint end point delegator.
     */
    void attachChanel(IEndPointDelegator endPoint);

    /**
     * Gets indicating whether the channel is started ; the channel is opened.
     * @return Returns <code>true</code> whether the channel is started, otherwise <code>false</code>.
     */
    boolean isStarted();

    /**
     * Disconnects the channel.
     * @throws Exception If the channel can not be stop.
     */
    void stop() throws Exception;

    /**
     * Create and connects the channel to the cluster.
     * @throws Exception If the channel can not be create.
     */
    void start() throws Exception;

    /**
     * connects the channel to the cluster.
     * @throws Exception If the channel can not be connected.
     */
    void connect() throws Exception;

    /**
     * Disconnects and destroys the channel and its associated resources.
     * @throws Exception If the channel can not be disconnected.
     */
    void close() throws Exception;

    /**
     * Sends a message. The message contains
     * <ol>
     * <li>a receiver address {@link IAddress}. A <code>null</code> address sends the message to all
     * group members.
     * <li>a sender address. Can be left empty as it will be assigned automatically.
     * </ol>
     * @param event the message to send.
     * @throws Exception If the message can not be send.
     */
    void sendNotification(@Nullable MulticastEvent event) throws Exception;

    /**
     * Gets the member list of cluster.
     * @return  Returns an unmodifiable member list of cluster.
     */
    @Nonnull
    Collection<IMember> getMembers();

    /**
     * Gets the local address of this node.
     * @return Returns the local address of this node.
     */
    @Nonnull
    IAddress getLocalAddress();

}
