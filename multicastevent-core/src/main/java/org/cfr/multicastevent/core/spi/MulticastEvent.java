package org.cfr.multicastevent.core.spi;

import java.util.EventObject;

/**
 * 
 * @author acochard [Aug 5, 2009]
 *
 */
public class MulticastEvent extends EventObject {

    /**
     * Default class serial version unique identifier.
     */
    private static final long serialVersionUID = 1L;

    private IAddress sender;

    private IAddress receiver;

    public MulticastEvent() {
        super(new Object());
    }

    public MulticastEvent(Object source) {
        super(source);
    }

    public IAddress sender() {
        return sender;
    }

    public IAddress receiver() {
        return receiver;
    }

    public void setSender(IAddress sender) {
        this.sender = sender;
    }

    public void setReceiver(IAddress receiver) {
        this.receiver = receiver;
    }

    public void setSource(Object source) {
        this.source = source;
    }

}
