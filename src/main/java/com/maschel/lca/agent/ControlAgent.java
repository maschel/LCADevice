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

import com.maschel.lca.agent.message.Json;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
        private static final String SENSOR_LIST_ONTOLOGY = "sensorlist";
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
                    case SENSOR_LIST_ONTOLOGY:
                        myAgent.addBehaviour(new SensorListBehaviour(msg));
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

        private ACLMessage message;

        public SensorBehaviour(ACLMessage msg) {
            this.message = msg;
        }

        @Override
        public void action() {
            String sensorName = message.getContent();
            Sensor sensor = agentDevice.getSensorByName(sensorName);
            if (sensor != null) {
                ACLMessage reply = message.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent(Json.sensorToJSON(sensor).toJSONString());
                myAgent.send(reply);
            }
        }
    }

    /**
     * SensorListBehaviour, is called on a sensorList request message
     */
    private class SensorListBehaviour extends OneShotBehaviour {

        private ACLMessage message;

        public SensorListBehaviour(ACLMessage msg) {
            this.message = msg;
        }

        @Override
        public void action() {
            ACLMessage reply = message.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent(Json.sensorArrayToJSON(agentDevice.getSensors()).toJSONString());
            myAgent.send(reply);
        }
    }

    /**
     * ActuatorBehaviour, is called on a actuator command message
     */
    private class ActuatorBehaviour extends OneShotBehaviour {

        private static final String JSON_ENCODING = "json";

        private ACLMessage message;

        public ActuatorBehaviour(ACLMessage msg) {
            this.message = msg;
        }

        @Override
        public void action() {

            if (message.getEncoding() == null || !message.getEncoding().equals(JSON_ENCODING)) {
                sendFailureReply("Invalid encoding");
                return;
            }

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject;
            try {
                jsonObject = (JSONObject)jsonParser.parse(message.getContent());
            } catch(ParseException pe) {
                sendFailureReply("Failed to parse JSON message");
                return;
            }

            String actuatorName = jsonObject.get("name").toString();
            if (actuatorName == null) {
                sendFailureReply("No actuator name specified");
                return;
            }

            Actuator actuator = agentDevice.getActuatorByName(actuatorName);
            if (actuator == null) {
                sendFailureReply("Could not find actuator: " + actuatorName);
                return;
            }

            JSONArray jsonArguments = (JSONArray)jsonObject.get("arguments");
            if (jsonArguments == null) {
                sendFailureReply("No arguments found");
            }

            try {
                actuator.actuate(actuator.getParsedArgumentInstance(jsonArguments));
            } catch (Exception e) {
                sendFailureReply("Failed to actuate: " + e.getMessage());
                return;
            }

            sendSuccessReply();
        }

        private void sendSuccessReply() {
            ACLMessage reply = message.createReply();
            reply.setPerformative(ACLMessage.AGREE);
            myAgent.send(reply);
        }

        private void sendFailureReply(String reason) {
            ACLMessage reply = message.createReply();
            reply.setPerformative(ACLMessage.FAILURE);
            reply.setContent(reason);
            myAgent.send(reply);
        }
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
