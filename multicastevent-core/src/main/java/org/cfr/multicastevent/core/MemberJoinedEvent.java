package org.cfr.multicastevent.core;

import org.cfr.multicastevent.core.spi.IMember;

public class MemberJoinedEvent extends MemberEvent {

    /**
     * 
     */
    private static final long serialVersionUID = -7367310918096120322L;

    public MemberJoinedEvent(IMember member, Object source) {
        super(member, source);
    }

}
