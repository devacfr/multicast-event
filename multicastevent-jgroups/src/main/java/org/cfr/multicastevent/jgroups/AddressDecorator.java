package org.cfr.multicastevent.jgroups;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.InetAddress;

import javax.annotation.Nonnull;

import org.cfr.commons.util.Assert;
import org.cfr.multicastevent.core.spi.IAddress;
import org.jgroups.Address;
import org.jgroups.stack.IpAddress;

/**
 * JGroups Address Adapter
 * @author acochard
 *
 */
public class AddressDecorator implements IAddress, Address {

    private Address address;

    public AddressDecorator() {
    }

    public AddressDecorator(@Nonnull final Address address) {
        this.address = Assert.notNull(address, "address is required");
    }

    @Override
    public InetAddress getIpAddress() {
        if (address instanceof IpAddress) {
            IpAddress addr = (IpAddress) address;
            return addr.getIpAddress();
        }
        return null;
    }

    @Override
    public int compareTo(Address o) {
        return address.compareTo(o);
    }

    @Override
    public boolean equals(Object obj) {
        return address.equals(obj);
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        address = (Address) in.readObject();
    }

    @Override
    public void readFrom(DataInput in) throws Exception {
        address.readFrom(in);
    }

    @Override
    public int size() {
        return address.size();
    }

    @Override
    public String toString() {
        return address.toString();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(address);
    }

    @Override
    public void writeTo(DataOutput out) throws Exception {
        address.writeTo(out);
    }
}
