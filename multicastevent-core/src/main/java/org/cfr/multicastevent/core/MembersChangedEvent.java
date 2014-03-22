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
package org.cfr.multicastevent.core;

import java.util.Collection;
import java.util.EventObject;

import org.cfr.multicastevent.core.spi.IMember;

/**
 * 
 * @author devacfr<christophefriederich@mac.com>
 * @since 1.0
 */
public class MembersChangedEvent extends EventObject {

    /**
     * 
     */
    private static final long serialVersionUID = -3438936632485077928L;

    /**
     * 
     */
    private final Collection<IMember> members;

    /**
     * 
     * @param members
     * @param source
     */
    public MembersChangedEvent(final Collection<IMember> members, final Object source) {
        super(source);
        this.members = members;
    }

    /**
     * 
     * @return
     */
    public Collection<IMember> getMembers() {
        return members;
    }
}
