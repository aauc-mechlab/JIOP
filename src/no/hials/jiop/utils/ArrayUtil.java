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

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Lars Ivar Hatledal
 */
public class ArrayUtil {

    public static double[] randomD(int length) {
        double[] array = new double[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = Math.random();

        }
        return array;
    }

    public static float[] randomF(int length) {
        float[] array = new float[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = (float) Math.random();
        }
        return array;
    }

    public static double[] randomD(double min, double max, int length) {
        double[] array = new double[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = (Math.random() * Math.abs(max - min) + min);
        }
        return array;
    }

    public static float[] randomF(float min, float max, int length) {
        float[] array = new float[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = (float) (Math.random() * Math.abs(max - min) + min);
        }
        return array;
    }

    public static double[] neighbor(double[] original, double proximity) {
        double[] array = original.clone();
        for (int i = 0; i < array.length; i++) {
            array[i] += (Math.random() * Math.abs(proximity + proximity) - proximity);
        }
        return array;
    }

    public static float[] neighbor(float[] original, double proximity) {
        float[] array = original.clone();
        for (int i = 0; i < array.length; i++) {
            array[i] += (float) (Math.random() * Math.abs(proximity + proximity) - proximity);
        }
        return array;
    }

    public static double[] clamp(double min, double max, double[] array) {
        for (int i = 0; i < array.length; i++) {
            double value = array[i];
            if (value < min) {
                array[i] = min;
            } else if (value > max) {
                array[i] = max;
            }
        }
        return array;
    }

    public static double[] cloneClamp(double min, double max, double[] original) {
        double[] array = newInstance(original);
        for (int i = 0; i < array.length; i++) {
            double value = array[i];
            if (value < min) {
                array[i] = min;
            } else if (value > max) {
                array[i] = max;
            }
        }
        return array;
    }

    public static float[] clamp(float min, float max, float[] array) {
        for (int i = 0; i < array.length; i++) {
            float value = array[i];
            if (value < min) {
                array[i] = min;
            } else if (value > max) {
                array[i] = max;
            }
        }
        return array;
    }

    public static float[] cloneClamp(float min, float max, float[] original) {
        float[] array = newInstance(original);
        for (int i = 0; i < array.length; i++) {
            float value = array[i];
            if (value < min) {
                array[i] = min;
            } else if (value > max) {
                array[i] = max;
            }
        }
        return array;
    }

    public static double[] scale(double[] original, double scaling) {
        double[] array = newInstance(original);
        for (int i = 0; i < array.length; i++) {
            array[i] *= scaling;
        }
        return array;
    }

    public static float[] scale(float[] original, float scaling) {
        float[] array = newInstance(original);
        for (int i = 0; i < array.length; i++) {
            array[i] *= scaling;
        }
        return array;
    }

    public static double[] divide(double[] original, double divide) {
        double[] array = newInstance(original);
        for (int i = 0; i < array.length; i++) {
            array[i] /= divide;
        }
        return array;
    }

    public static float[] divide(float[] original, float divide) {
        float[] array = newInstance(original);
        for (int i = 0; i < array.length; i++) {
            array[i] /= divide;
        }
        return array;
    }
    
     public static double[] mult(double[] arr1, double[] arr2) {
        if (arr1.length != arr2.length) {
            throw new IllegalArgumentException("Lengths does not match! " + arr1.length + " .vs " + arr2.length);
        }
        double[] array = new double[arr1.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = arr1[i] * arr2[i];
        }
        return array;
    }

    public static float[] mult(float[] arr1, float[] arr2) {
        if (arr1.length != arr2.length) {
            throw new IllegalArgumentException("Lengths does not match! " + arr1.length + " .vs " + arr2.length);
        }
        float[] array = new float[arr1.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = arr1[i] * arr2[i];
        }
        return array;
    }

    public static double[] plus(double[] arr1, double[] arr2) {
        if (arr1.length != arr2.length) {
            throw new IllegalArgumentException("Lengths does not match! " + arr1.length + " .vs " + arr2.length);
        }
        double[] array = new double[arr1.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = arr1[i] + arr2[i];
        }
        return array;
    }

