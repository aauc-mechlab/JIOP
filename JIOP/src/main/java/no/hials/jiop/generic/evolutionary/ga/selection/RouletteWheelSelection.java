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
package no.hials.jiop.generic.evolutionary.ga.selection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import no.hials.jiop.generic.candidates.Candidate;

/**
 *
 * @author LarsIvar
 */
public class RouletteWheelSelection<E> extends AbstractSelectionOperator<E> {

    public RouletteWheelSelection(double selectionRate) {
        super(selectionRate);
    }

    @Override
    protected List<Candidate<E>> selectCandidates(List<Candidate<E>> candidates, int howMany) {
        double[] cumulativeFitnesses = new double[candidates.size()];
        cumulativeFitnesses[0] = getAdjustedFitness(candidates.get(0).getCost());
        for (int i = 1; i < candidates.size(); i++) {
            double fitness = getAdjustedFitness(candidates.get(i).getCost());
            cumulativeFitnesses[i] = cumulativeFitnesses[i - 1] + fitness;
        }

        List<Candidate<E>> selection = new ArrayList<>(howMany);
        for (int i = 0; i < howMany; i++) {
            double randomFitness = rng.nextDouble() * cumulativeFitnesses[cumulativeFitnesses.length - 1];
            int index = Arrays.binarySearch(cumulativeFitnesses, randomFitness);
            if (index < 0) {
                // Convert negative insertion point to array index.
                index = Math.abs(index + 1);
            }
            selection.add(candidates.get(index));
        }
        return selection;
    }

    private double getAdjustedFitness(double cost) {
        return cost == 0 ? Double.POSITIVE_INFINITY : 1 / cost;

    }

}
