package com.maschel;

import com.maschel.lca.device.*;
import com.maschel.lca.device.actuator.Actuator;

import static com.maschel.Arguments.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IllegalAccessException, InstantiationException, IOException {
        final Device d = new Device("testDevice") {

            class UpdateThread extends Thread {
                private Device device;

                UpdateThread(Device device) {
                    this.device = device;
                }

                public void run() {
                    try {
                        while(!Thread.currentThread().isInterrupted())
                        {
                            System.out.println("Update sensors");
                            device.updateSensors();
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException ex) {}
                }
            }

            private UpdateThread updateThread;

            public void connect() {
                updateThread = new UpdateThread(this);
                updateThread.start();
            }

            public void disconnect() {
                System.out.println("Disconnect");
                updateThread.interrupt();
            }
        };

        d.addDeviceSensor(new Sensor("doubleSensor") {
            public Double readSensor() {
                return 10.0;
            }
        });
        d.addDeviceSensor(new Sensor("stringSensor") {
            public String readSensor() {
                return d.getId();
            }
        });
        d.addDeviceSensor(new Sensor("booleanSensor") {
            public Boolean readSensor() {
                return true;
            }
        });
        d.addDeviceSensor(new Sensor("integerSensor") {
            public Integer readSensor() {
                return 20;
            }
        });
        d.addDeviceSensor(new Sensor("testSensor") {
            @Override
            public Float readSensor() {
                return 0.0f;
            }
        });

        Component wheels = new Component("Wheels");
        Component rightWheel = new Component("Rightwheel");
        wheels.add(rightWheel);
        rightWheel.add(new Sensor("rightWheelSensor") {

            public String readSensor() {
                return "Test";
            }
        });

        rightWheel.add(new Actuator<MotorSpeedArgument>("LeftMotorSpeed") {
            @Override
            public void actuate(MotorSpeedArgument args) throws IllegalArgumentException {
                System.out.println("Set left motor speed: " + args.getSpeed());
            }
        });
        rightWheel.add(new Actuator<String>("RightMotorThing") {
            @Override
            public void actuate(String arg) throws IllegalArgumentException {
                System.out.println("Set left motor speed: " + arg);
            }
        });
        d.addComponent(wheels);

        Component leftWheel = new Component("Left Wheel");
        Component leftWheelMotor = new Component("Left Wheel Motor");
        leftWheel.add(leftWheelMotor);
        leftWheelMotor.add(new Sensor<Double>("Left Motor Speed") {
            public Double readSensor() {
                return 120.0;
            }
        });
        leftWheelMotor.add(new Sensor("Left Motor enabled") {
            public Boolean readSensor() {
                return true;
            }
        });
        d.addComponent(leftWheel);

//        for (Sensor s: d.getSensors()) {
//            s.update();
//            System.out.println("Sensor: " + s.getName() + ", Value: " + s.getValue() + ", type: " + s.getType());
//        }

        d.connect();

        Actuator act = d.getActuatorByName("LeftMotorSpeed");

        List<Object> arguments = new ArrayList<Object>();
        arguments.add(10.0);
        act.actuate(act.getParsedArgumentInstance(arguments));

        Actuator act2 = d.getActuatorByName("RightMotorThing");
        act2.actuate("Test");

        while (System.in.available() == 0);

        d.disconnect();


//        try {
//            act.actuate(act.getParsedArgumentInstance(arguments));
//        } catch (IllegalArgumentException ex) {
//            System.out.println("Invalid arguments!");
//        }
    }
}
