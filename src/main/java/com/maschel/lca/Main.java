/*
 *  LCADevice
 *
 *  MIT License
 *
 *  Copyright (c) 2016
 *
 *  Geoffrey Mastenbroek, geoffrey.mastenbroek@student.hu.nl
 *  Feiko Wielsma, feiko.wielsma@student.hu.nl
 *  Robbin van den Berg, robbin.vandenberg@student.hu.nl
 *  Arnoud den Haring, arnoud.denharing@student.hu.nl
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.maschel.lca;

import com.maschel.lca.analytics.AggregateCounter;
import com.maschel.lca.analytics.AggregateOperator;
import com.maschel.lca.analytics.Analytic;
import com.maschel.lca.analytics.TimeRange;
import com.maschel.lca.device.Device;
import com.maschel.lca.device.sensor.Sensor;

import java.io.IOException;

public class Main {

    public static void main(String [] args) throws IOException {
        Device testDevice = new Device("testDevice", 50) {
            @Override
            public void setup() {

            }

            @Override
            public void connect() {

            }

            @Override
            public void update() {

            }

            @Override
            public void disconnect() {

            }
        };

        Sensor testSensor = new Sensor("testSensor", 50) {
            @Override
            public Double readSensor() {
                return 12.0;
            }
        };

        // Analytics
        Analytic.registerAnalytic(new Analytic(testSensor, AggregateOperator.DOUBLE_TOTAL, TimeRange.HOUR));
        Analytic.registerAnalytic(new Analytic(testSensor, AggregateCounter.DOUBLE_HIGHER_THEN(10.0), TimeRange.MINUTE));
        Analytic.registerAnalytic(new Analytic(testSensor, AggregateOperator.DOUBLE_MAX, TimeRange.DAY));

        // Fake some sensor polling
        while(System.in.available() == 0) {
            testSensor.update();
        }
    }
}
