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

    final public String getId() {
        return id;
    }

    public abstract void connect();

    public abstract void update();

    public abstract void disconnect();
}
