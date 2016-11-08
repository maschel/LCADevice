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

package com.maschel.lca.device.actuator;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class Actuator<T> {

    private String name;
    private Class<T> argumentClass;

    @SuppressWarnings("unchecked")
    public Actuator(String name) {
        this.name = name;
        this.argumentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public abstract void actuate(T args) throws IllegalArgumentException;

    public String getName() {
        return name;
    }

    public T getParsedArgumentInstance(List<Object> args) throws IllegalArgumentException {
        try {

            if (argumentClass == Void.class) {
                return null;
            }

            T argument = argumentClass.newInstance();
            if (Argument.class.isAssignableFrom(argument.getClass())) {
                Argument argInstance = (Argument) argument;
                argInstance.parseRawArguments(args);
                argument = (T) argInstance;
                return argument;
            } else {
                return (T)args.get(0);
            }

        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to cast arguments, error: " + ex.getMessage());
        }
    }
}
