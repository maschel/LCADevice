package com.maschel.lca.device;

import java.util.List;

public interface IComponent {

    void add(IComponent component);
    void remove(IComponent component);

    String getName();
    void update();

    List<Component> getComponents();
    List<Component> getDescendantComponents();
    List<Sensor> getSensors();
    List<Sensor> getDescendantSensors();

    String toString();
}
