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

package com.maschel.lca.agent.message;

import com.maschel.lca.device.Sensor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * Converters for JSON to domain objects (Sensor, Actuator, etc.)
 */
public class Json {

    /**
     * Converts a sensor object to JSONObject.
     * @param sensor The sensor.
     * @return JSON representation of Sensor.
     */
    public static JSONObject sensorToJSON(Sensor sensor) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", sensor.getName());
        jsonObject.put("type", sensor.getType().getCanonicalName());
        jsonObject.put("value", sensor.getValue());
        return jsonObject;
    }

    /**
     * Converts a List of Sensors to a JSONArray.
     * @param sensors The sensor list.
     * @return JSONArray representation of Sensor.
     */
    public static JSONArray sensorArrayToJSON(List<Sensor> sensors) {
        JSONArray array = new JSONArray();
        for (Sensor s : sensors) {
            array.add(sensorToJSON(s));
        }
        return array;
    }
}
