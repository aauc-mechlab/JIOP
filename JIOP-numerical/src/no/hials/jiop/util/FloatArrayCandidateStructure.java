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

import java.util.Random;

/**
 *
 * @author LarsIvar
 */
public class FloatArrayCandidateStructure extends FloatArrayStructure implements CandidateStructure<float[]>, NumericCandidateStructure<float[]> {

    private final Random rng = new Random();
    private double cost = Float.MAX_VALUE;

    public FloatArrayCandidateStructure(int length) {
        super(length);
    }

    public FloatArrayCandidateStructure(float[] elements) {
        super(elements);
    }
    
     public FloatArrayCandidateStructure(float[] elements, double cost) {
        super(elements);
        this.cost = cost;
    }

    @Override
    public FloatArrayCandidateStructure neighbor(double proximity) {
        float[] arr = new float[size()];
        for (int i = 0; i < arr.length; i++) {
            float val = (float) (get(i).floatValue() + rng.nextDouble() * Math.abs(proximity - (-proximity)) + (-proximity));
            if (val < 0) {
                val = 0;
            } else if (val > 1) {
                val = 1;
            }
            arr[i] = val;
        }
        return new FloatArrayCandidateStructure(arr);
    }

    @Override
    public synchronized double getCost() {
        return cost;
    }

    @Override
    public synchronized void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public int compareTo(CandidateStructure o) {
        if (getCost() == getCost()) {
            return 0;
        } else if (getCost() < o.getCost()) {
            return -1;
        } else {
            return 1;
        }
    }

}
