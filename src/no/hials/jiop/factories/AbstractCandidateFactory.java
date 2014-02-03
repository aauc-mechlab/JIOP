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
package no.hials.jiop.factories;

import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.base.Evaluator;
import no.hials.jiop.base.candidates.BasicEncoding;

/**
 *
 * @author LarsIvar
 * @param <E>
 */
public abstract class AbstractCandidateFactory<E> {
    
    private final Evaluator<E> evaluator;

    public AbstractCandidateFactory(Evaluator<E> evaluator) {
        this.evaluator = evaluator;
    }
    
    public Candidate<E> generateRandom(int length) {
        BasicEncoding<E> random = random(length);
        return new Candidate<>(random, evaluator.evaluate(random.getElements()));
    }
    
    public Candidate<E> generateNeighbor(Candidate<E> original) {
        BasicEncoding<E> neighbor = neighbor(original.getElements());
        return new Candidate<>(neighbor, evaluator.evaluate(neighbor.getElements()));
    }
    
    public Candidate<E> createCandidate(BasicEncoding<E> encoding) {
        return new Candidate<>(encoding, evaluate(encoding.getElements()));
    }
    
    public double evaluate(E encoding) {
        return evaluator.evaluate(encoding);
    }

    public abstract BasicEncoding<E> random(int length);

    public abstract BasicEncoding<E> neighbor(E original);
}