    public static float[] plus(float[] arr1, float[] arr2) {
        if (arr1.length != arr2.length) {
            throw new IllegalArgumentException("Lengths does not match! " + arr1.length + " .vs " + arr2.length);
        }
        float[] array = new float[arr1.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = arr1[i] + arr2[i];
        }
        return array;
    }

    public static double[] minus(double[] arr1, double[] arr2) {
        if (arr1.length != arr2.length) {
            throw new IllegalArgumentException("Lengths does not match! " + arr1.length + " .vs " + arr2.length);
        }
        double[] array = new double[arr1.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = arr1[i] - arr2[i];
        }
        return array;
    }

    public static float[] minus(float[] arr1, float[] arr2) {
        if (arr1.length != arr2.length) {
            throw new IllegalArgumentException("Lengths does not match! " + arr1.length + " .vs " + arr2.length);
        }
        float[] array = new float[arr1.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = arr1[i] - arr2[i];
        }
        return array;
    }

    public static double[] parse(String data, String delimiter) {
        String[] values = data.split(delimiter);
        double[] array = new double[values.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = Double.parseDouble(values[i]);
        }
        return array;
    }

    public static double[] merge(double[] arr1, double[] arr2) {
        double[] array = new double[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, array, 0, arr1.length);
        System.arraycopy(arr2, 0, array, arr1.length, arr2.length);
        return array;
    }

    public static float[] merge(float[] arr1, float[] arr2) {
        float[] array = new float[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, array, 0, arr1.length);
        System.arraycopy(arr2, 0, array, arr1.length, arr2.length);
        return array;
    }

    public static double[] toDoubleArray(List<Double> list) {
        double[] array = new double[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static float[] toFloatArray(List<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static List<Double> asList(double[] array) {
        List<Double> list = new LinkedList<>();
        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }
        return list;
    }

    public static List<Float> asList(float[] array) {
        List<Float> list = new LinkedList<>();
        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }
        return list;
    }

    public static double[] newInstance(double[] original) {
        double[] array = new double[original.length];
        System.arraycopy(original, 0, array, 0, original.length);
        return array;
    }

    public static float[] newInstance(float[] original) {
        float[] array = new float[original.length];
        System.arraycopy(original, 0, array, 0, original.length);
        return array;
    }

    public static String toString(double[] array, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i != array.length - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static String toString(byte[] array, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(Integer.toBinaryString(array[i]));
            if (i != array.length - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public double max(double... values) {
        double max = 0;
        for (int i = 0; i < values.length; i++) {
            if (i == 0) {
                max = values[i];
            } else {
                if (values[i] > max) {
                    max = values[i];
                }
            }
        }
        return max;
    }

    public float max(float... values) {
        float max = 0;
        for (int i = 0; i < values.length; i++) {
            if (i == 0) {
                max = values[i];
            } else {
                if (values[i] > max) {
                    max = values[i];
                }
            }
        }
        return max;
    }

    public int max(int... values) {
        int max = 0;
        for (int i = 0; i < values.length; i++) {
            if (i == 0) {
                max = values[i];
            } else {
                if (values[i] > max) {
                    max = values[i];
                }
            }
        }
        return max;
    }

    public double min(double... values) {
        double min = 0;
        for (int i = 0; i < values.length; i++) {
            if (i == 0) {
                min = values[i];
            } else {
                if (values[i] < min) {
                    min = values[i];
                }
            }
        }
        return min;
    }

    public float min(float... values) {
        float min = 0;
        for (int i = 0; i < values.length; i++) {
            if (i == 0) {
                min = values[i];
            } else {
                if (values[i] < min) {
                    min = values[i];
                }
            }
        }
        return min;
    }

    public int min(int... values) {
        int min = 0;
        for (int i = 0; i < values.length; i++) {
            if (i == 0) {
                min = values[i];
            } else {
                if (values[i] < min) {
                    min = values[i];
                }
            }
        }
        return min;
    }

}
