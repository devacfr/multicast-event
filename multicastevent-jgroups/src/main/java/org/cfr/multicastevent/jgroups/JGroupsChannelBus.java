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
package org.cfr.multicastevent.jgroups;

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PreDestroy;
import javax.inject.Named;
import javax.inject.Singleton;

import org.cfr.commons.util.ResourceUtils;
import org.cfr.commons.util.collection.CollectionUtil;
import org.cfr.multicastevent.core.MulticastEvent;
import org.cfr.multicastevent.core.spi.IAddress;
import org.cfr.multicastevent.core.spi.IChannel;
import org.cfr.multicastevent.core.spi.IEndPointDelegator;
import org.cfr.multicastevent.core.spi.IMember;
import org.cfr.multicastevent.core.spi.impl.Member;
import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.Event;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.PhysicalAddress;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.stack.IpAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * a JGroups implementation of spi interface {@link IChannel}.
 * @author acochard
 * @author devacfr
 * @since 1.0
 */
@Named
@Singleton
public class JGroupsChannelBus extends ReceiverAdapter implements IChannel {

    /**
     * static log instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JGroupsChannelBus.class);

    /**
     * default jgroups file configurations.
     */
    private static final String DEFAULT_CONFIGURATION = "classpath:udp.xml";

    /**
     * jgroups channel instance.
     */
    private Channel channel;

    /**
     * uri on current jgroups configuration file.
     */
    private URI configuration;

    /**
     * indicating whether using default configuration.
     */
    private boolean configurationAuto = false;

    /**
     * end point delegate.
     */
    private IEndPointDelegator endPoint;

    /**
     * store the last members list. allows find joined and left member.
     */
    private List<Address> members;

    /**
     * create new instance.
     */
    public JGroupsChannelBus() {
    }

