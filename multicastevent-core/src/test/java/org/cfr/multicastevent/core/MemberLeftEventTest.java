package org.cfr.multicastevent.core;

import org.cfr.multicastevent.core.MemberLeftEvent;
import org.cfr.multicastevent.core.spi.IMember;
import org.easymock.EasyMock;
import org.junit.Test;


public class MemberLeftEventTest {

    @Test
    public void testMemberLeftEvent() {
        new MemberLeftEvent(EasyMock.createMock(IMember.class), new Object());
    }

}
