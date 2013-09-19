package com.pmi.multicastevent.core;

import org.easymock.EasyMock;
import org.junit.Test;

import com.pmi.multicastevent.core.multicast.IMember;

public class MemberLeftEventTest {

    @Test
    public void testMemberLeftEvent() {
        new MemberLeftEvent(EasyMock.createMock(IMember.class), new Object());
    }

}
