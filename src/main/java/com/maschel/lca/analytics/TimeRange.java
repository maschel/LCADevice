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

package com.maschel.lca.analytics;

import java.text.SimpleDateFormat;
import java.util.*;

public enum TimeRange {

    YEAR("year", "yyyy"),
    MONTH("month", "yyyy-M"),
    DAY("day", "yyyy-M-d"),
    HOUR("hour", "yyyy-M-d-H"),
    MINUTE("minute", "yyyy-M-d-H-m"),
    SECOND("second", "yyyy-M-d-H-m-s");

    private final String description;
    private final String timeComponents;

    TimeRange(String description, String timeComponents) {
        this.description = description;
        this.timeComponents = timeComponents;
    }

    public String getCurrentTimeString() {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(timeComponents);
        return formatter.format(now);
    }

    public String getTimeComponents() {
        return timeComponents;
    }

    public String getDescription() {
        return description;
    }
}
