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
package no.hials.jiop.candidates;

import java.util.Arrays;

/**
 *
 * @author Lars Ivar Hatledal
 */
public class DoubleArrayCandidate extends GeneralCandidate<double[]> implements NumericCandidate<double[]>{

    public DoubleArrayCandidate(int length) {
        super(length);
    }

    public DoubleArrayCandidate(double[] elements) {
        super(elements);
    }

    public DoubleArrayCandidate(double[] elements, double cost) {
        super(elements, cost);
    }

    @Override
    public GeneralCandidate<double[]> copy() {
        return new DoubleArrayCandidate(getElements().clone(), getCost());
    }


    @Override
    public DoubleArrayCandidate plus(Number[] other) {
        double[] arr = new double[size()];
        for (int i = 0; i < arr.length; i++) {
            set(i, get(i).doubleValue() + other[i].doubleValue());
        }
        return new DoubleArrayCandidate(arr);
    }

    @Override
    public DoubleArrayCandidate minus(Number[] other) {
        double[] arr = new double[size()];
        for (int i = 0; i < arr.length; i++) {
            set(i, get(i).doubleValue() - other[i].doubleValue());
        }
        return new DoubleArrayCandidate(arr);
    }

    @Override
    public DoubleArrayCandidate scale(Number scalar) {
        double[] arr = new double[size()];
        for (int i = 0; i < arr.length; i++) {
            set(i, get(i).doubleValue() * scalar.doubleValue());
        }
        return new DoubleArrayCandidate(arr);
    }

    @Override
    public void clamp(Number min, Number max) {
        for (int i = 0; i < size(); i++) {
            double val = get(i).doubleValue();
            if (val < min.doubleValue()) {
                set(i, min.doubleValue());
            } else if (val > max.doubleValue()) {
                set(i, max.doubleValue());
            }
        }
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
    public String toString() {
        return Arrays.toString(elements);
    }

    @Override
    public double[] randomElements(int length) {
        double[] rand = new double[length];
        for (int i = 0; i < rand.length; i++) {
            rand[i] = rng.nextDouble();
        }
        return rand;
    }

    @Override
    public DoubleArrayCandidate neighbor(double proximity) {
        double[] neighbor = new double[size()];
        for (int i = 0; i < size(); i++) {
            double val = get(i).doubleValue() + rng.nextDouble() * Math.abs(proximity - (-proximity)) + (-proximity);
            if (val < 0) {
                val = 0;
            } else if (val > 1) {
                val = 1;
            }
            neighbor[i] = val;
        }
        return new DoubleArrayCandidate(neighbor);
    }

}
