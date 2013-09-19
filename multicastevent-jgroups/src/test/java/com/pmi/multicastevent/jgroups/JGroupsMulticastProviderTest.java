package com.pmi.multicastevent.jgroups;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.cfr.commons.testing.EasyMockTestCase;
import org.easymock.EasyMock;
import org.jgroups.stack.IpAddress;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import com.pmi.multicastevent.core.multicast.IMember;
import com.pmi.multicastevent.core.multicast.MulticastEvent;

public class JGroupsMulticastProviderTest extends EasyMockTestCase {

	@Test
	public void testAfterPropertiesSet() throws Exception {
		JGroupsMulticastProvider provider = new JGroupsMulticastProvider();

		try {
			provider.afterPropertiesSet();
			Assert.fail();
		} catch (IllegalArgumentException e) {
			// Expected
		}

		provider.setClusterName("test");

		try {
			provider.afterPropertiesSet();
			Assert.fail();
		} catch (IllegalArgumentException e) {
			// Expected
		}

		ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);
		ApplicationContext applicationContext = mock(ApplicationContext.class);

		provider.setApplicationEventPublisher(applicationEventPublisher);

		provider.afterPropertiesSet();
		provider.onApplicationEvent(new ContextClosedEvent(applicationContext));

	}

	@Test
	public void testGetMembers() throws Exception {
		JGroupsMulticastProvider provider = new JGroupsMulticastProvider();
		provider.setClusterName("test");

		ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);
		provider.setApplicationEventPublisher(applicationEventPublisher);
		ApplicationContext applicationContext = mock(ApplicationContext.class);

		provider.afterPropertiesSet();
		// Open
		provider.onApplicationEvent(new ContextRefreshedEvent(applicationContext));

		for (IMember member : provider.getMembers()) {
			Assert.assertEquals(InetAddress.getLocalHost().getHostAddress(), member.getAddress().getHostAddress());
		}

		// Close
		provider.onApplicationEvent(new ContextClosedEvent(applicationContext));

	}

	@Test
	public void testMemberJoined() throws UnknownHostException {
		JGroupsMulticastProvider provider = new JGroupsMulticastProvider();

		ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);
		applicationEventPublisher.publishEvent((ApplicationEvent) EasyMock.anyObject());
		replay();

		provider.setApplicationEventPublisher(applicationEventPublisher);

		provider.memberJoined(new IpAddress("0.0.0.0", 0));
		verify();
	}

	@Test
	public void testMemberLeft() throws UnknownHostException {
		JGroupsMulticastProvider provider = new JGroupsMulticastProvider();

		ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);
		applicationEventPublisher.publishEvent((ApplicationEvent) EasyMock.anyObject());
		replay();

		provider.setApplicationEventPublisher(applicationEventPublisher);

		provider.memberLeft(new IpAddress("0.0.0.0", 0));
		verify();
	}

	@Test
	public void testPublish() throws Exception {
		JGroupsMulticastProvider provider = new JGroupsMulticastProvider();
		provider.setClusterName("test");

		ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);
		provider.setApplicationEventPublisher(applicationEventPublisher);

		ApplicationContext applicationContext = mock(ApplicationContext.class);
		MulticastEvent event = new MulticastEvent();

		provider.afterPropertiesSet();
		// Open
		provider.onApplicationEvent(new ContextRefreshedEvent(applicationContext));

		provider.publish(event);

		// Close
		provider.onApplicationEvent(new ContextClosedEvent(applicationContext));

	}

	@Test
	public void testReceive() throws Exception {
		JGroupsMulticastProvider provider = new JGroupsMulticastProvider();
		provider.setClusterName("test");

		ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);
		applicationEventPublisher.publishEvent((ApplicationEvent) EasyMock.anyObject());
		applicationEventPublisher.publishEvent((ApplicationEvent) EasyMock.anyObject());
		applicationEventPublisher.publishEvent((ApplicationEvent) EasyMock.anyObject());
		ApplicationContext applicationContext = mock(ApplicationContext.class);

		replay();

		provider.setApplicationEventPublisher(applicationEventPublisher);

		MulticastEvent event = new MulticastEvent();

		provider.afterPropertiesSet();
		// Open
		provider.onApplicationEvent(new ContextRefreshedEvent(applicationContext));

		event.setAddress(new JGroupsAddressAdapter(new IpAddress("0.0.0.0", 0)));

		provider.handleNotification(event);

		// Close
		provider.onApplicationEvent(new ContextClosedEvent(applicationContext));

		verify();
	}

}
