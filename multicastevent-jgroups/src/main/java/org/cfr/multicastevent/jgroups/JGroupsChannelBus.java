package org.cfr.multicastevent.jgroups;

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.cfr.commons.util.Assert;
import org.cfr.commons.util.ResourceUtils;
import org.cfr.multicastevent.core.IMulticastPublisher;
import org.cfr.multicastevent.core.spi.IAddress;
import org.cfr.multicastevent.core.spi.IChannel;
import org.cfr.multicastevent.core.spi.IMember;
import org.cfr.multicastevent.core.spi.MulticastEvent;
import org.cfr.multicastevent.core.spi.impl.Member;
import org.jgroups.Address;
import org.jgroups.Event;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.PhysicalAddress;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.stack.IpAddress;
import org.jgroups.stack.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 *
 * @author acochard, devacfr
 * @since 1.0
 */
@Named
@Singleton
public class JGroupsChannelBus extends ReceiverAdapter implements IChannel {

    private static final Logger logger = LoggerFactory.getLogger(JGroupsChannelBus.class);

    private static final String resourceUdp = "classpath:udp.xml";

    private JChannel channel;

    private URI configuration;

    private boolean configurationAuto = false;

    private final IMulticastPublisher publisher;

    @Inject
    public JGroupsChannelBus(@Nonnull final IMulticastPublisher publisher) {
        this.publisher = Assert.notNull(publisher, "eventPublisher is required");
    }

    @Override
    public boolean isStarted() {
        return channel != null;
    }

    @Override
    public void start() throws Exception {

        // Check if use automatic-configuration;
        if (!configurationAuto) {
            configurationAuto = configuration == null;
        }
        URL url = null;
        if (configurationAuto) {
            url = ResourceUtils.getURL(resourceUdp);
        } else {
            url = this.configuration.toURL();
        }
        try {
            url = ResourceUtils.getURL(resourceUdp);
            // Automatic configuration, try ipv6 first
            channel = createChannel(url);
            return;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            stop();
            logger.error("Unable to open channel bus", e);
            throw e;
        }

    }

    @Override
    public void stop() throws Exception {
        if (!isStarted())
            return;
        if (channel != null) {
            this.channel.close();
        }
        this.channel = null;
    }

    protected JChannel createChannel(URL configFile) throws Exception {
        // Create channel
        JChannel channel = new JChannel(configFile);

        // Configure protocol
        for (Protocol protocol : channel.getProtocolStack().getProtocols()) {
            if (protocol instanceof GMS) {
                GMS gms = (GMS) protocol;
                gms.setPrintLocalAddress(false);
                break;
            }
        }
        // Connect and register listener
        channel.connect(this.publisher.getClusterName());
        channel.setReceiver(this);
        return channel;
    }

    @Override
    public IAddress getLocalAddress() {
        if (this.channel == null) {
            return null;
        }
        Address localAddress = this.channel.getAddress();
        if (localAddress == null)
            return null;
        IpAddress address = getPhysicalAddress(localAddress);
        if (address == null)
            return new AddressDecorator(localAddress);
        return new AddressDecorator(address);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<IMember> getMembers() {
        if (this.channel == null) {
            return Collections.emptyList();
        }
        Collection<IMember> members = Lists.newArrayList();
        List<Address> membersList = this.channel.getView().getMembers();
        List<IpAddress> addresses = Lists.newArrayList();
        for (org.jgroups.Address member : membersList) {
            addresses.add(getPhysicalAddress(member));
        }

        for (IpAddress address : addresses) {
            IMember member = new Member(address.getIpAddress());
            members.add(member);
        }
        return Collections.unmodifiableCollection(members);
    }

    public IpAddress getPhysicalAddress(Address address) {
        PhysicalAddress physicalAddr = (PhysicalAddress) channel.down(new Event(Event.GET_PHYSICAL_ADDRESS, address));
        return (IpAddress) physicalAddr;
    }

    @Override
    public void sendNotification(MulticastEvent event) throws Exception {
        event.setSender(getLocalAddress());
        Message target = new org.jgroups.Message();
        target.setObject(event);
        channel.send(target);
    }

    @Override
    public void receive(Message msg) {
        Object object = msg.getObject();
        if (object == null) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Receiving multicast event: " + object.getClass());
        }
        if (!(object instanceof MulticastEvent)) {
            logger.warn("Unknow message");
            return;
        }
        MulticastEvent event = (MulticastEvent) object;
        event.setSource(this);
        Address sender = msg.src();
        Address receiver = msg.dest();
        if (receiver == null || sender == null)
            return;
        event.setSender(new AddressDecorator(sender));
        event.setReceiver(new AddressDecorator(receiver));
        if (!sender.equals(receiver)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Receiving multicast event: " + event);
            }
            try {
                publisher.receiveEvent(event);
            } catch (Exception ex) {
                logger.error("error publishing notification", ex);
            }
        }
    }

    @Override
    public void suspect(Address address) {
        if (address instanceof IpAddress) {
            IMember member = new Member(((IpAddress) address).getIpAddress());
            publisher.memberLeft(member);
        }
    }

    public void memberJoined(Address address) {
        if (address instanceof IpAddress) {
            IMember member = new Member(((IpAddress) address).getIpAddress());
            publisher.memberJoined(member);
        }
    }

    public void memberLeft(Address address) {
        if (address instanceof IpAddress) {
            IMember member = new Member(((IpAddress) address).getIpAddress());
            publisher.memberLeft(member);
        }
    }

    @Override
    public synchronized void viewAccepted(View new_view) {
        List<Address> joined_mbrs, left_mbrs;
        Address tmp_mbr;

        if (new_view == null)
            return;
        List<Address> members = this.channel.getView().getMembers();
        List<Address> tmp = new_view.getMembers();

        synchronized (members) {
            // get new members
            joined_mbrs = Lists.newArrayList();
            for (int i = 0; i < tmp.size(); i++) {
                tmp_mbr = tmp.get(i);
                if (!members.contains(tmp_mbr))
                    joined_mbrs.add(tmp_mbr);
            }

            // get members that left
            left_mbrs = Lists.newArrayList();
            for (int i = 0; i < members.size(); i++) {
                tmp_mbr = members.get(i);
                if (!tmp.contains(tmp_mbr))
                    left_mbrs.add(tmp_mbr);
            }
        }

        if (!joined_mbrs.isEmpty())
            for (int i = 0; i < joined_mbrs.size(); i++)
                memberJoined(joined_mbrs.get(i));
        if (!left_mbrs.isEmpty())
            for (int i = 0; i < left_mbrs.size(); i++)
                memberLeft(left_mbrs.get(i));
    }

    public void setConfiguration(URI configuration) {
        this.configuration = configuration;
    }
}
