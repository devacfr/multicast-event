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

import javax.inject.Singleton;

import org.cfr.multicastevent.core.MemberJoinedEvent;
import org.cfr.multicastevent.core.MemberLeftEvent;
import org.cfr.multicastevent.core.MembersChangedEvent;
import org.cfr.multicastevent.core.MulticastEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.event.api.EventListener;

/**
 * @author devacfr<christophefriederich@mac.com>
 *
 */
@Singleton
public class SimpleEventListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public int receive;

    public int joined;

    public int left;

    public int changed;

    public void reset() {
        receive = 0;
        joined = 0;
        left = 0;
        changed = 0;
    }

    @EventListener
    public void onReceiveEvent(MulticastEvent event) {
        logger.debug("Raise event listener Listener");
        receive++;
    }

    @EventListener
    public void onMemberJoined(MemberJoinedEvent event) {
        logger.debug("Raise Members joined Listener");
        joined++;
    }

    @EventListener
    public void onMemberLeft(MemberLeftEvent event) {
        logger.debug("Raise Members left Listener");
        left++;
    }

    @EventListener
    public void onMemberChanged(MembersChangedEvent event) {
        logger.debug("Raise Members Changed Listener");
        changed++;
    }
}
