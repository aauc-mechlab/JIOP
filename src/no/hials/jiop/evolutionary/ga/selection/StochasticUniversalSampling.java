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
package no.hials.jiop.evolutionary.ga.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.evolutionary.ga.SelectionOperator;

/**
 *
 * @author Lars Ivar Hatledal
 */
public class StochasticUniversalSampling<E> implements SelectionOperator<E> {
    
    private final Random rng = new Random();

    @Override
    public List<Candidate<E>> selectCandidates(List<Candidate<E>> candidates, int numSelections) {
        // Calculate the sum of all fitness values.
        double aggregateFitness = 0;
        for (Candidate<E> candidate : candidates) {
            aggregateFitness += getAdjustedFitness(candidate.getCost());
        }

        List<Candidate<E>> selection = new ArrayList<>(numSelections);
        // Pick a random offset between 0 and 1 as the starting point for selection.
        double startOffset = rng.nextDouble();
        double cumulativeExpectation = 0;
        int index = 0;
        for (Candidate<E> candidate : candidates) {
            // Calculate the number of times this candidate is expected to
            // be selected on average and add it to the cumulative total
            // of expected frequencies.
            cumulativeExpectation += getAdjustedFitness(candidate.getCost()) / aggregateFitness * numSelections;

            // If f is the expected frequency, the candidate will be selected at
            // least as often as floor(f) and at most as often as ceil(f). The
            // actual count depends on the random starting offset.
            while (cumulativeExpectation > startOffset + index) {
                selection.add(new Candidate<>(candidate.getEncoding().copy(), candidate.getCost()));
                index++;
            }
        }
        return selection;
    }

    private double getAdjustedFitness(double rawFitness) {
            // If standardised fitness is zero we have found the best possible
        // solution.  The evolutionary algorithm should not be continuing
        // after finding it.
        return rawFitness == 0 ? Double.POSITIVE_INFINITY : 1 / rawFitness;

    }

}
