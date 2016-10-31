package com.maschel;

import com.maschel.lca.device.Component;
import com.maschel.lca.device.Device;
import com.maschel.lca.device.Sensor;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        final Device d = new Device("testDevice") {

            public void connect() {
                // connect
            }

            public void update() {
                //
            }

            public void disconnect() {

            }
        };

        d.addDeviceSensor(new Sensor("doubleSensor") {
            public Double readSensorData() {
                return 10.0;
            }
        });
        d.addDeviceSensor(new Sensor("stringSensor") {
            public String readSensorData() {
                return d.getId();
            }
        });
        d.addDeviceSensor(new Sensor("booleanSensor") {
            public Boolean readSensorData() {
                return true;
            }
        });
        d.addDeviceSensor(new Sensor("integerSensor") {
            public Integer readSensorData() {
                return 20;
            }
        });
        d.addDeviceSensor(new Sensor("testSensor") {
            @Override
            public Float readSensorData() {
                return 0.0f;
            }
        });

        Component wheels = new Component("Wheels");
        Component rightWheel = new Component("Rightwheel");
        wheels.add(rightWheel);
        rightWheel.add(new Sensor("rightWheelSensor") {

            public String readSensorData() {
                return "Test";
            }
        });
        d.addComponent(wheels);

        Component leftWheel = new Component("Left Wheel");
        Component leftWheelMotor = new Component("Left Wheel Motor");
        leftWheel.add(leftWheelMotor);
        leftWheelMotor.add(new Sensor<Double>("Left Motor Speed") {
            public Double readSensorData() {
                return 120.0;
            }
        });
        leftWheelMotor.add(new Sensor("Left Motor enabled") {
            public Boolean readSensorData() {
                return true;
            }
        });
        d.addComponent(leftWheel);

        for (Sensor s: d.getSensors()) {
            s.update();
            System.out.println("Sensor: " + s.getName() + ", Value: " + s.getValue() + ", type: " + s.getType());
        }
    }
}
