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
package no.hials.jiop.swarm;

import no.hials.jiop.base.AbstractEvaluator;
import no.hials.jiop.base.candidates.containers.CandidateContainer;
import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.base.MLMethod;
import no.hials.jiop.base.candidates.encoding.ParticleEncoding;

/**
 *
 * @author LarsIvar
 * @param <E>
 */
public class PSO<E> extends MLMethod<E> {

    private final double omega, c1, c2;

    public PSO(double omega, double c1, double c2, CandidateContainer<E> container, AbstractEvaluator<E> evaluator) {
        super(container, evaluator);
        this.omega = omega;
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    protected void doIteration() {
        for (Candidate<E> c : getContainer()) {
            ParticleEncoding<E> p = (ParticleEncoding<E>) c.getEncoding();
            p.update(omega, c1, c2, getBestCandidate().getVariables());
            double evaluate = evaluate(c.getVariables());
            c.setCost(evaluate);
            if (evaluate < p.getLocalBest().getCost()) {
                p.setLocalBest(c);
            }
            if (evaluate < getBestCandidate().getCost()) {
                setBestCandidate(c);
            }
        }
    }

    @Override
    public String getName() {
        return "Particle Swarm Optimization";
    }
}
