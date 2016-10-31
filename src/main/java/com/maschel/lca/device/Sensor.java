package com.maschel.lca.device;

import java.util.ArrayList;
import java.util.List;

public abstract class Sensor<T> implements IComponent {

    private String name;
    private T value;

    private long minUpdateInterval = 0;
    private long lastSensorRead = 0;

    public Sensor(String name) {
        this.name = name;
    }

    public Sensor(String name, long minUpdateIntervalMillis) {
        this.name = name;
        this.minUpdateInterval = minUpdateIntervalMillis;
    }

    final public void add(IComponent component) {}
    final public void remove(IComponent component) {}
    final public List<Component> getComponents() { return new ArrayList<Component>(); }
    final public List<Component> getDescendantComponents() { return new ArrayList<Component>(); }
    final public List<Sensor> getSensors() { return new ArrayList<Sensor>(); }
    final public List<Sensor> getDescendantSensors() { return new ArrayList<Sensor>(); }

    final public String getName() {
        return this.name;
    }

    final public void update() {
        if (lastSensorRead == 0 || (System.currentTimeMillis() > (lastSensorRead + minUpdateInterval))) {
            lastSensorRead = System.currentTimeMillis();
            this.value = readSensorData();
        }
    }

    final public T getValue() {
        return this.value;
    }

    final public Class<?> getType() {
        return getValue().getClass();
    }

    public abstract T readSensorData();
}
