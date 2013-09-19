package org.cfr.multicastevent.core.multicast.impl;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;

import org.cfr.commons.testing.EasyMockTestCase;
import org.cfr.multicastevent.core.multicast.IMember;
import org.cfr.multicastevent.core.multicast.IMulticastProvider;
import org.cfr.multicastevent.core.multicast.MulticastEvent;
import org.cfr.multicastevent.core.multicast.impl.Multicast;
import org.easymock.EasyMock;
import org.junit.Test;


public class MulticastTest extends EasyMockTestCase {

	@Test
	public void testAfterPropertiesSet() throws Exception {
		Multicast multicast = new Multicast();
		IMulticastProvider publisher = mock(IMulticastProvider.class);
		multicast.setProvider(publisher);
		multicast.afterPropertiesSet();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAfterPropertiesSetException() throws Exception {
		new Multicast().afterPropertiesSet();
	}

	@Test
	public void testGetMembers() {
		Collection<IMember> members = new ArrayList<IMember>();
		IMulticastProvider provider = mock(IMulticastProvider.class);
		EasyMock.expect(provider.getMembers()).andReturn(members);
		replay();

		Multicast multicast = new Multicast();
		multicast.setProvider(provider);

		Assert.assertEquals(members, multicast.getMembers());
		verify();
	}

	@Test
	public void testOnApplicationEvent() {
		IMulticastProvider remoteProvider = mock(IMulticastProvider.class);
		MulticastEvent event = new MulticastEvent(remoteProvider);

		IMulticastProvider provider = mock(IMulticastProvider.class);
		provider.publish(event);
		replay();

		Multicast multicast = new Multicast();
		multicast.setProvider(provider);

		multicast.onApplicationEvent(event);
		verify();
	}

	@Test
	public void testOnApplicationEventWithNullEvent() {

		IMulticastProvider provider = mock(IMulticastProvider.class);
		replay();

		Multicast multicast = new Multicast();
		multicast.setProvider(provider);

		multicast.onApplicationEvent(null);
		verify();
	}

	@Test
	public void testOnApplicationEventWithOnSameProvider() {
		IMulticastProvider provider = mock(IMulticastProvider.class);
		MulticastEvent event = new MulticastEvent(provider);
		replay();

		Multicast multicast = new Multicast();
		multicast.setProvider(provider);

		multicast.onApplicationEvent(event);
		verify();
	}
}
