package org.cfr.multicastevent.core;

import java.util.EventObject;

import org.cfr.multicastevent.core.spi.IMember;

public abstract class MemberEvent extends EventObject {

    /**
     * 
     */
    private static final long serialVersionUID = -3438936632485077928L;

    private IMember member;

    public MemberEvent(IMember member, Object source) {
        super(source);
        this.member = member;
    }

    public IMember getMember() {
        return member;
    }
}
