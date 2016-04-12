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
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import no.hials.jiop.generic.candidates.Candidate;

/**
 *
 * @author LarsIvar
 */
public abstract class AbstractCrossoverOperator<E> {

    protected final Random rng = new Random();

    private double crossoverProbability;

    public AbstractCrossoverOperator(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }

    public List<E> mateCandidates(List<Candidate<E>> candidates, int numOffspring) {
        if (numOffspring % 2 != 0) {
//            throw new RuntimeException();
            numOffspring += 1;
        }

//        Set<Candidate<E>> pSet = new HashSet<>(numOffspring / 2);
//        while (pSet.size() != numOffspring / 2) {
//            pSet.add(candidates.get(rng.nextInt(candidates.size())));
//        }
//        List<Candidate<E>> pList = new ArrayList<>(pSet);
        Candidate<E> ma, pa;
        ma = candidates.get(rng.nextInt(candidates.size()));
        do {
            pa = candidates.get(rng.nextInt(candidates.size()));
        } while (ma != pa);

        List<E> offspring = new ArrayList<>(numOffspring * 2);
        for (int i = 0; i < numOffspring; i++) {
            if (rng.nextDouble() < crossoverProbability) {
                offspring.addAll(mate(ma.getElements(), pa.getElements()));
            } else {
                offspring.add(ma.getElements());
                offspring.add(pa.getElements());
            }
        }
        return offspring;
    }

    protected abstract List<E> mate(E ma, E pa);

    public double getCrossoverProbability() {
        return crossoverProbability;
    }

    public void setCrossoverProbability(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }

}