    /**
     * 
     * @param endPointDelegator
     */
    public JGroupsChannelBus(@Nullable final IEndPointDelegator endPointDelegator) {
        this.endPoint = endPointDelegator;
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
            url = ResourceUtils.getURL(DEFAULT_CONFIGURATION);
        } else {
            url = this.configuration.toURL();
        }
        try {
            channel = createChannel(url);
            // Connect
            channel.connect(getClusterName());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            stop();
            LOGGER.error("Unable to open channel bus", e);
            throw e;
        }
    }

    /**
     * 
     * @throws Exception
     */
    @Override
    public void connect() throws Exception {
        if (!isStarted()) {
            start();
        } else {
            // Connect
            channel.connect(getClusterName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreDestroy
    public void close() throws Exception {
        if (!isStarted()) {
            return;
        }
        channel.close();
    }

    @Override
    public void stop() throws Exception {
        if (!isStarted()) {
            return;
        }
        if (channel != null) {
            this.channel.disconnect();
        }
        this.channel = null;
    }

    /**
     * Gets the cluster name
     * @return Returns a string representing the cluster name.
     */
    @Nonnull
    public String getClusterName() {
        return this.endPoint == null ? "cluster" : this.endPoint.getClusterName();
    }

    /**
     * 
     * @param configFile
     * @return
     * @throws Exception
     */
    protected JChannel createChannel(URL configFile) throws Exception {
        // Create channel
        JChannel channel = new JChannel(configFile);
        channel.setReceiver(this);
        return channel;
    }

    /**
     * @param endPoint the endPoint to set
     */
    @Override
    public void attachChanel(final IEndPointDelegator endPoint) {
        this.endPoint = endPoint;
    }

    @Override
    public IAddress getLocalAddress() {
        if (this.channel == null) {
            throw new RuntimeException("The channel is not started");
        }
        Address localAddress = this.channel.getAddress();
        if (localAddress == null) {
            throw new RuntimeException("The channel is not started");
        }
        IpAddress address = getPhysicalAddress(localAddress);
        if (address == null) {
            return new AddressDecorator(localAddress);
        }
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
        return CollectionUtil.transform(this.channel.getView().getMembers(), new ToMember());
    }

    /**
     * Give the {@link IpAddress} physical address corresponding of the <code>address</code> parameter.
     * @param address the reference address
     * @return Returns the {@link IpAddress} physical address corresponding of the <code>address</code> parameter.
     */
    public IpAddress getPhysicalAddress(final Address address) {
        PhysicalAddress physicalAddr = (PhysicalAddress) channel.down(new Event(Event.GET_PHYSICAL_ADDRESS, address));
        return (IpAddress) physicalAddr;
    }

    @Override
    public void sendNotification(final MulticastEvent event) throws Exception {
        event.setSender(getLocalAddress());
        Message target = new org.jgroups.Message();
        target.setObject(event);
        if (event.receiver() != null
                && event.receiver() != null) {
            target.setDest((Address) event.receiver());
        }
        channel.send(target);
    }

    @Override
    public void receive(final Message msg) {
        Object object = msg.getObject();
        if (object == null) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Receiving multicast event: "
                    + object.getClass());
        }
        if (!(object instanceof MulticastEvent)) {
            LOGGER.warn("Unknow message");
            return;
        }
        MulticastEvent event = (MulticastEvent) object;
        event.setSource(this);
        Address sender = msg.src();
        Address receiver = msg.dest();
        if (receiver == null
                || sender == null) {
            return;
        }
        event.setSender(new AddressDecorator(sender));
        event.setReceiver(new AddressDecorator(receiver));
        if (!sender.equals(receiver)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Receiving multicast event: "
                        + event);
            }
            try {
                if (endPoint != null) {
                    endPoint.receiveEvent(event);
                }
            } catch (Exception ex) {
                LOGGER.error("error publishing notification", ex);
            }
        }
    }

    @Override
    public void suspect(final Address address) {
        if (address instanceof IpAddress) {
            IMember member = new Member(((IpAddress) address).getIpAddress());
            endPoint.memberLeft(member);
        }
    }

    /**
     * 
     * @param member
     */
    public void memberChanged(final Collection<IMember> member) {
        if (endPoint != null) {
            endPoint.membersChanged(member);
        }
    }

    /**
     * 
     * @param member
     */
    public void memberJoined(final IMember member) {
        if (endPoint != null) {
            endPoint.memberJoined(member);
        }
    }

    /**
     * 
     * @param member
     */
    public void memberLeft(final IMember member) {
        if (endPoint != null) {
            endPoint.memberLeft(member);
        }
    }

    @Override
    public synchronized void viewAccepted(final View newView) {
        List<Address> joinedMenbers, leftMembers;
        Address tmpMember;

        if (newView == null) {
            return;
        }
        if (members == null) {
            members = this.channel.getView().getMembers();
        }

        List<Address> tmp = newView.getMembers();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("#viewAccepted( newView: "
                    + tmp.toString() + ')');
            LOGGER.debug("#viewAccepted( channelView: "
                    + members.toString() + ')');
        }
        memberChanged(CollectionUtil.transform(tmp, new ToMember()));

        synchronized (members) {
            // get new members
            joinedMenbers = Lists.newArrayList();
            for (int i = 0; i < tmp.size(); i++) {
                tmpMember = tmp.get(i);
                if (!members.contains(tmpMember)) {
                    joinedMenbers.add(tmpMember);
                }
            }

            // get members that left
            leftMembers = Lists.newArrayList();
            for (int i = 0; i < members.size(); i++) {
                tmpMember = members.get(i);
                if (!tmp.contains(tmpMember)) {
                    leftMembers.add(tmpMember);
                }
            }
        }
        members = this.channel.getView().getMembers();

        if (!joinedMenbers.isEmpty()) {
            for (int i = 0; i < joinedMenbers.size(); i++) {
                memberJoined(getPysicalMember(joinedMenbers.get(i)));
            }
        }
        if (!leftMembers.isEmpty()) {
            for (int i = 0; i < leftMembers.size(); i++) {
                memberLeft(getPysicalMember(leftMembers.get(i)));
            }
        }
    }

    public void setConfiguration(@Nonnull URI configuration) {
        this.configuration = configuration;
    }

    @Nonnull
    protected IMember getPysicalMember(@Nonnull Address member) {
        return new Member(getPhysicalAddress(member).getIpAddress());
    }

    public class ToMember implements Function<Address, IMember> {

        @Override
        @Nullable
        public IMember apply(@Nullable Address input) {
            if (input == null) {
                return null;
            }
            return getPysicalMember(input);
        }
    }
}
