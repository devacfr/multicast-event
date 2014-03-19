package org.cfr.multicastevent.core;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.cfr.commons.util.Assert;
import org.cfr.multicastevent.core.spi.IChannel;
import org.cfr.multicastevent.core.spi.IMember;
import org.cfr.multicastevent.core.spi.MulticastEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.event.api.EventPublisher;

/**
 * 
 * @author cfriedri
 *
 */
@Named(IMulticastPublisher.MULTICAST_EVENT_PUBLISHER_NAME)
@Singleton
public class MulticastPublisher implements IMulticastPublisher {

    private static final Logger logger = LoggerFactory.getLogger(MulticastPublisher.class);

    private final String clusterName;

    private final EventPublisher eventPublisher;

    private final IChannel channel;

    @Inject
    public MulticastPublisher(@Nonnull final EventPublisher eventPublisher, @Nonnull final IChannel channel,
            @Named("clusterName") String clusterName) {
        this.eventPublisher = Assert.notNull(eventPublisher, "eventPublisher is required");
        this.clusterName = Assert.hasText(clusterName, "cluster is required");
        this.channel = Assert.notNull(channel, "chanel is required");
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

        // Send event
        eventPublisher.publish(new MulticastOpenEvent(this));
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
        // Send event
        eventPublisher.publish(new MulticastClosingEvent(this));
        // Close
        channel.stop();
    }

    @Override
    public void sendNotification(MulticastEvent event) throws Exception {
        if (event == null)
            return;
        if (event.getSource() == this)
            return;
        MulticastEvent multicastEvent = event;
        this.channel.sendNotification(multicastEvent);
    }

    @Override
    public void memberJoined(@Nullable IMember member) {
        if (member == null)
            return;
        MemberJoinedEvent event = new MemberJoinedEvent(member, this);
        eventPublisher.publish(event);
    }

    @Override
    public void memberLeft(@Nullable IMember member) {
        if (member == null)
            return;
        MemberLeftEvent event = new MemberLeftEvent(member, this);
        eventPublisher.publish(event);
    }

    @Override
    public void receiveEvent(@Nullable MulticastEvent event) {
        if (event == null) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Receiving multicast event: " + event.getClass());
        }
        event.setSource(this);
        if (logger.isDebugEnabled()) {
            logger.debug("Receiving multicast event: " + event);
        }
        try {
            eventPublisher.publish(event);
        } catch (Exception ex) {
            logger.error("error publishing notification", ex);
        }
    }

}
