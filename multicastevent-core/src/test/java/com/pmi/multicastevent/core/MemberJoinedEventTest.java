package com.pmi.multicastevent.core;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

import com.pmi.multicastevent.core.multicast.IMember;

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
