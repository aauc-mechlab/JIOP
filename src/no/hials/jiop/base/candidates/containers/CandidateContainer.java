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

import java.util.ArrayList;
import java.util.List;
import no.hials.jiop.base.MLMethod;
import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.base.candidates.encoding.BasicEncoding;

/**
 *
 * @author Lars Ivar
 */
public abstract class CandidateContainer<E> implements Iterable<Candidate<E>> {
    
    private MLMethod<E> owner;
    protected final int size, candidateLength;

    public CandidateContainer(int size, int candidateLength) {
        this.size = size;
        this.candidateLength = candidateLength;
    }

    public CandidateContainer<E> setOwner(MLMethod<E> owner) {
        this.owner = owner;
        return this;
    }
    
    public int size() {
        return size;
    }

    public int candidateLength() {
        return candidateLength;
    }
    
    public abstract BasicEncoding<E> randomEncoding(int length);

    public abstract BasicEncoding<E> wrapVariables(E original);

    public abstract BasicEncoding<E> neighborEncoding(E original);

    public abstract List<Candidate<E>> getCandidates();

    public abstract CandidateContainer<E> sort();

    public abstract Candidate<E> get(int i);

    public abstract void set(int index, Candidate<E> candidate);

    public abstract int indexOf(Candidate<E> candidate);

    public abstract void clearAndAddAll(List<Candidate<E>> candidates);

    public void initialize() {
        List<Candidate<E>> candidates = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            candidates.add(generateRandomCandidate());
        }
        clearAndAddAll(candidates);
        evaluateAll();
    }

    public void initialize(List<E> seed) {
        if (seed.size() > size) {
            throw new IllegalArgumentException("The number of seeds are greater than the preset container size");
        }
        List<Candidate<E>> candidates = new ArrayList<>(size);
        int i = 0;
        for (; i < seed.size(); i++) {
            candidates.add(createCandidate(seed.get(i)));
        }
        for (; i < size; i++) {
            candidates.add(generateRandomCandidate());
        }
        clearAndAddAll(candidates);
        evaluateAll();
    }

    public CandidateContainer<E> evaluateAll() {
        owner.getEvaluator().evaluateAll(this);
        return this;
    }

    public double getAverage() {
        double avg = 0;
        for (Candidate<E> c : this) {
            avg += c.getCost();
        }
        return avg / size();
    }

//    public  Candidate<E> getBestCandidate() {
//        return bestCandidate;
//    }

//    public  void setBestCandidate(Candidate<E> candidate) {
//        if (this.bestCandidate == null) {
//            this.bestCandidate = new Candidate<>(candidate);
//        } else if (candidate.getCost() < this.bestCandidate.getCost()) {
//            this.bestCandidate = new Candidate<>(candidate);
//        }
//    }

    public double evaluate(E encoding) {
        return owner.getEvaluator().evaluate(encoding);
    }

    public Candidate<E> generateRandomCandidate() {
        BasicEncoding<E> random = randomEncoding(candidateLength());
        return new Candidate<>(random, owner.getEvaluator().evaluate(random.getVariables()));
    }

    public Candidate<E> generateNeighborCandidate(Candidate<E> original) {
        BasicEncoding<E> neighbor = neighborEncoding(original.getVariables());
        return new Candidate<>(neighbor, owner.getEvaluator().evaluate(neighbor.getVariables()));
    }

    public Candidate<E> createCandidate(BasicEncoding<E> encoding) {
        return new Candidate<>(encoding, evaluate(encoding.getVariables()));
    }

    public Candidate<E> createCandidate(E elements) {
        BasicEncoding<E> encoding = wrapVariables(elements);
        return new Candidate<>(encoding, evaluate(encoding.getVariables()));
    }

//    public int candidateLength() {
//        return candidateLength;
//    }
//
//    public int size() {
//        return size;
//    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Candidate c : this) {
            sb.append(c);
            if (i++ != size() - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

//    private class EvaluateCandidates implements Callable<Candidate> {
//
//        private final Candidate<E> candidate;
//
//        public EvaluateCandidates(Candidate<E> candidate) {
//            this.candidate = candidate;
//        }
//
//        @Override
//        public Candidate call() throws Exception {
//            candidate.setCost(owner.getEvaluator().evaluate(candidate.getVariables()));
//            return candidate;
//        }
//    }
//
//    private class CreateCandidates implements Callable<Candidate> {
//
//        private final E elements;
//
//        public CreateCandidates(E elements) {
//            this.elements = elements;
//        }
//
//        @Override
//        public Candidate call() throws Exception {
//            if (elements == null) {
//                return createCandidate(randomEncoding(candidateLength()));
//            } else {
//                return createCandidate(elements);
//            }
//        }
//    }
}
