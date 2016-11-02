package com.maschel.lca.device.actuator;

import java.util.List;

public abstract class Argument {
    public abstract void parseRawArguments(List<Object> args) throws IllegalArgumentException;
}
