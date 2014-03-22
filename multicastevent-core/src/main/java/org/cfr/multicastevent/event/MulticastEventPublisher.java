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
package org.cfr.multicastevent.event;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.cfr.commons.util.Assert;
import org.cfr.multicastevent.core.AbstractChannelAdapter;
import org.cfr.multicastevent.core.MemberJoinedEvent;
import org.cfr.multicastevent.core.MemberLeftEvent;
import org.cfr.multicastevent.core.MembersChangedEvent;
import org.cfr.multicastevent.core.MulticastClosingEvent;
import org.cfr.multicastevent.core.MulticastEvent;
import org.cfr.multicastevent.core.MulticastOpenEvent;
import org.cfr.multicastevent.core.spi.IChannel;
import org.cfr.multicastevent.core.spi.IMember;

import com.atlassian.event.api.EventPublisher;

/**
 * 
 * @author devacfr<christophefriederich@mac.com>
 * @since 1.0
 */
@Singleton
public class MulticastEventPublisher extends AbstractChannelAdapter {

    /**
     * this event publisher
     */
    private final EventPublisher eventPublisher;

    @Inject
    public MulticastEventPublisher(@Nonnull final EventPublisher eventPublisher, @Nonnull final IChannel channel,
            @Named("clusterName") String clusterName) {
        super(channel, clusterName);
        this.eventPublisher = Assert.notNull(eventPublisher, "eventPublisher is required");

    }

    @Override
    public void start() throws Exception {
        super.start();
        // Send event
        eventPublisher.publish(new MulticastOpenEvent(this));
    }

    @Override
    public void stop() throws Exception {
        if (!isStarted()) {
            throw new RuntimeException("Channel is already stopped or not started.");
        }
        // Send event
        eventPublisher.publish(new MulticastClosingEvent(this));
        super.stop();
    }

    @Override
    public void membersChanged(@Nonnull final Collection<IMember> members) {
        if (members == null) {
            return;
        }
        MembersChangedEvent event = new MembersChangedEvent(members, this);
        eventPublisher.publish(event);
    }

    @Override
    public void memberJoined(@Nullable final IMember member) {
        if (member == null) {
            return;
        }
        MemberJoinedEvent event = new MemberJoinedEvent(member, this);
        eventPublisher.publish(event);
    }

    @Override
    public void memberLeft(@Nullable final IMember member) {
        if (member == null) {
            return;
        }
        MemberLeftEvent event = new MemberLeftEvent(member, this);
        eventPublisher.publish(event);
    }

    @Override
    public void receiveEvent(@Nullable final MulticastEvent event) {
        if (event == null) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Receiving multicast event: "
                    + event.getClass());
        }
        event.setSource(this);
        if (logger.isDebugEnabled()) {
            logger.debug("Receiving multicast event: "
                    + event);
        }
        try {
            eventPublisher.publish(event);
        } catch (Exception ex) {
            logger.error("error publishing notification", ex);
        }
    }

}
