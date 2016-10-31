package com.maschel.lca.device;

import java.util.Arrays;
import java.util.List;

public abstract class Actuator<T> {

    private String name;
    private final Class<T> type;

    public Actuator(Class<T> type, String name) {
        this.name = name;
        this.type = type;
    }

    public void doNow(T[] args) {
        actuate(createArgumentArray(Arrays.asList(args)));
    }

    public abstract void actuate(T... args) throws IllegalArgumentException;

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return this.type;
    }

    public <T> T[] createArgumentArray(List<T> list) {
        Class clazz = this.getType();
        T[] array = (T[]) java.lang.reflect.Array.newInstance(clazz, list.size());
        return list.toArray(array);
    }
}
