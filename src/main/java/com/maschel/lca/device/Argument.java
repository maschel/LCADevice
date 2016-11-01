package com.maschel.lca.device;

import java.util.List;

public abstract class Argument {
    public abstract void parseRawArguments(List<Object> args) throws IllegalArgumentException;
}
