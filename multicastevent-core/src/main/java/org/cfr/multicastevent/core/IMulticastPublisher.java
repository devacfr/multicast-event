package org.cfr.multicastevent.core;

import java.util.Collection;

import org.cfr.multicastevent.core.spi.IMember;
import org.cfr.multicastevent.core.spi.MulticastEvent;

public interface IMulticastPublisher {

    public static String MULTICAST_EVENT_PUBLISHER_NAME = "MulticastEventPublisherName";

    /**
     * 
     * @return
     */
    String getClusterName();

    Collection<IMember> getMembers();

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

    /**
     * 
     * @return
     */
    boolean isStarted();

    /**
     * Publish an event to the multicast network.
     * @param event the event
     * @throws Exception 
     */
    void sendNotification(MulticastEvent event) throws Exception;

    /**
     * 
     * @param event
     */
    void receiveEvent(MulticastEvent event);

    /**
     * 
     * @param member
     */
    void memberJoined(IMember member);

    /**
     * 
     * @param member
     */
    void memberLeft(IMember member);

}