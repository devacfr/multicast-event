package org.cfr.multicastevent.core;

import org.cfr.multicastevent.core.spi.IMember;

public class MemberLeftEvent extends MemberEvent {

    /**
     * 
     */
    private static final long serialVersionUID = 2495814092984480631L;

    public MemberLeftEvent(IMember member, Object source) {
        super(member, source);
    }

}
