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
package no.hials.jiop.evolutionary.ga;

import no.hials.jiop.base.candidates.CandidatePair;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import no.hials.jiop.base.candidates.Candidate;

/**
 *
 * @author LarsIvar
 */
public abstract class AbstractCrossoverOperator<E> implements CrossoverOperator<E> {

    private final Random rng = new Random();

    @Override
    public List<Candidate<E>> createoffspring(List<Candidate<E>> candidates, int numMatings) {

//        List<Candidate<E>> matingPool = new ArrayList<>(candidates);
        List<Candidate<E>> offspring = new ArrayList<>(numMatings);
        
        for (int i = 0; i < numMatings; i++) {
            Candidate<E> c1, c2;
            do {
                c1 = candidates.get(rng.nextInt(candidates.size()));
                c2 = candidates.get(rng.nextInt(candidates.size()));
            } while (c1 == c2);
//            matingPool.remove(c1);
//            matingPool.remove(c2);

            offspring.addAll(mate(c1, c2, rng).asList());
        }
        return offspring;
    }

    public abstract CandidatePair<E> mate(Candidate<E> ma, Candidate<E> pa, Random rng);
}
