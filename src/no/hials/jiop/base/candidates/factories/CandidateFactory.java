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
package no.hials.jiop.base.candidates.factories;

import java.util.ArrayList;
import java.util.List;
import no.hials.jiop.base.MLMethod;
import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.base.candidates.encoding.Encoding;

/**
 *
 * @author Lars Ivar
 */
public abstract class CandidateFactory<E> {

   
    private final int encodingLength;
    
     private MLMethod<E> owner;
     
     
    public CandidateFactory(int encodingLength) {
        this.encodingLength = encodingLength;
    }

    public void setOwner(MLMethod<E> owner) {
        this.owner = owner;
    }

    public int getEncodingLength() {
        return encodingLength;
    }
    
    public Candidate<E> randomCandidate() {
        Encoding<E> random = randomEncoding();
        return new Candidate<>(random, owner.getEvaluator().evaluate(random.getVariables()));
    }

    public Candidate<E> neighborCandidate(Candidate<E> original) {
        Encoding<E> neighbor = neighborEncoding(original.getVariables());
        return new Candidate<>(neighbor, owner.getEvaluator().evaluate(neighbor.getVariables()));
    }

    public Candidate<E> toCandidate(Encoding<E> encoding) {
        return new Candidate<>(encoding, owner.getEvaluator().evaluate(encoding.getVariables()));
    }

    public Candidate<E> toCandidate(E elements) {
        Encoding<E> encoding = wrapVariables(elements);
        return new Candidate<>(encoding, owner.getEvaluator().evaluate(elements));
    }
    
    public Encoding<E> randomEncoding() {
        return randomEncoding(encodingLength);
    }
    
    public List<Candidate<E>> randomCandidates(int size) {
        List<Candidate<E>> random = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            random.add(randomCandidate());
        }
        return random;
    }
    
    public List<Encoding<E>> randomEncodings(int size) {
         List<Encoding<E>> random = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            random.add(randomEncoding());
        }
        return random;
    }
    
    public List<Candidate<E>> toCandidates(List<E> elements) {
        List<Candidate<E>> candidates = new ArrayList<>(elements.size());
        for (int i = 0; i < elements.size(); i++) {
            candidates.add(toCandidate(elements.get(i)));
        }
        return candidates;
    }

    protected abstract Encoding<E> randomEncoding(int length);

    protected abstract Encoding<E> wrapVariables(E original);

    protected abstract Encoding<E> neighborEncoding(E original);

}
