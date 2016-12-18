/*
 * Copyright 2014 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fnlab.fwd;

import java.util.Map;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.net.HostId;

/**
 * A demo service that lists the endpoints for which flows are installed.
 */
@Command(scope = "onos", name = "fwdmap",
        description = "Lists the endpoints for which flows are installed")
public class ForwardingMapCommand extends AbstractShellCommand {

    // formatted string for output to CLI
    private static final String FMT = "src=%s, dst=%s";

    // the String to hold the optional argument
    @Argument(index = 0, name = "hostId", description = "Host ID of source",
            required = false, multiValued = false)
    private String hostId = null;

    // reference to our service
    private ForwardingMapService service;
    // to hold the service's response
    private Map<HostId, HostId> hmap;

    @Override
    protected void execute() {
        // get a reference to our service
        service = get(ForwardingMapService.class);

        /*
         * getEndPoints() returns an empty map even if it contains nothing, so
         * we don't need to check for null hmap here.
         */
        hmap = service.getEndPoints();

        // check for an argument, then display information accordingly
        if (hostId != null) {
            // we were given a hostId to filter on, print only those that match
            HostId host = HostId.hostId(hostId);
            for (Map.Entry<HostId, HostId> el : hmap.entrySet()) {
                if (el.getKey().equals(host)) {
                    print(FMT, el.getKey(), el.getValue());
                }
            }
        } else {
            // print everything we have
            for (Map.Entry<HostId, HostId> el : hmap.entrySet()) {
                print(FMT, el.getKey(), el.getValue());
            }
        }
    }
}
