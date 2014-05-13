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
package no.hials.jiop.candidates;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LarsIvar
 */
public class DoubleListCandidate extends GeneralCandidate<List<Double>> implements NumericCandidate<List<Double>> {

    public DoubleListCandidate(List<Double> elements) {
        super(elements);
    }

    public DoubleListCandidate(List<Double> elements, double cost) {
        super(elements, cost);
    }

    @Override
    public int getDimension() {
        return elements.size();
    }

    @Override
    public Candidate<List<Double>> neighbor(double proximity) {
        List<Double> neighbor = new ArrayList<>(getDimension());
        for (int i = 0; i < getDimension(); i++) {
            double val = elements.get(i) + (rng.nextDouble() * Math.abs(proximity - (-proximity)) + (-proximity));
            if (val < 0) {
                val = 0;
            } else if (val > 1) {
                val = 1;
            }
            neighbor.add(val);
        }
        return new DoubleListCandidate(neighbor);
    }

    @Override
    public NumericCandidate plus(Number[] other) {
        List<Double> list = new ArrayList<>(getDimension());
        for (int i = 0; i < list.size(); i++) {
            set(i, elements.get(i) + other[i].doubleValue());
        }
        return new DoubleListCandidate(list);
    }

    @Override
    public NumericCandidate minus(Number[] other) {
        List<Double> list = new ArrayList<>(getDimension());
        for (int i = 0; i < list.size(); i++) {
            set(i, elements.get(i) - other[i].doubleValue());
        }
        return new DoubleListCandidate(list);
    }

    @Override
    public NumericCandidate scale(Number scalar) {
        List<Double> list = new ArrayList<>(getDimension());
        for (int i = 0; i < getDimension(); i++) {
            list.add(elements.get(i) * scalar.doubleValue());
        }
        return new DoubleListCandidate(list);
    }

    @Override
    public void clamp(Number min, Number max) {
        for (int i = 0; i < getDimension(); i++) {
            double val = elements.get(i);
            if (val < min.doubleValue()) {
                elements.set(i, min.doubleValue());
            } else if (val > max.doubleValue()) {
                elements.set(i, max.doubleValue());
            }
        }
    }

    @Override
    public void set(int index, Number value) {
        elements.set(index, value.doubleValue());
    }

    @Override
    public NumericCandidate<List<Double>> copy() {
        List<Double> list = new ArrayList<>(getDimension());
        for (int i = 0; i < getDimension(); i++) {
            list.add((double) elements.get(i));
        }
        return new DoubleListCandidate(list, cost);
    }

    @Override
    public Number get(int index) {
        return elements.get(index);
    }

    @Override
    public String toString() {
        return elements.toString();
    }

}
