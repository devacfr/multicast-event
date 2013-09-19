package org.cfr.multicastevent.core.multicast.impl;

import java.net.InetAddress;

import org.cfr.multicastevent.core.multicast.IMember;


/**
 * 
 * @author acochard [Aug 5, 2009]
 *
 */
public class Member implements IMember {

    private InetAddress address;

    public Member(InetAddress address) {
        super();
        this.address = address;
    }

    public InetAddress getAddress() {
        return address;
    }

}
