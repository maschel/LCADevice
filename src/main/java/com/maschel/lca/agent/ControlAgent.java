/*
 *  LCADevice
 *
 *  MIT License
 *
 *  Copyright (c) 2016
 *
 *  Geoffrey Mastenbroek, geoffrey.mastenbroek@student.hu.nl
 *  Feiko Wielsma, feiko.wielsma@student.hu.nl
 *  Robbin van den Berg, robbin.vandenberg@student.hu.nl
 *  Arnoud den Haring, arnoud.denharing@student.hu.nl
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.maschel.lca.agent;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.maschel.lca.agent.message.mapper.SensorMapper;
import com.maschel.lca.agent.message.request.ActuatorRequestMessage;
import com.maschel.lca.agent.message.request.SensorRequestMessage;
import com.maschel.lca.agent.message.response.SensorValueMessage;
import com.maschel.lca.analytics.Analytic;
import com.maschel.lca.device.Device;
import com.maschel.lca.device.sensor.Sensor;
import com.maschel.lca.device.actuator.Actuator;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * ControlAgent class
 * The ControlAgent is used to get sensor value requests and actuator commands from the agent platform and forward
 * them to the device implementation. This Class is also responsible for the device lifecycle:
 * <ul>
 *     <li>Load the device class</li>
 *     <li>Setup</li>
 *     <li>Connect</li>
 *     <li>Update(loop)</li>
 *     <li>(Process requests)</li>
 *     <li>Disconnect</li>
 * </ul>
 */
public class ControlAgent extends Agent {

    private Device agentDevice;

    private Gson gson = new Gson();

    /**
     * Setup the agent platform and setup the device.
     */
    protected void setup() {

        // Get agent arguments
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            String agentDeviceClassName = (String)args[0];
            // Load Device class
            try {
                agentDevice = loadDeviceClass(agentDeviceClassName);
                agentDevice.setup();
                agentDevice.connect();
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                System.exit(1);
            }
        } else {
            System.out.println("ERROR: No device class provided to ControlAgent");
            System.exit(1);
        }

        if (agentDevice.getSensorUpdateInterval() != 0) {
            addBehaviour(new TickerBehaviour(this, agentDevice.getSensorUpdateInterval()) {
                @Override
                protected void onTick() {
                    agentDevice.update();
                    agentDevice.updateSensors();
                }
            });
        }

        addBehaviour(new MessagePerformer(this));

    }

    /**
     * Default MessagePerformer. Is called on a new message and passes it to the correct behaviour based on the
     * ontology specified in the message.
     */
    private class MessagePerformer extends CyclicBehaviour {
        private static final String SENSOR_ONTOLOGY = "sensor";
        private static final String ACTUATOR_ONTOLOGY = "actuator";

        public MessagePerformer(Agent a) {
            super(a);
        }

        @Override
        public void action() {
            MessageTemplate mtPerformative = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mtPerformative);
            if (msg != null) {
                switch (msg.getOntology()) {
                    case SENSOR_ONTOLOGY:
                        myAgent.addBehaviour(new SensorBehaviour(msg));
                        break;
                    case ACTUATOR_ONTOLOGY:
                        myAgent.addBehaviour(new ActuatorBehaviour(msg));
                        break;
                }
            } else {
                block();
            }
        }
    }

    /**
     * SensorBehaviour, is called on a sensor request message
     */
    private class SensorBehaviour extends OneShotBehaviour {

        private SensorMapper sensorMapper = new SensorMapper();

        private ACLMessage message;

        public SensorBehaviour(ACLMessage msg) {
            this.message = msg;
        }

        @Override
        public void action() {

            SensorRequestMessage sensorRequestMessage;
            try {
                sensorRequestMessage = gson.fromJson(
                        message.getContent(),
                        SensorRequestMessage.class
                );
            } catch (JsonSyntaxException ex) {
                sendFailureReply(message, "Invalid JSON syntax");
                return;
            }

            if (sensorRequestMessage.sensor == null || sensorRequestMessage.equals("")) {
                sendFailureReply(message, "Invalid sensor");
                return;
            }

            Sensor sensor = agentDevice.getSensorByName(sensorRequestMessage.sensor);
            if (sensor == null) {
                sendFailureReply(message, "Sensor not found");
                return;
            }

            ACLMessage reply = message.createReply();
            reply.setPerformative(ACLMessage.INFORM);

            SensorValueMessage message = new SensorValueMessage(
                    agentDevice.getId(),
                    sensorMapper.ObjectToDto(sensor)
            );
            reply.setContent(gson.toJson(message));

            myAgent.send(reply);
        }
    }

    /**
     * ActuatorBehaviour, is called on a actuator command message
     */
    private class ActuatorBehaviour extends OneShotBehaviour {

        private ACLMessage message;

        public ActuatorBehaviour(ACLMessage msg) {
            this.message = msg;
        }

        @Override
        public void action() {

            ActuatorRequestMessage actuateMessage;
            try {
                actuateMessage = gson.fromJson(
                        message.getContent(),
                        ActuatorRequestMessage.class
                );
            } catch (JsonSyntaxException ex) {
                sendFailureReply(message, "Invalid JSON syntax");
                return;
            }

            if (actuateMessage.actuator == null || actuateMessage.actuator.equals("")) {
                sendFailureReply(message, "No actuator name specified");
                return;
            }

            Actuator actuator = agentDevice.getActuatorByName(actuateMessage.actuator);
            if (actuator == null) {
                sendFailureReply(message, "Could not find actuator: " + actuateMessage.actuator);
                return;
            }

            try {
                actuator.actuate(actuator.getParsedArgumentInstance(actuateMessage.arguments));
            } catch (Exception e) {
                sendFailureReply(message, "Failed to actuate: " + e.getMessage());
                return;
            }

            sendSuccessReply(message);
        }
    }

    private void sendFailureReply(ACLMessage msg, String reason) {
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.FAILURE);
        reply.setContent(reason);
        this.send(reply);
    }

    private void sendSuccessReply(ACLMessage msg) {
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.AGREE);
        this.send(reply);
    }

    protected void takeDown() {
        agentDevice.disconnect();
        Analytic.closeStorage();
    }

    private Device loadDeviceClass(String agentDeviceClassName) throws Exception {
        try {
            return this.getClass()
                    .getClassLoader()
                    .loadClass(agentDeviceClassName)
                    .asSubclass(Device.class)
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new Exception("Failed to load ControlAgent with class: " + agentDeviceClassName);
        }
    }
}
