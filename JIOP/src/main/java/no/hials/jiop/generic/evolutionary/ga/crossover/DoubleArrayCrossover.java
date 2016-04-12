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
package no.hials.jiop.generic.evolutionary.ga.crossover;

import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/dwdyer/watchmaker/blob/master/framework/src/java/main/org/uncommons/watchmaker/framework/operators/DoubleArrayCrossover.java
 * @author Lars Ivar Hatledal
 */
public class DoubleArrayCrossover extends AbstractCrossoverOperator<double[]> {

    private final double crossoverPoints;

    public DoubleArrayCrossover(double crossoverPoints, double crossoverProbability) {
        super(crossoverProbability);
        this.crossoverPoints = crossoverPoints;
    }

    @Override
    protected List<double[]> mate(double[] ma, double[] pa) {
        if (ma.length != pa.length) {
            throw new IllegalArgumentException("Cannot perform cross-over with different length parents.");
        }
        double[] offspring1 = new double[ma.length];
        System.arraycopy(ma, 0, offspring1, 0, ma.length);
        double[] offspring2 = new double[pa.length];
        System.arraycopy(pa, 0, offspring2, 0, pa.length);
        // Apply as many cross-overs as required.
        double[] temp = new double[ma.length];
        for (int i = 0; i < crossoverPoints; i++) {
            // Cross-over index is always greater than zero and less than
            // the length of the parent so that we always pick a point that
            // will result in a meaningful cross-over.
            int crossoverIndex = (1 + rng.nextInt(ma.length - 1));
            System.arraycopy(offspring1, 0, temp, 0, crossoverIndex);
            System.arraycopy(offspring2, 0, offspring1, 0, crossoverIndex);
            System.arraycopy(temp, 0, offspring2, 0, crossoverIndex);
        }
        List<double[]> result = new ArrayList<>(2);
        result.add(offspring1);
        result.add(offspring2);
        return result;
    }
}
