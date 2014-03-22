/**
 * Copyright 2014 devacfr<christophefriederich@mac.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        expect(addressExpected.compareTo(null)).andReturn(1);
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
        expectLastCall();
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
        expectLastCall();
        replay();

        AddressDecorator adapter = new AddressDecorator(addressExpected);
        adapter.writeExternal(output);

        verify();
    }

    @Test
    public void testWriteTo() throws Exception {
        Address addressExpected = mock(Address.class);
        addressExpected.writeTo((DataOutputStream) EasyMock.anyObject());
        expectLastCall();
        replay();

        AddressDecorator adapter = new AddressDecorator(addressExpected);
        adapter.writeTo((DataOutputStream) EasyMock.anyObject());

        verify();
    }
}
