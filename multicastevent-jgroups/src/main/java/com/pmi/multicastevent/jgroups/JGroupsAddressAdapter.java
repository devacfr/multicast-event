package com.pmi.multicastevent.jgroups;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.jgroups.Address;
import org.springframework.util.Assert;

import com.pmi.multicastevent.core.multicast.IAddress;

/**
 * JGroups Address Adapter
 * @author acochard
 *
 */
public class JGroupsAddressAdapter implements IAddress, Address {

    Address address;

    public JGroupsAddressAdapter() {
    }

    public JGroupsAddressAdapter(Address address) {
        Assert.notNull(address, "address is required");
        this.address = address;
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
    public boolean isMulticastAddress() {
        return address.isMulticastAddress();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        address = (Address) in.readObject();
    }

    @Override
    public void readFrom(DataInputStream in) throws IOException, IllegalAccessException, InstantiationException {
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
    public void writeTo(DataOutputStream out) throws IOException {
        address.writeTo(out);
    }
}
