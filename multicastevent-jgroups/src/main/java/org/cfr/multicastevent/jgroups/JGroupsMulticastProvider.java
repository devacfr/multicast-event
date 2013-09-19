package org.cfr.multicastevent.jgroups;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.cfr.multicastevent.core.MemberJoinedEvent;
import org.cfr.multicastevent.core.MemberLeftEvent;
import org.cfr.multicastevent.core.MulticastClosingEvent;
import org.cfr.multicastevent.core.MulticastOpenEvent;
import org.cfr.multicastevent.core.multicast.IMember;
import org.cfr.multicastevent.core.multicast.IMulticastProvider;
import org.cfr.multicastevent.core.multicast.MulticastEvent;
import org.cfr.multicastevent.core.multicast.impl.Member;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.blocks.NotificationBus;
import org.jgroups.blocks.NotificationBus.Consumer;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.stack.IpAddress;
import org.jgroups.stack.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;


/**
 *
 * @author acochard [Aug 6, 2009]
 *
 */
public class JGroupsMulticastProvider implements ApplicationListener<ApplicationEvent>, IMulticastProvider, InitializingBean,
        FactoryBean<JGroupsMulticastProvider>, ApplicationEventPublisherAware, Consumer {

    private static final Logger logger = LoggerFactory.getLogger(JGroupsMulticastProvider.class);

    private static final String resourceIpv4 = "jgroups-ipv4.xml";

    private static final String resourceIpv6 = "jgroups-ipv6.xml";

    private ApplicationEventPublisher applicationEventPublisher;

    private String clusterName;

    private JChannel channel;

    private NotificationBus notificationBus;

    private Resource configuration;

    private String configurationString;

    private boolean configurationAuto = false;

    private boolean startOnContext = true;

    private boolean stopOnContext = true;

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(applicationEventPublisher, "applicationEventPublisher is required");
        Assert.hasText(clusterName, "clusterName is required");
        if (configuration != null) {
            configurationString = configuration.getURL().toString();
        }
    }

    @Override
    public JGroupsMulticastProvider getObject() throws Exception {
        return this;
    }

    @Override
    public Class<?> getObjectType() {
        return this.getClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void start() throws Exception {
        if (isStarted())
            throw new RuntimeException("JGroup is already started.");
        this.open();
    }

    @Override
    public boolean isStarted() {
        return channel != null;
    }

    @Override
    public void stop() throws Exception {
        if (!isStarted())
            throw new RuntimeException("JGroup is already stopped or not started.");
        this.close();
    }

    private void close() {
        logger.info("Closing channel '" + clusterName + "'");
        // Send event
        applicationEventPublisher.publishEvent(new MulticastClosingEvent(this));
        // Close
        channel.close();
        channel = null;
    }

    private void connect() throws Exception {
        // Create channel
        channel = new JChannel(configurationString);

        // Configure protocol
        for (Protocol protocol : channel.getProtocolStack().getProtocols()) {
            if (protocol instanceof GMS) {
                GMS gms = (GMS) protocol;
                gms.setPrintLocalAddress(false);
                break;
            }
        }
        // Connect and register listener
        channel.connect(clusterName);
        notificationBus = new NotificationBus(channel, channel.getClusterName());
        notificationBus.setConsumer(this);

        // Send event
        applicationEventPublisher.publishEvent(new MulticastOpenEvent(this));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable getCache() {
        // Unused
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<IMember> getMembers() {
        Collection<IMember> members = new ArrayList<IMember>();
        for (Address address : channel.getView().getMembers()) {
            if (address instanceof IpAddress) {
                IMember member = new Member(((IpAddress) address).getIpAddress());
                members.add(member);
            }
        }
        return Collections.unmodifiableCollection(members);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleNotification(Serializable notification) {
        if (notification == null) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Receiving multicast event: " + notification.getClass());
        }
        MulticastEvent event = (MulticastEvent) notification;
        event.setSource(this);
        if (!event.getAddress().toString().equals(channel.getLocalAddress().toString())) {
            if (logger.isDebugEnabled()) {
                logger.debug("Receiving multicast event: " + event);
            }
            try {
                applicationEventPublisher.publishEvent(event);
            } catch (Exception ex) {
                logger.error("error publishing notification", ex);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void memberJoined(Address address) {
        if (address instanceof IpAddress) {
            if (logger.isDebugEnabled()) {
                logger.debug("Member joined: " + address);
            }
            IMember member = new Member(((IpAddress) address).getIpAddress());
            MemberJoinedEvent event = new MemberJoinedEvent(member, this);
            applicationEventPublisher.publishEvent(event);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void memberLeft(Address address) {
        if (address instanceof IpAddress) {
            if (logger.isDebugEnabled()) {
                logger.debug("Member left: " + address);
            }
            IMember member = new Member(((IpAddress) address).getIpAddress());
            MemberLeftEvent event = new MemberLeftEvent(member, this);
            applicationEventPublisher.publishEvent(event);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        if (event instanceof ContextStartedEvent || event instanceof ContextRefreshedEvent) {
            if (this.isStartOnContext()) {
                if (channel == null) {
                    open();
                }
            }
        } else if (event instanceof ContextClosedEvent) {
            if (this.isStopOnContext()) {
                if (channel != null) {
                    close();
                }
            }
        }

    }

    private void open() {
        logger.info("Opening channel '" + clusterName + "'");

        // Check if use automatic-configuration;
        if (!configurationAuto) {
            configurationAuto = configurationString == null;
        }

        if (configurationAuto) {
            try {
                // Automatic configuration, try ipv6 first
                configurationString = resourceIpv6;
                connect();
                return;
            } catch (Exception e) {
                channel.close();
                logger.trace("Unable to open channel with ipv6", e);
                logger.debug("Unable to connect with ipv6, trying ipv4...");
                configurationString = resourceIpv4;
            }
        }

        // Connecting
        try {
            connect();
        } catch (Exception e) {
            logger.error("Unable to open channel '" + clusterName + "'", e);
            channel.close();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(MulticastEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Publishing multicast event: " + event);
        }
        event.setAddress(new JGroupsAddressAdapter(channel.getLocalAddress()));
        notificationBus.sendNotification(event);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isStartOnContext() {
        return startOnContext;
    }

    /**
     * Allow to start this provider on starting of Spring Application Context
     * @param startOnContext
     */
    public void setStartOnContext(boolean startOnContext) {
        this.startOnContext = startOnContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStopOnContext() {
        return this.stopOnContext;
    }

    /**
     * Allow to stop this provider on stopping of Spring Application Context
     * @param stopOnContext
     */
    public void setStopOnContext(boolean stopOnContext) {
        this.stopOnContext = stopOnContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public void setConfiguration(Resource configuration) {
        this.configuration = configuration;
    }
}
