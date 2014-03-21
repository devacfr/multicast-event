package org.cfr.multicastevent.event;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;

import org.cfr.commons.testing.EasyMockTestCase;
import org.cfr.multicastevent.core.AbstractChannelAdapter;
import org.cfr.multicastevent.core.MulticastEvent;
import org.cfr.multicastevent.core.spi.IChannel;
import org.cfr.multicastevent.core.spi.IMember;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.event.api.EventPublisher;

public class MulticastPublisherTest extends EasyMockTestCase {

    private EventPublisher eventPublisher;

    private IChannel channel;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        eventPublisher = mock(EventPublisher.class);
        channel = mock(IChannel.class);
    }

    @Test
    public void defaultConstructor() throws Exception {
        new MulticastEventPublisher(eventPublisher, channel, "name");
    }

    @Test
    public void testGetMembers() {
        Collection<IMember> members = new ArrayList<IMember>();
        EasyMock.expect(channel.getMembers()).andReturn(members);
        replay();

        MulticastEventPublisher multicast = new MulticastEventPublisher(eventPublisher, channel, "name");

        Assert.assertEquals(members, multicast.getMembers());
        verify();
    }

    @Test
    public void testOnApplicationEvent() throws Exception {
        MulticastEventPublisher multicast = new MulticastEventPublisher(eventPublisher, channel, "name");

        MulticastEvent event = new MulticastEvent(new Object());
        channel.sendNotification(event);
        expectLastCall();
        replay();

        multicast.sendNotification(event);
        verify();
    }

    @Test
    public void testOnApplicationEventWithNullEvent() throws Exception {

        replay();

        MulticastEventPublisher multicast = new MulticastEventPublisher(eventPublisher, channel, "name");

        multicast.sendNotification(null);
        verify();
    }

    @Test
    public void testOnApplicationEventWithOnSameProvider() throws Exception {
        AbstractChannelAdapter multicast = new MulticastEventPublisher(eventPublisher, channel, "name");
        MulticastEvent event = new MulticastEvent(multicast);
        replay();

        multicast.sendNotification(event);
        verify();
    }
}
