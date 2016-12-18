package net.fnlab.fwd;

import java.util.Map;

import org.onosproject.net.HostId;

/**
 * A demonstrative service for the intent reactive forwarding application to
 * export.
 */
public interface ForwardingMapService {

    /**
     * Get the endpoints of the host-to-host intents that were installed.
     *
     * @return maps of source to destination
     */
    public Map<HostId, HostId> getEndPoints();

}
