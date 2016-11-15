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

package com.maschel.lca.analytics.storage;

import com.maschel.lca.analytics.Aggregates;
import com.maschel.lca.analytics.Analytic;
import com.maschel.lca.analytics.TimeRange;
import com.maschel.lca.device.Device;
import com.maschel.lca.device.sensor.Sensor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AnalyticsStorageJSON implements AnalyticsStorage {

    private static final String JSON_FILE = "analytics.json";

    private static final String ANALYTICS_TAG = "analytics";
    private static final String SENSORS_TAG = "sensors";
    private static final String VALUES_TAG = "values";

    private BufferedWriter output;
    private JSONParser parser = new JSONParser();
    private JSONObject rootObject;

    public AnalyticsStorageJSON() {
        readDatabaseFromFile(true);
    }

    @Override
    public void store(Analytic analytic) {
        JSONObject sensorStore = getOrCreateSensorStore(analytic.getSensor());
        JSONObject timeRangeStore = getOrCreateTimeRangeStore(sensorStore, analytic.getTimeRange());
        Object value = getOrCreateValue(timeRangeStore, analytic.getAggregate());
        Object newValue = analytic.getAggregate().calculate(value, analytic.getSensor().getValue());
        updateValue(timeRangeStore, analytic.getAggregate(), newValue);
    }

    private JSONObject getOrCreateSensorStore(Sensor sensor) {
        JSONObject analytics = (JSONObject)rootObject.get(ANALYTICS_TAG);
        JSONObject sensorsObject = (JSONObject)analytics.get(SENSORS_TAG);
        if (sensorsObject.containsKey(sensor.getName())) {
            return (JSONObject)sensorsObject.get(sensor.getName());
        } else {
            sensorsObject.put(sensor.getName(), new JSONObject());
            JSONObject sensorObject = (JSONObject)sensorsObject.get(sensor.getName());
            sensorObject.put("values", new JSONObject());
            writeDatabaseToFile();
            return sensorObject;
        }
    }

    private JSONObject getOrCreateTimeRangeStore(JSONObject sensorStore, TimeRange timeRange) {
        JSONObject valuesObject = (JSONObject)sensorStore.get(VALUES_TAG);
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat();

        JSONObject currentObject = valuesObject;

        for (String component: timeRange.getTimeComponents()) {
            formatter.applyPattern(component);
            String currDateComponent = formatter.format(now);
            if (!currentObject.containsKey(currDateComponent)) {
                currentObject.put(currDateComponent, new JSONObject());
            }
            currentObject = (JSONObject)currentObject.get(currDateComponent);
        }
        writeDatabaseToFile();
        return currentObject;
    }

    private Object getOrCreateValue(JSONObject timeRangeStore, Aggregates aggregate) {
        if (!timeRangeStore.containsKey(aggregate.getDescription())) {
            timeRangeStore.put(aggregate.getDescription(), aggregate.getDefaultValue());
            writeDatabaseToFile();
        }
        return timeRangeStore.get(aggregate.getDescription());
    }

    private void updateValue(JSONObject timeRangeStore, Aggregates aggregate, Object newValue) {
        timeRangeStore.put(aggregate.getDescription(), newValue);
        writeDatabaseToFile();
    }

    private void readDatabaseFromFile(Boolean firstTry) {
        try {
            Object obj = parser.parse(new FileReader(JSON_FILE));
            rootObject = (JSONObject)obj;
        } catch (IOException e) {
            if (firstTry) {
                System.out.println("Failed to read database file, creating: " + JSON_FILE);
                createDatabaseFile();
            }
        } catch (ParseException e) {
            System.out.println("Failed to parse database file");
        }
    }

    private void writeDatabaseToFile() {
        try {
            getFileWriter().write(rootObject.toJSONString());
            output.flush();
            // TODO: This is so ugly it makes me cry, FIND A SOLUTION!
            getFileWriter().close();
            output = null;
        } catch (IOException e) {
            System.out.println("Failed to write database file changes");
        }
    }

    private void createDatabaseFile() {
        String template = "{\"deviceId\":\"" + Device.deviceId + "\",\"analytics\":{\"sensors\":{}}}";
        Path file = Paths.get(JSON_FILE);
        try {
            Files.createFile(file);
            getFileWriter().write(template);
            getFileWriter().flush();
            // TODO: This is so ugly it makes me cry, FIND A SOLUTION!
            getFileWriter().close();
            output = null;
            readDatabaseFromFile(false);
        } catch (IOException e) {
            System.out.println("Failed to create database file");
        }
    }

    private BufferedWriter getFileWriter() {
        if (output == null) {
            try {
                output = new BufferedWriter(new FileWriter(new File(JSON_FILE), false));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return output;
    }
}
