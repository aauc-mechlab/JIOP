/*
 * Copyright (c) 2014, Lars Ivar
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
package no.hials.jiop.base.candidates.containers;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.hials.jiop.base.Evaluator;
import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.base.candidates.encoding.ParticleEncoding;

/**
 *
 * @author Lars Ivar
 */
public abstract class ParticleListContainer<E> extends CandidateListContainer<E> {

    public ParticleListContainer(int size, int candidateLength, Evaluator<E> evaluator, boolean multiThreaded) {
        super(size, candidateLength, evaluator, multiThreaded);
    }

    @Override
    public abstract ParticleEncoding<E> randomEncoding(int length);

    @Override
    public abstract ParticleEncoding<E> wrapVariables(E original);

    @Override
    public CandidateContainer<E> evaluateAll() {
        if (multiThreaded) {
            for (Candidate c : this) {
                completionService.submit(new EvaluateCandidates(c));
            }
            for (Candidate c : this) {
                try {
                    Future<Double> take = completionService.take();
                    Double get = take.get();
//                    c.setCost(get);

                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(CandidateContainer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            for (Candidate<E> c : this) {
                Double get = evaluate(c.getVariables());
                c.setCost(get);
                ParticleEncoding<E> p = (ParticleEncoding) c.getEncoding();
                if (p.getLocalBest().getCost() > get) {
                    p.setLocalBest(c);
                }
                if (get < getBestCandidate().getCost()) {
                    setBestCandidate(c);
                }
                c.setCost(evaluate(c.getVariables()));
            }
        }

        return this;
    }

    private class EvaluateCandidates implements Callable<Double> {

        private final Candidate<E> candidate;

        public EvaluateCandidates(Candidate<E> candidate) {
            this.candidate = candidate;
        }

        @Override
        public Double call() throws Exception {
            double evaluate = evaluate(candidate.getVariables());
            candidate.setCost(evaluate);
            ParticleEncoding<E> p = (ParticleEncoding) candidate.getEncoding();
            if (evaluate < p.getLocalBest().getCost()) {
                p.setLocalBest(candidate);
            }
            if (evaluate < getBestCandidate().getCost()) {
                setBestCandidate(candidate);
            }
            return evaluate;
        }
    }
}
