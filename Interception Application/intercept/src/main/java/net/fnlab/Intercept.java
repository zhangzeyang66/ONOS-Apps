/*
 * Copyright 2017-present Open Networking Laboratory
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
package net.fnlab;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onlab.packet.Ethernet;
import org.onlab.packet.IPv4;
import org.onlab.packet.Ip4Prefix;
import org.onlab.packet.TpPort;
import org.onlab.util.Tools;
import org.onosproject.cfg.ComponentConfigService;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.Device;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.flowobjective.DefaultForwardingObjective;
import org.onosproject.net.flowobjective.FlowObjectiveService;
import org.onosproject.net.flowobjective.ForwardingObjective;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;

/**
 * Skeletal ONOS application component.
 */
@Service
@Component(immediate = true)
public class Intercept implements InterceptService {

    private static final int DEFAULT_TIMEOUT = 0;
    private static final int DEFAULT_PRIORITY = 100;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected ComponentConfigService cfgService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowRuleService flowRuleService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowObjectiveService flowObjectiveService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    private ApplicationId appId;

    @Property(name = "serverIP", value = "10.0.0.3",
            label = "The server's IP address")
    private String serverIP = "10.0.0.3";

    @Property(name = "flowTimeout", intValue = DEFAULT_TIMEOUT,
            label = "Configure Flow Timeout for installed flow rules; " +
                    "default is 0")
    private int flowTimeout = DEFAULT_TIMEOUT;

    @Property(name = "flowPriority", intValue = DEFAULT_PRIORITY,
            label = "Configure Flow Priority for installed flow rules; " +
                    "default is 100")
    private int flowPriority = DEFAULT_PRIORITY;

    @Activate
    protected void activate(ComponentContext context) {
        cfgService.registerProperties(getClass());

        modified(context);

        appId = coreService.registerApplication("net.fnlab.intercept");
        log.info("Started", appId.id());
    }

    @Modified
    public void modified(ComponentContext context) {
        Dictionary<?, ?> properties = context.getProperties();

        serverIP = Tools.get(properties, "serverIP");
        flowTimeout = Tools.getIntegerProperty(properties, "flowTimeout", DEFAULT_TIMEOUT);
        flowPriority = Tools.getIntegerProperty(properties, "flowPriority", DEFAULT_PRIORITY);
    }

    @Deactivate
    protected void deactivate() {
        cfgService.unregisterProperties(getClass(), false);
        flowRuleService.removeFlowRulesById(appId);
        log.info("Stopped");
    }

    @Override
    public void startIntercept(boolean isTcp, String ipAddress, int portNumber) {
        flowRuleService.removeFlowRulesById(appId);
        TrafficSelector.Builder selectorBuilder = DefaultTrafficSelector.builder();
        if (isTcp) {
            selectorBuilder.matchIPProtocol(IPv4.PROTOCOL_TCP)
                    .matchEthType(Ethernet.TYPE_IPV4)
                    .matchTcpDst(TpPort.tpPort(portNumber))
                    .matchIPDst(Ip4Prefix.valueOf(serverIP + "/32"))
                    .matchIPSrc(Ip4Prefix.valueOf(ipAddress + "/32"));
        } else {
            selectorBuilder.matchIPProtocol(IPv4.PROTOCOL_UDP)
                    .matchEthType(Ethernet.TYPE_IPV4)
                    .matchUdpDst(TpPort.tpPort(portNumber))
                    .matchIPDst(Ip4Prefix.valueOf(serverIP + "/32"))
                    .matchIPSrc(Ip4Prefix.valueOf(ipAddress + "/32"));
        }

        TrafficTreatment treatment = DefaultTrafficTreatment.builder()
                .drop()
                .build();

        ForwardingObjective forwardingObjective = DefaultForwardingObjective.builder()
                .withSelector(selectorBuilder.build())
                .withTreatment(treatment)
                .withPriority(flowPriority)
                .withFlag(ForwardingObjective.Flag.VERSATILE)
                .fromApp(appId)
                .makeTemporary(flowTimeout)
                .makePermanent()
                .add();
        for (Device device : deviceService.getDevices()) {
            flowObjectiveService.forward(device.id(), forwardingObjective);
        }
    }

    @Override
    public void deleteIntercept() {
        flowRuleService.removeFlowRulesById(appId);
    }
}
