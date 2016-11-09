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

import java.util.ArrayList;
import java.util.List;

public class Component {

    private List<Component> components = new ArrayList<Component>();
    private List<Actuator> actuators = new ArrayList<Actuator>();
    private List<Sensor> sensors = new ArrayList<Sensor>();

    private String name;

    public Component(String name) {
        this.name = name;
    }

    public void add(Component component) {
        components.add(component);
    }
    public void remove(Component component) {
        components.remove(component);
    }
    public void add(Actuator actuator) {
        actuators.add(actuator);
    }
    public void remove(Actuator actuator) {
        actuators.remove(actuator);
    }
    public void add(Sensor sensor) {
        sensors.add(sensor);
    }
    public void remove(Sensor sensor) {
        sensors.remove(sensor);
    }

    public void updateSensors() {
        for (Sensor s: sensors) {
            s.update();
        }
        // Recurse
        for (Component c: components) {
            c.updateSensors();
        }
    }

    public List<Component> getComponents() {
        return components;
    }

    public List<Component> getDescendantComponents() {
        List<Component> components = getComponents();
        for (Component c: components) {
            components.addAll(c.getDescendantComponents());
        }
        return components;
    }

    public List<Actuator> getActuators() {
        return actuators;
    }

    public List<Actuator> getDescendantActuators() {
        List<Actuator> act = getActuators();
        for (Component c: components) {
            act.addAll(c.getDescendantActuators());
        }
        return act;
    }

    public Actuator getActuatorByName(String name) {
        for (Actuator a: actuators) {
            if (a.getName().equals(name))
                return a;
        }
        for (Component c: components) {
            if (c.getActuatorByName(name) != null) {
                return c.getActuatorByName(name);
            }
        }
        return null;
    }

    public Sensor getSensorByName(String name) {
        for (Sensor s: sensors) {
            if (s.getName().equals(name))
                return s;
        }
        for (Component c: components) {
            if (c.getSensorByName(name) != null) {
                return c.getSensorByName(name);
            }
        }
        return null;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public List<Sensor> getDescendantSensors() {
        List<Sensor> sens = getSensors();
        for (Component c: components) {
            sens.addAll(c.getDescendantSensors());
        }
        return sens;
    }

    public String getName() {
        return this.getName();
    }
}
