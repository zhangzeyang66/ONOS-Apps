package org.onosproject.net.apps;

import java.util.Map;

import org.onosproject.net.HostId;

/**
 * A demonstrative service for the intent reactive forwarding application to
 * export. 
 * location:onos/core/api/src/main/java/org/onosproject/net/apps/ForwardingMapService.java
 */
public interface ForwardingMapService {

    /**
     * Get the endpoints of the host-to-host intents that were installed.
     *
     * @return maps of source to destination
     */
    public Map<HostId, HostId> getEndPoints();

}
