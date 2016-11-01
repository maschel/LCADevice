package com.maschel.lca.device;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class Actuator<T extends Argument> {

    private String name;
    private Class<T> argumentClass;

    @SuppressWarnings("unchecked")
    public Actuator(String name) {
        this.name = name;
        this.argumentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public abstract void actuate(T args) throws IllegalArgumentException;

    public String getName() {
        return name;
    }

    public T getParsedArgumentInstance(List<Object> args) throws IllegalArgumentException {
        T argument;
        try {
            argument = argumentClass.newInstance();
            argument.parseRawArguments(args);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to cast arguments, error: " + ex.getMessage());
        }
        return argument;
    }
}
