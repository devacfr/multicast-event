package org.cfr.multicastevent.core;

import org.cfr.multicastevent.core.multicast.IMember;
import org.springframework.context.ApplicationEvent;


public abstract class MemberEvent extends ApplicationEvent {

    /**
     * Default class serial version unique identifier.
     */
    private static final long serialVersionUID = 1L;

    private IMember member;

    public MemberEvent(IMember member, Object source) {
        super(source);
        this.member = member;
    }

    public IMember getMember() {
        return member;
    }
}
