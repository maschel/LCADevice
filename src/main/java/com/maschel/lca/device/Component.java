package com.maschel.lca.device;

import java.util.ArrayList;
import java.util.List;

public class Component implements IComponent {

    private List<IComponent> components = new ArrayList<IComponent>();
    private String name;

    public Component(String name) {
        this.name = name;
    }

    public void add(IComponent component) {
        components.add(component);
    }

    public void remove(IComponent component) {
        components.remove(component);
    }

    public String getName() {
        return this.getName();
    }

    public void update() {
        for (IComponent c: components) {
            c.update();
        }
    }

    public List<Component> getComponents() {
        List<Component> comp = new ArrayList<Component>();
        for (IComponent c: components) {
            if (c.getClass().isAssignableFrom(Component.class)) {
                comp.add((Component) c);
            }
        }
        return comp;
    }

    public List<Component> getDescendantComponents() {
        List<Component> components = new ArrayList<Component>();
        for (IComponent c: components) {
            if (c.getClass().isAssignableFrom(Component.class)) {
                components.add((Component) c);
            } else {
                components.addAll(c.getDescendantComponents());
            }
        }
        return components;
    }

    public List<Sensor> getSensors() {
        List<Sensor> sensors = new ArrayList<Sensor>();
        for (IComponent c: components) {
            if (Sensor.class.isAssignableFrom(c.getClass())) {
                sensors.add((Sensor)c);
            }
        }
        return sensors;
    }

    public List<Sensor> getDescendantSensors() {
        List<Sensor> sensors = new ArrayList<Sensor>();
        for (IComponent c: components) {
            if (Sensor.class.isAssignableFrom(c.getClass())) {
                sensors.add((Sensor)c);
            } else {
                sensors.addAll(c.getDescendantSensors());
            }
        }
        return sensors;
    }
}
