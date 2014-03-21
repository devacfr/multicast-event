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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.cfr.commons.util.Assert;
import org.cfr.multicastevent.core.spi.IChannel;
import org.cfr.multicastevent.core.spi.IEndPointDelegator;
import org.cfr.multicastevent.core.spi.IMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author cfriedri
 *
 */
public abstract class AbstractChannelAdapter implements IChanelAdapter, IEndPointDelegator,
        Provider<IEndPointDelegator> {

    /**
     * log instance
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * the cluster name
     */
    private final String clusterName;

    /**
     * the endPoint channel
     */
    private final IChannel channel;

    @Inject
    public AbstractChannelAdapter(@Nonnull final IChannel channel, @Named("clusterName") String clusterName) {
        this.clusterName = Assert.hasText(clusterName, "cluster is required");
        this.channel = Assert.notNull(channel, "chanel is required");
    }

    @Override
    public IEndPointDelegator get() {
        return this;
    }

    @Override
    public String getClusterName() {
        return clusterName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<IMember> getMembers() {
        return channel.getMembers();
    }

    @Override
    public void start() throws Exception {
        if (isStarted())
            throw new RuntimeException("Chanel is already started.");
        // Open channel
        logger.info("Opening channel '" + clusterName + "'");
        try {
            channel.start();
        } catch (Exception e) {
            logger.error("Unable to open channel '" + clusterName + "'", e);
            channel.stop();
        }
    }

    @Override
    public boolean isStarted() {
        return channel.isStarted();
    }

    @Override
    public void stop() throws Exception {
        if (!isStarted())
            throw new RuntimeException("Channel is already stopped or not started.");
        logger.info("Closing channel '" + clusterName + "'");
        // Close
        channel.stop();
    }

    @Override
    public abstract void memberJoined(@Nullable IMember member);

    @Override
    public abstract void memberLeft(@Nullable IMember member);

    @Override
    public abstract void receiveEvent(@Nullable MulticastEvent event);

    @Override
    public void sendNotification(MulticastEvent event) throws Exception {
        if (event == null)
            return;
        if (event.getSource() == this)
            return;
        MulticastEvent multicastEvent = event;
        this.channel.sendNotification(multicastEvent);
    }

}
