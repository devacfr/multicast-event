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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.cfr.multicastevent.core.spi.IChannel;
import org.cfr.multicastevent.core.spi.IMember;

/**
 * 
 * @author devacfr
 * @since 1.0
 */
@Named(DefaultChannelAdapter.MULTICAST_CHANEL_ADAPTER_NAME)
@Singleton
public class DefaultChannelAdapter extends AbstractChannelAdapter {

    public final static String MULTICAST_CHANEL_ADAPTER_NAME = "MulticastChanelAdapter";

    private IInboundEndpoint inboundEndpoint;

    @Inject
    public DefaultChannelAdapter(@Nonnull final IChannel channel, @Named("clusterName") String clusterName) {
        super(channel, clusterName);

    }

    public void setInboundEndpoint(@Nullable IInboundEndpoint inboundEndpoint) {
        this.inboundEndpoint = inboundEndpoint;
    }

    @Nullable
    public IInboundEndpoint getInboundEndpoint() {
        return inboundEndpoint;
    }

    @Override
    public void memberChanged(Collection<IMember> member) {
        //noop
    }

    @Override
    public void memberJoined(@Nullable IMember member) {
        //noop
    }

    @Override
    public void memberLeft(@Nullable IMember member) {
        //noop
    }

    @Override
    public void receiveEvent(MulticastEvent event) {
        if (this.inboundEndpoint == null)
            return;
        this.inboundEndpoint.receive(event);
    };

}
