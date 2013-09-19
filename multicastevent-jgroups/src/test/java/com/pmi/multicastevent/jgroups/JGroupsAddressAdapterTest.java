package com.pmi.multicastevent.jgroups;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.cfr.commons.testing.EasyMockTestCase;
import org.easymock.EasyMock;
import org.jgroups.Address;
import org.junit.Test;

public class JGroupsAddressAdapterTest extends EasyMockTestCase {

	@Test(expected = IllegalArgumentException.class)
	public void jGroupsAddressAdapterWithNullParameter() {
		new JGroupsAddressAdapter(null);
	}

	@Test
	public void jGroupsAddressAdapter() {
		Address address = mock(Address.class);
		replay();

		new JGroupsAddressAdapter(address);

		verify();
	}

	@Test
	public void testCompareTo() {
		Address addressExpected = mock(Address.class);
		EasyMock.expect(addressExpected.compareTo(null)).andReturn(1);
		replay();

		JGroupsAddressAdapter adapter = new JGroupsAddressAdapter(addressExpected);
		assertEquals(1, adapter.compareTo(null));

		verify();
	}

	@Test
	public void testIsMulticastAddress() {
		Address addressExpected = mock(Address.class);
		EasyMock.expect(addressExpected.isMulticastAddress()).andReturn(true);
		replay();

		JGroupsAddressAdapter adapter = new JGroupsAddressAdapter(addressExpected);
		assertEquals(true, adapter.isMulticastAddress());

		verify();
	}

	@Test
	public void testReadExternal() throws IOException, ClassNotFoundException {
		reset();
		Address addressExpected = mock(Address.class);
		ObjectInput input = mock(ObjectInput.class);
		EasyMock.expect(input.readObject()).andReturn(addressExpected);
		replay();

		JGroupsAddressAdapter adapter = new JGroupsAddressAdapter(addressExpected);
		adapter.readExternal(input);

		verify();
	}

	@Test
	public void testReadFrom() throws IOException, IllegalAccessException, InstantiationException {
		Address addressExpected = mock(Address.class);
		addressExpected.readFrom(null);
		EasyMock.expectLastCall();
		replay();

		JGroupsAddressAdapter adapter = new JGroupsAddressAdapter(addressExpected);
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

		JGroupsAddressAdapter adapter = new JGroupsAddressAdapter(addressExpected);
		adapter.writeExternal(output);

		verify();
	}

	@Test
	public void testWriteTo() throws IOException, IllegalAccessException, InstantiationException {
		Address addressExpected = mock(Address.class);
		addressExpected.writeTo((DataOutputStream) EasyMock.anyObject());
		EasyMock.expectLastCall();
		replay();

		JGroupsAddressAdapter adapter = new JGroupsAddressAdapter(addressExpected);
		adapter.writeTo((DataOutputStream) EasyMock.anyObject());

		verify();
	}
}
