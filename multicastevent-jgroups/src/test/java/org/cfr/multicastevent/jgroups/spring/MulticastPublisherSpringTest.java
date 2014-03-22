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
package org.cfr.multicastevent.jgroups.spring;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import org.cfr.commons.testing.EasyMockTestCase;
import org.cfr.multicastevent.core.IChanelAdapter;
import org.cfr.multicastevent.core.spi.IMember;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author devacfr<christophefriederich@mac.com>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
public class MulticastPublisherSpringTest extends EasyMockTestCase {

    @Inject
    @Named("MasterChannelAdapter")
    private IChanelAdapter chanelAdapterMaster;

    @Inject
    @Named("SlaveChannelAdapter")
    private IChanelAdapter chanelAdapterSlave;

    @Inject
    private SimpleEventListener simpleEventListener;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        simpleEventListener.reset();
    }

    @Test
    public void simpleNodeConnectionAndDeconnection() throws Exception {
        Collection<IMember> members = chanelAdapterMaster.getMembers();
        assertNotNull(members);
        assertEquals(1, members.size());

        try {
            chanelAdapterSlave.connect();
            Thread.sleep(500);
            members = chanelAdapterMaster.getMembers();
            assertEquals(2, members.size());
            // listener test
            checkExpectedCountListener(1, 1, 0, 0);
        } finally {
            chanelAdapterSlave.close();
        }
        checkExpectedCountListener(2, 1, 1, 0);
    }

    private void checkExpectedCountListener(int changed, int joined, int left, int receive) {
        assertEquals(changed, simpleEventListener.changed);
        assertEquals(joined, simpleEventListener.joined);
        assertEquals(left, simpleEventListener.left);
        assertEquals(receive, simpleEventListener.receive);
    }
}
