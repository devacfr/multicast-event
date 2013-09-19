package com.pmi.multicastevent.core;

import com.pmi.multicastevent.core.multicast.IMember;

public class MemberJoinedEvent extends MemberEvent {

    /**
     * Default class serial version unique identifier.
     */
    private static final long serialVersionUID = 1L;

    public MemberJoinedEvent(IMember member, Object source) {
        super(member, source);
    }

}
