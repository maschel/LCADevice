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

import com.maschel.lca.analytics.Analytic;
import org.mapdb.*;
import org.mapdb.serializer.SerializerArray;
import org.mapdb.serializer.SerializerArrayTuple;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class AnalyticsStorageMapDB implements AnalyticsStorage {

    private static String DATABASE_FILE = "analytics.db";
    private static String ANALYTICS_MAP = "analytics";

    DB db = null;

    private HTreeMap<Object[], Object> analyticsMap = null;

    private final int COMMIT_INTERVAL = 1000;
    private long lastCommitMillis = 0;

    private void openDatabase() {
        if (db == null) {
            db = DBMaker.fileDB(DATABASE_FILE)
                    .fileMmapEnableIfSupported()
                    .transactionEnable()
                    .closeOnJvmShutdown()
                    .make();
            analyticsMap = db.hashMap(ANALYTICS_MAP)
                    .keySerializer(new SerializerArray(Serializer.STRING)).valueSerializer(Serializer.JAVA)
                    .createOrOpen();
        }
    }

    @Override
    public void store(Analytic analytic) {

        openDatabase();

        Object[] currentKey = new String[]{
                analytic.getSensor().getName(),
                analytic.getAggregate().getDescription(),
                analytic.getTimeRange().getCurrentTimeString()
        };

        if(!analyticsMap.containsKey(currentKey)) {
            analyticsMap.put(currentKey, analytic.getAggregate().getDefaultValue());
        }

        Object currentValue = analyticsMap.get(currentKey);
        analyticsMap.put(currentKey, analytic.getAggregate().calculate(currentValue, analytic.getSensor().getValue()));

        commit();
    }

    @Override
    public List<AnalyticsSensorData> getCurrentData(Boolean purgeData) {

        openDatabase();

        Set<Map.Entry<Object[], Object>> currentSet = analyticsMap.getEntries();
        if (purgeData) {
            analyticsMap.clear();
            db.commit();
        }

        List<AnalyticsSensorData> data = new ArrayList<>();
        for (Map.Entry<Object[], Object> entry: currentSet) {
            data.add(new AnalyticsSensorData(
                    (String)entry.getKey()[0],
                    (String)entry.getKey()[1],
                    (String)entry.getKey()[2],
                    entry.getValue()
            ));
        }
        return data;
    }

    private void commit() {
        if (lastCommitMillis == 0L || ((System.currentTimeMillis() - lastCommitMillis) > COMMIT_INTERVAL)) {
            lastCommitMillis = System.currentTimeMillis();
            db.commit();
        }
    }

    @Override
    public void close() {
        if(db != null && !db.isClosed()) {
            db.close();
            analyticsMap = null;
            db = null;
        }
    }
}
