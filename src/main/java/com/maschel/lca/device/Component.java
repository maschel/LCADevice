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
            return c.getActuatorByName(name);
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
