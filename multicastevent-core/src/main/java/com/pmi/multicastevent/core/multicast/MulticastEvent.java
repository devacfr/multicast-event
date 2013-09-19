package com.pmi.multicastevent.core.multicast;

import org.springframework.context.ApplicationEvent;

/**
 * 
 * @author acochard [Aug 5, 2009]
 *
 */
public class MulticastEvent extends ApplicationEvent {

    /**
     * Default class serial version unique identifier.
     */
    private static final long serialVersionUID = 1L;

    private IAddress address;

    public MulticastEvent() {
        super(new Object());
    }

    public MulticastEvent(Object source) {
        super(source);
    }

    public IAddress getAddress() {
        return address;
    }

    public void setAddress(IAddress source) {
        this.address = source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

}
