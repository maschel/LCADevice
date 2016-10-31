package com.maschel.lca.device;

public abstract class Sensor<T> {

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
