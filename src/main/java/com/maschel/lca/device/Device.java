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

public abstract class Device {

    private final String id;
    private final int sensorUpdateInterval;
    private Component rootComponent;

    public Device(String id, int sensorUpdateInterval) {
        this.id = id;
        this.sensorUpdateInterval = sensorUpdateInterval;
        this.rootComponent = new Component(id);
    }

    final public void addDeviceSensor(Sensor sensor) {
        rootComponent.add(sensor);
    }

    final public void addComponent(Component component) {
        rootComponent.add(component);
    }

    final public List<Component> getComponents() {
        return rootComponent.getDescendantComponents();
    }

    final public Sensor getSensorByName(String name) { return rootComponent.getSensorByName(name); }

    final public List<Sensor> getSensors() {
        return rootComponent.getDescendantSensors();
    }

    final public Actuator getActuatorByName(String name) { return rootComponent.getActuatorByName(name); }

    final public List<Actuator> getActuators() {
        return rootComponent.getDescendantActuators();
    }

    final public String getId() {
        return id;
    }

    final public int getSensorUpdateInterval() {
        return sensorUpdateInterval;
    }

    final public void updateSensors() {
        rootComponent.updateSensors();
    }

    public abstract void setup();

    public abstract void connect();

    public abstract void update();

    public abstract void disconnect();
}
