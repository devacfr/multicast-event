package com.pmi.multicastevent.core.multicast;

import java.util.Collection;

import org.springframework.context.ApplicationListener;

public interface IMulticast extends ApplicationListener<MulticastEvent> {

    Collection<IMember> getMembers();

    void setProvider(IMulticastProvider provider);
}