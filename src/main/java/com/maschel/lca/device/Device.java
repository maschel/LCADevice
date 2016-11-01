package com.maschel.lca.device;

import java.util.List;

public abstract class Device {

    private final String id;

    private Component rootComponent;

    public Device(String id) {
        this.id = id;
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

    final public void updateSensors() {
        rootComponent.updateSensors();
    }

    public abstract void connect();

    public abstract void disconnect();
}
