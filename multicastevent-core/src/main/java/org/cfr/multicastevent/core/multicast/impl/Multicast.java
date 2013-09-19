package org.cfr.multicastevent.core.multicast.impl;

import java.util.Collection;

import org.cfr.multicastevent.core.multicast.IMember;
import org.cfr.multicastevent.core.multicast.IMulticast;
import org.cfr.multicastevent.core.multicast.IMulticastProvider;
import org.cfr.multicastevent.core.multicast.MulticastEvent;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;


public class Multicast implements InitializingBean, IMulticast, FactoryBean<Multicast> {

    private IMulticastProvider provider;

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(provider, "provider is required");
    }

    @Override
    public Multicast getObject() throws Exception {
        return this;
    }

    @Override
    public Class<?> getObjectType() {
        return this.getClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<IMember> getMembers() {
        return provider.getMembers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onApplicationEvent(MulticastEvent event) {
        if (event == null)
            return;
        if (event.getSource() == provider)
            return;
        MulticastEvent multicastEvent = event;
        provider.publish(multicastEvent);
    }

    public void setProvider(IMulticastProvider publisher) {
        this.provider = publisher;
    }
}