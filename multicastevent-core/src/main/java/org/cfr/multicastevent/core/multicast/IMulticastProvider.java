package org.cfr.multicastevent.core.multicast;

import java.util.Collection;

/**
 * 
 * @author acochard [Aug 5, 2009]
 *
 */
public interface IMulticastProvider {

    /**
     * Start the provider
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * stop the provider
     * @throws Exception
     */
    void stop() throws Exception;

    boolean isStarted();

    /**
     * Retrieves the multicast network's members.
     * @return the members
     */
    Collection<IMember> getMembers();

    /**
     * Publish an event to the multicast network.
     * @param event the event
     */
    void publish(MulticastEvent event);

    /**
     * Gets indicating wether the provider has to start automatically on starting of Spring Application Context.
     * @return
     */
    boolean isStartOnContext();

    /**
     * Gets indicating whether the provider has to stop automatically on stopping of Spring Application Context.
     * @return
     */
    boolean isStopOnContext();

}
