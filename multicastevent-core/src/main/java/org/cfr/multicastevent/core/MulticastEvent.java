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

import java.util.EventObject;

import org.cfr.multicastevent.core.spi.IAddress;

/**
 * 
 * @author acochard, devacfr
 * @since 1.0
 */
public class MulticastEvent extends EventObject {

    /**
     * Default class serial version unique identifier.
     */
    private static final long serialVersionUID = 1L;

    private IAddress sender;

    private IAddress receiver;

    public MulticastEvent() {
        super(new Object());
    }

    public MulticastEvent(Object source) {
        super(source);
    }

    public IAddress sender() {
        return sender;
    }

    public IAddress receiver() {
        return receiver;
    }

    public void setSender(IAddress sender) {
        this.sender = sender;
    }

    public void setReceiver(IAddress receiver) {
        this.receiver = receiver;
    }

    public void setSource(Object source) {
        this.source = source;
    }

}
