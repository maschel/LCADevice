package com.maschel.lca.device.actuator;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class Actuator<T> {

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

            if (Argument.class.isAssignableFrom(argument.getClass())) {
                Argument argInstance = (Argument)argument;
                argInstance.parseRawArguments(args);
                argument = (T)argInstance;
            } else if(argument instanceof List<?>) {
                System.out.println("Array!");
            } else {
                argument = (T)args;
            }

        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to cast arguments, error: " + ex.getMessage());
        }
        return argument;
    }
}
