package org.cfr.multicastevent.core;

import org.cfr.multicastevent.core.multicast.IMember;

public class MemberLeftEvent extends MemberEvent {

    /**
     * Default class serial version unique identifier.
     */
    private static final long serialVersionUID = 1L;

    public MemberLeftEvent(IMember member, Object source) {
        super(member, source);
    }

}
