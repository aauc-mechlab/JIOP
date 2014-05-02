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
package no.hials.jiop.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author LarsIvar
 */
public class NumericListStructure implements NumericStructure<List<Number>> {

    private final Random rng = new Random();
    private final List<Number> elements;

    public NumericListStructure(int length) {
        elements = new ArrayList<>(length);
    }

    public NumericListStructure(double[] elements) {
        this(elements.length);
        for (double d : elements) {
            this.elements.add(d);
        }
    }

    @Override
    public void randomize() {
        for (int i = 0; i < size(); i++) {
            set(i, rng.nextDouble());
        }
    }

    @Override
    public NumericListStructure plus(Number[] other) {
        double[] arr = new double[size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = elements.get(i).doubleValue() + other[i].doubleValue();
        }
        return new NumericListStructure(arr);
    }

    @Override
    public NumericListStructure minus(Number[] other) {
        double[] arr = new double[size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = elements.get(i).doubleValue() - other[i].doubleValue();
        }
        return new NumericListStructure(arr);
    }

    @Override
    public NumericListStructure scale(Number scalar) {
        double[] arr = new double[size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = elements.get(i).doubleValue() * scalar.doubleValue();
        }
        return new NumericListStructure(arr);
    }

    @Override
    public void clamp(Number min, Number max) {
        for (int i = 0; i < size(); i++) {
            double val = elements.get(i).doubleValue();
            if (val < min.doubleValue()) {
                elements.set(i, min.doubleValue());
            } else if (val > max.doubleValue()) {
                elements.set(i, max.doubleValue());
            }
        }
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public Number get(int index) {
        return elements.get(index);
    }

    @Override
    public void set(int index, Number value) {
        elements.set(index, value.doubleValue());
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
        return elements.toString();
    }

    @Override
    public List<Number> getElements() {
       return elements;
    }

}
