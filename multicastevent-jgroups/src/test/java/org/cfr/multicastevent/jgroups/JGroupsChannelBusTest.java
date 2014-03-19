package org.cfr.multicastevent.jgroups;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import org.cfr.commons.testing.EasyMockTestCase;
import org.cfr.commons.util.collection.CollectionUtil;
import org.cfr.multicastevent.core.IMulticastPublisher;
import org.cfr.multicastevent.core.spi.IAddress;
import org.cfr.multicastevent.core.spi.IMember;
import org.cfr.multicastevent.core.spi.MulticastEvent;
import org.jgroups.Message;
import org.jgroups.stack.IpAddress;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JGroupsChannelBusTest extends EasyMockTestCase {

    private IMulticastPublisher multicastPublisher;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.multicastPublisher = mock(IMulticastPublisher.class);
        expect(multicastPublisher.getClusterName()).andReturn("test").anyTimes();
    }

    @Test
    public void testGetMembers() throws Exception {
        JGroupsChannelBus provider = new JGroupsChannelBus(multicastPublisher);

        replay();
        try {
            provider.start();

            Collection<IMember> members = provider.getMembers();
            assertNotNull(members);
            assertTrue(members.size() == 1);
            InetAddress address = CollectionUtil.first(members).getAddress();
            IAddress localAddress = provider.getLocalAddress();
            Assert.assertEquals(localAddress.getIpAddress().getHostAddress(), address.getHostAddress());
        } finally {
            provider.stop();
        }
        verify();
    }

    @Test
    public void testMemberJoined() throws UnknownHostException {
        JGroupsChannelBus provider = new JGroupsChannelBus(multicastPublisher);

        replay();

        provider.memberJoined(new IpAddress("0.0.0.0", 0));
        verify();
    }

    @Test
    public void testMemberLeft() throws UnknownHostException {
        JGroupsChannelBus provider = new JGroupsChannelBus(multicastPublisher);

        replay();

        provider.memberLeft(new IpAddress("0.0.0.0", 0));
        verify();
    }

    @Test
    public void testPublish() throws Exception {
        JGroupsChannelBus provider = new JGroupsChannelBus(multicastPublisher);

        replay();
        MulticastEvent event = new MulticastEvent();

        try {
            provider.start();

            provider.sendNotification(event);

        } finally {
            provider.stop();
        }
        verify();
    }

    @Test
    public void testReceive() throws Exception {
        JGroupsChannelBus provider = new JGroupsChannelBus(multicastPublisher);

        replay();

        MulticastEvent event = new MulticastEvent();

        try {
            provider.start();

            IpAddress sender = new IpAddress("0.0.0.0", 0);
            IpAddress receiver = new IpAddress("127.0.0.1", 0);
            event.setSender(new AddressDecorator(sender));
            Message msg = new Message();
            msg.setSrc(sender);
            msg.setDest(receiver);

            msg.setObject(event);
            provider.receive(msg);

        } finally {
            provider.stop();
        }

        verify();
    }
}
