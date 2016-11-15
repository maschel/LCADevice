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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum TimeRange {

    YEAR("year", new String[] { "yyyy" }),
    MONTH("month", new String[] { "yyyy", "M" }),
    DAY("day", new String[] { "yyyy", "M", "d" }),
    HOUR("hour", new String[] { "yyyy", "M", "d", "H" }),
    MINUTE("minute", new String[] { "yyyy", "M", "d", "H", "m" }),
    SECOND("second", new String[] { "yyyy", "M", "d", "H", "m", "s" });

    private final String description;
    private final List<String> timeComponents;

    TimeRange(String description, String[] timeComponents) {
        this.description = description;
        this.timeComponents = new ArrayList<>(Arrays.asList(timeComponents));
    }

    public List<String> getTimeComponents() {
        return timeComponents;
    }

    public String getDescription() {
        return description;
    }
}
