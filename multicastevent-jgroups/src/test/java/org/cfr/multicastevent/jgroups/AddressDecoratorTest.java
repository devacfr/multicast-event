package org.cfr.multicastevent.jgroups;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.cfr.commons.testing.EasyMockTestCase;
import org.easymock.EasyMock;
import org.jgroups.Address;
import org.junit.Test;

public class AddressDecoratorTest extends EasyMockTestCase {

    @Test(expected = IllegalArgumentException.class)
    public void jGroupsAddressAdapterWithNullParameter() {
        new AddressDecorator(null);
    }

    @Test
    public void jGroupsAddressAdapter() {
        Address address = mock(Address.class);
        replay();

        new AddressDecorator(address);

        verify();
    }

    @Test
    public void testCompareTo() {
        Address addressExpected = mock(Address.class);
        EasyMock.expect(addressExpected.compareTo(null)).andReturn(1);
        replay();

        AddressDecorator adapter = new AddressDecorator(addressExpected);
        assertEquals(1, adapter.compareTo(null));

        verify();
    }

    @Test
    public void testReadExternal() throws IOException, ClassNotFoundException {
        Address addressExpected = mock(Address.class);
        ObjectInput input = mock(ObjectInput.class);
        expect(input.readObject()).andReturn(addressExpected);
        replay();

        AddressDecorator adapter = new AddressDecorator(addressExpected);
        adapter.readExternal(input);

        verify();
    }

    @Test
    public void testReadFrom() throws Exception {
        Address addressExpected = mock(Address.class);
        addressExpected.readFrom(null);
        EasyMock.expectLastCall();
        replay();

        AddressDecorator adapter = new AddressDecorator(addressExpected);
        adapter.readFrom(null);

        verify();
    }

    @Test
    public void testWriteExternal() throws IOException, ClassNotFoundException {
        Address addressExpected = mock(Address.class);
        ObjectOutput output = mock(ObjectOutput.class);
        output.writeObject(addressExpected);
        EasyMock.expectLastCall();
        replay();

        AddressDecorator adapter = new AddressDecorator(addressExpected);
        adapter.writeExternal(output);

        verify();
    }

    @Test
    public void testWriteTo() throws Exception {
        Address addressExpected = mock(Address.class);
        addressExpected.writeTo((DataOutputStream) EasyMock.anyObject());
        EasyMock.expectLastCall();
        replay();

        AddressDecorator adapter = new AddressDecorator(addressExpected);
        adapter.writeTo((DataOutputStream) EasyMock.anyObject());

        verify();
    }
}
