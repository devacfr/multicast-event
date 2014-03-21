package org.cfr.multicastevent.jgroups;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import org.cfr.commons.testing.EasyMockTestCase;
import org.cfr.commons.util.collection.CollectionUtil;
import org.cfr.multicastevent.core.MulticastEvent;
import org.cfr.multicastevent.core.spi.IAddress;
import org.cfr.multicastevent.core.spi.IEndPointDelegator;
import org.cfr.multicastevent.core.spi.IMember;
import org.cfr.multicastevent.core.spi.impl.Member;
import org.easymock.internal.LastControl;
import org.jgroups.Message;
import org.jgroups.stack.IpAddress;
import org.junit.Assert;
import org.junit.Test;

public class JGroupsChannelBusTest extends EasyMockTestCase {

    private IEndPointDelegator multicastPublisher;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // TODO [devacfr] remove when upgrade commons-testing 1.0.2+
        LastControl.pullMatchers();
        this.multicastPublisher = mock(IEndPointDelegator.class);
        expect(multicastPublisher.getClusterName()).andReturn("test").anyTimes();
    }

    @Test
    public void testGetMembers() throws Exception {

        replay();

        JGroupsChannelBus provider = new JGroupsChannelBus(multicastPublisher);

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
        replay();

        JGroupsChannelBus provider = new JGroupsChannelBus(multicastPublisher);

        provider.memberJoined(new Member(InetAddress.getLocalHost()));
        verify();
    }

    @Test
    public void testMemberLeft() throws UnknownHostException {
        replay();

        JGroupsChannelBus provider = new JGroupsChannelBus(multicastPublisher);

        provider.memberLeft(new Member(InetAddress.getLocalHost()));
        verify();
    }

    @Test
    public void testPublish() throws Exception {
        replay();

        JGroupsChannelBus provider = new JGroupsChannelBus(multicastPublisher);
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
        replay();

        JGroupsChannelBus provider = new JGroupsChannelBus(multicastPublisher);

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
