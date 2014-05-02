/*
 * Copyright (c) 2014, Aalesund University College
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package no.hials.jiop.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author LarsIvar
 */
public class DoubleArray implements Iterable<Double>, Serializable {

    public final int length;
    protected double[] array;

    public DoubleArray(int length) {
        this.length = length;
        this.array = new double[length];
    }

    public DoubleArray(double... array) {
        this.length = array.length;
        this.array = array;
    }


    protected DoubleArray(DoubleArray array) {
        this.array = array.getArray().clone();
        this.length = array.length;
    }

    public double get(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be negative! Was: " + index);
        } else if (index >= length) {
            throw new IndexOutOfBoundsException();
        }
        return array[index];
    }

    public void set(int index, double value) {
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be negative! Was: " + index);
        } else if (index >= length) {
            throw new IndexOutOfBoundsException();
        }
        array[index] = value;
    }

    public void clamp(double min, double max) {
        for (int i = 0; i < length; i++) {
            double value = array[i];
            if (value < min) {
                array[i] = min;
            } else if (value > max) {
                array[i] = max;
            }
        }
    }

    public DoubleArray clamp(double[] min, double[] max) {
        for (int i = 0; i < length; i++) {
            double value = array[i];
            if (value < min[i]) {
                array[i] = min[i];
            } else if (value > max[i]) {
                array[i] = max[i];
            }
        }
        return this;
    }

    public DoubleArray scale(double factor) {
        for (int i = 0; i < length; i++) {
            set(i, get(i) * factor);
        }
        return this;
    }

    public DoubleArray divide(double factor) {
        for (int i = 0; i < length; i++) {
            set(i, get(i) / factor);
        }
        return this;
    }

    public List<Double> toList() {
        List<Double> list = new ArrayList<>(length);
        for (double d : array) {
            list.add(d);
        }
        return list;
    }

    public double[] getArray() {
        return array;
    }

    public DoubleArray copy() {
        return new DoubleArray(this);
    }

    public String toString(String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(array[i]);
            if (i != length - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return Arrays.toString(array);
    }

    public static DoubleArray random(int length) {
        double[] array = new double[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.random();
        }
        return new DoubleArray(array);
    }

    public static DoubleArray random(double min, double max, int length) {
        double[] array = new double[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.random() * Math.abs(max - min) + min;
        }
        return new DoubleArray(array);
    }

    public static DoubleArray neighbor(DoubleArray array, double factor) {
        double[] neighbor = new double[array.length];
        for (int i = 0; i < neighbor.length; i++) {
            neighbor[i] = array.get(i) + Math.random() * Math.abs(factor - (-factor)) + (-factor);
        }
        return new DoubleArray(neighbor);
    }

    public static DoubleArray plus(DoubleArray d1, DoubleArray d2) {
        if (d1.length != d2.length) {
            throw new IllegalArgumentException("The length of the input arrays must match!");
        }
        double[] arr = new double[d1.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = d1.get(i) + d2.get(i);
        }
        return new DoubleArray(arr);
    }

    public static DoubleArray minus(DoubleArray d1, DoubleArray d2) {
        if (d1.length != d2.length) {
            throw new IllegalArgumentException("The length of the input arrays must match!");
        }
        double[] arr = new double[d1.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = d1.get(i) - d2.get(i);
        }
        return new DoubleArray(arr);
    }

    public static DoubleArray plus(double[] d1, double[] d2) {
        if (d1.length != d2.length) {
            throw new IllegalArgumentException("The length of the input arrays must match!");
        }
        double[] arr = new double[d1.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = d1[i] + d2[i];
        }
        return new DoubleArray(arr);
    }

    public static DoubleArray minus(double[] d1, double[] d2) {
        if (d1.length != d2.length) {
            throw new IllegalArgumentException("The length of the input arrays must match!");
        }
        double[] arr = new double[d1.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = d1[i] - d2[i];
        }
        return new DoubleArray(arr);
    }

    public static DoubleArray toDoubleArray(double[]  
        ... arrays) {
        List<Double> list = new LinkedList<>();
        for (double[] array : arrays) {
            for (double d : array) {
                list.add(d);
            }
        }
        return DoubleArray.toDoubleArray(list);
    }

    public static DoubleArray toDoubleArray(Collection<? extends Double> c) {
        int length = c.size();
        double[] array = new double[length];
        int i = 0;
        for (double d : c) {
            array[i++] = d;
        }
        return new DoubleArray(array);
    }

    public static DoubleArray fill(int length, double value) {
        double[] arr = new double[length];
        Arrays.fill(arr, value);
        return new DoubleArray(arr);
    }

    @Override
    public Iterator<Double> iterator() {
        return new DoubleArrayIterator();
    }

    private class DoubleArrayIterator implements Iterator<Double> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index != length;
        }

        @Override
        public Double next() {
            return get(index++);
        }
    }

}
