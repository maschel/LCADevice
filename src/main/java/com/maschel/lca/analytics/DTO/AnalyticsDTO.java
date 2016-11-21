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

package com.maschel.lca.analytics.DTO;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AnalyticsDTO {
    private String deviceId;

    private List<DateComponent> dateComponents = new ArrayList<>();

    public AnalyticsDTO(String deviceId) {
        this.deviceId = deviceId;
    }

    public void addAnalytic(String key, Object value) {
        String[] keyParts = key.split("_");
        String[] components = keyParts[2].split("-");
        DateComponent dc = getOrCreateDateComponent(Arrays.asList(components));
        dc.addOrUpdateSensor(keyParts[0], keyParts[1], value);
    }

    private DateComponent getOrCreateDateComponent(List<String> components) {
        DateComponent dc = null;
        for (DateComponent comp: this.dateComponents) {
            if (comp.component.equals(components.get(0))) {
                dc = comp;
            }
        }
        if (dc == null) {
            dc = new DateComponent(components.get(0));
            this.dateComponents.add(dc);
        }
        if (components.size() > 1) {
            return dc.getOrCreateDateComponent(components.subList(1, (components.size())));
        } else {
            return dc;
        }
    }

    private class DateComponent {
        private String component;
        private List<DateComponent> dateComponents = new ArrayList<>();
        private List<Sensor> sensors = new ArrayList<>();

        DateComponent(String component) {
            this.component = component;
        }

        public DateComponent getOrCreateDateComponent(List<String> components) {
            DateComponent dc = null;
            for (DateComponent comp: this.dateComponents) {
                if (comp.component.equals(components.get(0))) {
                    dc = comp;
                }
            }
            if (dc == null) {
                dc = new DateComponent(components.get(0));
                this.dateComponents.add(dc);
            }
            if (components.size() > 1) {
                return dc.getOrCreateDateComponent(components.subList(1, (components.size())));
            } else {
                return dc;
            }
        }

        public void addOrUpdateSensor(String name, String aggregate, Object value) {
            Sensor sensor = null;
            for (Sensor s: sensors) {
                if (s.name.equals(name) && s.aggregate.equals(aggregate)) {
                    sensor = s;
                }
            }
            if (sensor == null) {
                sensor = new Sensor(name, aggregate, value);
                sensors.add(sensor);
            } else {
                sensor.value = value;
            }
        }

        public String toString() {
            return component;
        }

        private class Sensor {
            private String name;
            private String aggregate;
            private Object value;

            Sensor(String name, String aggregate, Object value) {
                this.name = name;
                this.aggregate = aggregate;
                this.value = value;
            }
        }
    }
}
