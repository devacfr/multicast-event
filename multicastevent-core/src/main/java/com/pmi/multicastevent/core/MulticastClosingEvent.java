package com.pmi.multicastevent.core;

import org.springframework.context.ApplicationEvent;

public class MulticastClosingEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    public MulticastClosingEvent(Object source) {
        super(source);
    }

}
