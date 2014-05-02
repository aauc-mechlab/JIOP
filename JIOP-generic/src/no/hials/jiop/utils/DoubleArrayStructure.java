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
package no.hials.jiop.utils;

import java.util.Iterator;

/**
 *
 * @author LarsIvar
 */
public class DoubleArrayStructure extends NumericStructure<double[]> {

    private final double[] elements;

    public DoubleArrayStructure(double[] elements) {
        this.elements = elements;
    }

    @Override
    public DoubleArrayStructure plus(NumericStructure other) {
        double[] arr = new double[size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = elements[i] + other.get(i).doubleValue();
        }
        return new DoubleArrayStructure(arr);
    }

    @Override
    public DoubleArrayStructure minus(NumericStructure other) {
        double[] arr = new double[size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = elements[i] - other.get(i).doubleValue();
        }
        return new DoubleArrayStructure(arr);
    }

    @Override
    public DoubleArrayStructure scale(Number scalar) {
        double[] arr = new double[size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = elements[i] * scalar.doubleValue();
        }
        return new DoubleArrayStructure(arr);
    }

    @Override
    public double[] getElements() {
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
        elements[index] = value.doubleValue();
    }

    @Override
    public Iterator iterator() {
        return new StructureIterator(this);
    }

}
