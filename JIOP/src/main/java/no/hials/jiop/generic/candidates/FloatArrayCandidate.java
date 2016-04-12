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
package no.hials.jiop.generic.candidates;

import java.util.Arrays;

/**
 *
 * @author LarsIvar
 */
public class FloatArrayCandidate extends GeneralCandidate<float[]> implements NumericCandidate<float[]> {

    public FloatArrayCandidate(float[] elements) {
        super(elements);
    }

    public FloatArrayCandidate(float[] elements, double cost) {
        super(elements, cost);
    }

    @Override
    public FloatArrayCandidate copy() {
        return new FloatArrayCandidate(getElements().clone(), getCost());
    }

    @Override
    public FloatArrayCandidate plus(Number[] other) {
        float[] arr = new float[size()];
        for (int i = 0; i < arr.length; i++) {
            set(i, get(i).floatValue() + other[i].floatValue());
        }
        return new FloatArrayCandidate(arr);
    }

    @Override
    public FloatArrayCandidate minus(Number[] other) {
        float[] arr = new float[size()];
        for (int i = 0; i < arr.length; i++) {
            set(i, get(i).floatValue() - other[i].floatValue());
        }
        return new FloatArrayCandidate(arr);
    }

    @Override
    public FloatArrayCandidate scale(Number scalar) {
        float[] arr = new float[size()];
        for (int i = 0; i < arr.length; i++) {
            set(i, get(i).floatValue() * scalar.floatValue());
        }
        return new FloatArrayCandidate(arr);
    }

    @Override
    public void clamp(Number min, Number max) {
        for (int i = 0; i < size(); i++) {
            float val = get(i).floatValue();
            if (val < min.floatValue()) {
                set(i, min.floatValue());
            } else if (val > max.floatValue()) {
                set(i, max.floatValue());
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

//    @Override
//    public void set(int index, Number value) {
//        elements[index] = value.floatValue();
//    }
    
      @Override
    public void set(int index, Object o) {
        elements[index] = (float) o;
    }


    @Override
    public String toString() {
        return Arrays.toString(elements);
    }

    @Override
    public FloatArrayCandidate neighbor(double proximity) {
        float[] neighbor = new float[size()];
        for (int i = 0; i < size(); i++) {
            float val = (float) (elements[i] + (rng.nextFloat() * Math.abs(proximity - (-proximity)) + (-proximity)));
            if (val < 0) {
                val = 0;
            } else if (val > 1) {
                val = 1;
            }
            neighbor[i] = val;
        }
        return new FloatArrayCandidate(neighbor);
    }

}
