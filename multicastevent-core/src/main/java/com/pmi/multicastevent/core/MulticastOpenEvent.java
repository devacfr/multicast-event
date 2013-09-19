package com.pmi.multicastevent.core;

import org.springframework.context.ApplicationEvent;

public class MulticastOpenEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    public MulticastOpenEvent(Object source) {
        super(source);
    }

}
