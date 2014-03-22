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
package org.cfr.multicastevent.core.spi;

import java.net.InetAddress;

import javax.annotation.Nullable;

/**
 * This is simple member interface connected in a cluster.
 * @author devacfr<christophefriederich@mac.com>
 * @since 1.0
 */
public interface IMember {

    /**
     * Gets the Internet Protocol address if exists.
     * @return Return {@link InetAddress} of member if exists, otherwise <code>null</code>.
     */
    @Nullable
    InetAddress getAddress();

}