package org.cfr.multicastevent.core;

import junit.framework.Assert;

import org.cfr.multicastevent.core.MemberJoinedEvent;
import org.cfr.multicastevent.core.spi.IMember;
import org.easymock.EasyMock;
import org.junit.Test;


public class MemberJoinedEventTest {

    @Test
    public void testGetMember() {
        IMember member = EasyMock.createMock(IMember.class);
        MemberJoinedEvent event = new MemberJoinedEvent(member, new Object());
        Assert.assertEquals(member, event.getMember());
    }

    @Test
    public void testMemberJoinedEvent() {
        new MemberJoinedEvent(EasyMock.createMock(IMember.class), new Object());
    }

}
