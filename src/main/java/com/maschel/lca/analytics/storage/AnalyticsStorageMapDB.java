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
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.concurrent.ConcurrentMap;

public class AnalyticsStorageMapDB implements AnalyticsStorage {

    private static String DATABASE_FILE = "analytics.db";
    private static String ANALYTICS_MAP = "analytics";

    DB db = DBMaker.fileDB(DATABASE_FILE)
            .closeOnJvmShutdown()
            .make();

    ConcurrentMap analyticsMap = db.hashMap(ANALYTICS_MAP).createOrOpen();

    @Override
    public void store(Analytic analytic) {
        String currentKey = analytic.getCurrentDescription();
        if(!analyticsMap.containsKey(currentKey)) {
            analyticsMap.put(currentKey, analytic.getAggregate().getDefaultValue());
        }
        Object currentValue = analyticsMap.get(currentKey);
        analyticsMap.put(currentKey, analytic.getAggregate().calculate(currentValue, analytic.getSensor().getValue()));
    }

    @Override
    public void close() {
        if(!db.isClosed()) {
            db.close();
        }
    }
}
