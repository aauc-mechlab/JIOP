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
package no.hials.jiop.generic.evolutionary.ga.mutation;

import java.util.List;
import java.util.Random;
import no.hials.jiop.generic.candidates.Candidate;

/**
 *
 * @author Lars Ivar Hatledal
 */
public abstract class AbstractMutationOperator<E> {

    protected Random rng = new Random();

    private double mutProb, mutChange;

    public AbstractMutationOperator(double mutProb, double mutChange) {
        this.mutProb = mutProb;
        this.mutChange = mutChange;
    }

    public void mutateCandidates(List<Candidate<E>> candidates) {
//        int numMutations = (int) (candidates.size() * mutRate);
//        if (numMutations > candidates.size()) {
//            throw new IllegalArgumentException("The number of mutations are grater than the number of candidates!");
//        }
//        Set<Integer> indexes = new HashSet<>(numMutations);
//        while (indexes.size() != numMutations) {
//            indexes.add(rng.nextInt(candidates.size()));
//        }
//        for (int i : indexes) {
//            mutate(candidates.get(i), mutProb);
//        }
        for (Candidate<E> c : candidates) {
            mutate(c, mutProb, mutChange);
        }
    }

    protected abstract void mutate(Candidate<E> c, double mutationProbability, double mutChange);

    public double getMutChange() {
        return mutChange;
    }

    public void setMutChange(double mutChange) {
        this.mutChange = mutChange;
    }

    public double getMutProb() {
        return mutProb;
    }

    public void setMutProb(double mutProb) {
        this.mutProb = mutProb;
    }

}
