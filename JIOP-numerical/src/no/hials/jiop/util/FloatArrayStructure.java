/*
 * Copyright (c) 2014, LarsIvar
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
package no.hials.jiop.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

/**
 *
 * @author LarsIvar
 */
public class FloatArrayStructure implements NumericStructure<float[]> {

    private final Random rng = new Random();
    private final float[] elements;

    public FloatArrayStructure(int length) {
        this.elements = new float[length];
    }

    public FloatArrayStructure(float[] elements) {
        this.elements = elements.clone();
    }

    @Override
    public void randomize() {
        for (int i = 0; i < size(); i++) {
            set(i, (float) rng.nextDouble());
        }
    }

    @Override
    public FloatArrayStructure plus(Number[] other) {
        float[] arr = new float[size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = elements[i] + other[i].floatValue();
        }
        return new FloatArrayStructure(arr);
    }

    @Override
    public FloatArrayStructure minus(Number[] other) {
        float[] arr = new float[size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = elements[i] - other[i].floatValue();
        }
        return new FloatArrayStructure(arr);
    }

    @Override
    public FloatArrayStructure scale(Number scalar) {
        float[] arr = new float[size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = elements[i] * scalar.floatValue();
        }
        return new FloatArrayStructure(arr);
    }

    @Override
    public void clamp(Number min, Number max) {
        float[] arr = getElements().clone();
        for (int i = 0; i < arr.length; i++) {
            double val = arr[i];
            if (val < min.floatValue()) {
                arr[i] = min.floatValue();
            } else if (val > max.floatValue()) {
                arr[i] = max.floatValue();
            }
        }
    }

//    @Override
//    public Number[] getValues() {
//        Float[] d = new Float[size()];
//        for (int i = 0; i < d.length; i++) {
//            d[i] = get(i).floatValue();
//        }
//        return d;
//    }

    @Override
    public float[] getElements() {
        return elements;
    }

    @Override
    public int size() {
        return elements.length;
    }

    @Override
    public Number get(int index) {
        return elements[index];
    }

    @Override
    public void set(int index, Number value) {
        elements[index] = value.floatValue();
    }

    @Override
    public Iterator iterator() {
        return new StructureIterator(this);
    }

    @Override
    public String toString(String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            sb.append(get(i));
            if (i != size() - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return Arrays.toString(elements);
    }

}
