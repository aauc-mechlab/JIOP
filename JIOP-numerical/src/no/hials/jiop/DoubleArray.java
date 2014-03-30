/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hials.jiop;

import java.util.Arrays;

/**
 *
 * @author LarsIvar
 */
public class DoubleArray {

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

    public double[] getArray() {
        return array;
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

    public DoubleArray copy() {
        return new DoubleArray(this);
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

}
