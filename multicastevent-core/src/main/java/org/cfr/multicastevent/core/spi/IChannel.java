package org.cfr.multicastevent.core.spi;

import java.util.Collection;

/**
 * 
 * @author cfriedri
 *
 */
public interface IChannel {

    /**
     * 
     * @return
     */
    boolean isStarted();

    /**
     * 
     * @throws Exception
     */
    void stop() throws Exception;

    /**
     * 
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * 
     * @param event
     * @throws Exception 
     */
    void sendNotification(MulticastEvent event) throws Exception;

    /**
     * 
     * @return
     */
    Collection<IMember> getMembers();

    /**
     * 
     * @return
     */
    IAddress getLocalAddress();

}
