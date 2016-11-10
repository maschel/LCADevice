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

package com.maschel.lca.device;

import com.maschel.lca.device.actuator.Actuator;

import java.util.List;

/**
 * Abstract Device class
 * This class should be extended by the device that needs to be connected the JADE agent platform.
 * When the class is extended it can be loaded into the platform:
 * java jade.Boot -gui -agents AgentName:com.maschel.lca.agent.ControlAgent(org.package.TheDeviceImplementation)
 * The device class has the following lifecycle:
 * <ul>
 *     <li>(Device class is loaded by the agent platform)</li>
 *     <li>setup()</li>
 *     <li>Connect()</li>
 *     <li>update() (loops based on sensorUpdateInterval)</li>
 *     <li>(Process requests)</li>
 *     <li>disconnect()</li>
 * </ul>
 */
public abstract class Device {

    private final String id;
    private final int sensorUpdateInterval;
    private Component rootComponent;

    /**
     * Default Device constructor
     * This should be called using super(id, sensorUpdateInterval) in any device implementation.
     * @param id The device id
     * @param sensorUpdateInterval The interval at which sensors should be updated.
     */
    public Device(String id, int sensorUpdateInterval) {
        this.id = id;
        this.sensorUpdateInterval = sensorUpdateInterval;
        this.rootComponent = new Component(id);
    }

    /**
     * Add a device sensor.
     * @param sensor The sensor to add.
     */
    final public void addDeviceSensor(Sensor sensor) {
        rootComponent.add(sensor);
    }

    /**
     * Add a device actuator.
     * @param actuator The actuator to add.
     */
    final public void addDeviceActuator(Actuator actuator) { rootComponent.add(actuator); }

    /**
     * Add a component to the device.
     * @param component The component to add.
     */
    final public void addComponent(Component component) {
        rootComponent.add(component);
    }

    /**
     * Get all the (sub)components of the device.
     * @return List of the (sub)components.
     */
    final public List<Component> getComponents() {
        return rootComponent.getDescendantComponents();
    }

    /**
     * Searches the device and component tree for a sensor with the given name.
     * @param name Sensor name.
     * @return {@link Sensor} instance with given sensor name or null if not found.
     */
    final public Sensor getSensorByName(String name) { return rootComponent.getSensorByName(name); }

    /**
     * Get all the (sub)sensors of the device
     * @return List of all the (sub)sensors.
     */
    final public List<Sensor> getSensors() {
        return rootComponent.getDescendantSensors();
    }

    /**
     * Searches the device and component tree for a actuator with the given name.
     * @param name Actuator name.
     * @return {@link Actuator} instance with given actuator name or null if not found.
     */
    final public Actuator getActuatorByName(String name) { return rootComponent.getActuatorByName(name); }

    /**
     * Get all the (sub)actuators of the device.
     * @return List of the (sub)actuators.
     */
    final public List<Actuator> getActuators() {
        return rootComponent.getDescendantActuators();
    }

    /**
     * Get the Device id.
     * @return The Device id.
     */
    final public String getId() {
        return id;
    }

    /**
     * Get the sensor update interval.
     * @return The sensor update interval.
     */
    final public int getSensorUpdateInterval() {
        return sensorUpdateInterval;
    }

    /**
     * This updates the (sub)sensors of the device and all its components.
     * Note: this should not be called manually.
     */
    final public void updateSensors() {
        rootComponent.updateSensors();
    }

    /**
     * This method will be called when de agent platform has started.
     * Use this method to setup/instantiate the device.
     */
    public abstract void setup();

    /**
     * This method will be called when setup is done.
     * Use this method to open the connect to the device.
     */
    public abstract void connect();

    /**
     * This method will be called on every update (sensorUpdateInterval), before updateSensors.
     * Use this method to update the sensor values in the device library (if applicable).
     */
    public abstract void update();

    /**
     * This method will be called when the agent(platform) will be stopped.
     * Use this method to clean up any open connections to the device.
     */
    public abstract void disconnect();
}
