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
package no.hials.jiop.evolutionary;

import no.hials.jiop.base.candidates.Candidate;
import java.util.Random;
import no.hials.jiop.base.AbstractEvaluator;
import no.hials.jiop.base.candidates.containers.CandidateContainer;
import no.hials.jiop.base.MLMethod;
import no.hials.jiop.base.candidates.encoding.BasicEncoding;

/**
 *
 * @author LarsIvar
 */
public abstract class DEEngine<E> extends MLMethod<E> {

    private final double F, CR;
    private final Random rng = new Random();

    public DEEngine(double F, double CR, CandidateContainer<E> container, AbstractEvaluator<E> evaluator) {
        super(container, evaluator);
        this.F = F;
        this.CR = CR;
    }
    
    @Override
    protected void doIteration() {
        for (Candidate<E> c : getContainer()) {
            E p = c.getVariables();
            E p1, p2, p3;
            do {
                int rand = rng.nextInt(getContainer().size());
                p1 = getContainer().get(rand).getVariables();
            } while (p1 == c);
            do {
                int rand = rng.nextInt(getContainer().size());
                p2 = getContainer().get(rand).getVariables();
            } while (p2 == c && p2 == p1);
            do {
                int rand = rng.nextInt(getContainer().size());
                p3 = getContainer().get(rand).getVariables();
            } while (p3 == c && p3 == p1 && p3 == p2);

            int R = rng.nextInt(getContainer().candidateLength());
            BasicEncoding<E> differentiate = differentiate(R, F, CR, p, p1, p2, p3);
            Candidate<E> sample = getContainer().createCandidate(differentiate);
            if (sample.getCost() < c.getCost()) {
                getContainer().set(getContainer().indexOf(c), sample);
                if (sample.getCost() < getBestCandidate().getCost()) {
                    setBestCandidate(sample);
                }
            }
        }
    }
    
    

    public abstract BasicEncoding<E> differentiate(int R, double F, double CR, E c, E c1, E c2, E c3);

}
