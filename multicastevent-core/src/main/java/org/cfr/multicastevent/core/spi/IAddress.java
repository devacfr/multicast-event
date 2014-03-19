package org.cfr.multicastevent.core.spi;

import java.net.InetAddress;

public interface IAddress {

    public InetAddress getIpAddress();

    /**
     * Gets serialized size of this address
     * @return Returns serialized size of this address
     */
    int size();
}
