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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.hials.jiop.base.Evaluator;
import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.base.candidates.encoding.BasicEncoding;

/**
 *
 * @author Lars Ivar
 */
public abstract class CandidateContainer<E> implements Iterable<Candidate<E>> {

    private final int size;
    private final int candidateLength;
    private final Evaluator<E> evaluator;
    
    private Candidate<E> bestCandidate;

    protected ExecutorService pool;
    protected ExecutorCompletionService completionService;

    protected boolean multiThreaded = false;

    public CandidateContainer(int size, int candidateLength, Evaluator<E> evaluator, boolean multiThreaded) {
        this.size = size;
        this.candidateLength = candidateLength;
        this.evaluator = evaluator;
        if (multiThreaded) {
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            if (availableProcessors > 1) {
                this.pool = Executors.newFixedThreadPool(availableProcessors);
                this.completionService = new ExecutorCompletionService<>(pool);
                this.multiThreaded = multiThreaded;
            }
        }
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
        this.bestCandidate = null;
        List<Candidate<E>> candidates = new ArrayList<>(size);
        if (multiThreaded) {
            for (int i = 0; i < size; i++) {
                completionService.submit(new CreateCandidates(null));
            }
            for (int i = 0; i < size; i++) {
                try {
                    candidates.add((Candidate<E>) completionService.take().get());
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(CandidateContainer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                candidates.add(generateRandomCandidate());
            }
        }
        clearAndAddAll(candidates);
        setBestCandidate(sort().get(0));
        evaluateAll();
    }

    public void initialize(List<E> seed) {
        if (seed.size() > size) {
            throw new IllegalArgumentException("The number of seeds are greater than the preset container size");
        }
        this.bestCandidate = null;
        List<Candidate<E>> candidates = new ArrayList<>(size);
        int i = 0;
        if (multiThreaded) {
            for (E e : seed) {
                completionService.submit(new CreateCandidates(e));
            }
            for (E e : seed) {
                try {
                    candidates.add((Candidate<E>) completionService.take().get());
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(CandidateContainer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            i = seed.size();
            int j = i;
            for (; i < size; i++) {
                completionService.submit(new CreateCandidates(null));
            }
            for (; j < size; j++) {
                try {
                    candidates.add((Candidate<E>) completionService.take().get());
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(CandidateContainer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            for (; i < seed.size(); i++) {
                candidates.add(createCandidate(seed.get(i)));
            }
            for (; i < size; i++) {
                candidates.add(generateRandomCandidate());
            }
        }
        clearAndAddAll(candidates);
        setBestCandidate(sort().get(0));
        evaluateAll();
    }

    public CandidateContainer<E> evaluateAll() {
        if (multiThreaded) {
            for (Candidate c : this) {
                completionService.submit(new EvaluateCandidates(c));
            }
            for (Candidate c : this) {
                try {
                    Future<Candidate> take = completionService.take();
                    take.get();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(CandidateContainer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            for (Candidate<E> c : this) {
                c.setCost(evaluator.evaluate(c.getVariables()));
            }
        }
        
        return this;
    }

    public double getAverage() {
        double avg = 0;
        for (Candidate<E> c : this) {
            avg += c.getCost();
        }
        return avg / size();
    }

    public synchronized Candidate<E> getBestCandidate() {
        return bestCandidate;
    }

    public synchronized void setBestCandidate(Candidate<E> candidate) {
        if (this.bestCandidate == null) {
             this.bestCandidate = new Candidate<>(candidate);
        } else if (candidate.getCost() < this.bestCandidate.getCost()) {
            this.bestCandidate = new Candidate<>(candidate);
        }
    }

    public double evaluate(E encoding) {
        return evaluator.evaluate(encoding);
    }

    public Candidate<E> generateRandomCandidate() {
        BasicEncoding<E> random = randomEncoding(candidateLength);
        return new Candidate<>(random, evaluator.evaluate(random.getVariables()));
    }

    public Candidate<E> generateNeighborCandidate(Candidate<E> original) {
        BasicEncoding<E> neighbor = neighborEncoding(original.getVariables());
        return new Candidate<>(neighbor, evaluator.evaluate(neighbor.getVariables()));
    }

    public Candidate<E> createCandidate(BasicEncoding<E> encoding) {
        return new Candidate<>(encoding, evaluate(encoding.getVariables()));
    }

    public Candidate<E> createCandidate(E elements) {
        BasicEncoding<E> encoding = wrapVariables(elements);
        return new Candidate<>(encoding, evaluate(encoding.getVariables()));
    }

    public int candidateLength() {
        return candidateLength;
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Candidate c : this) {
            sb.append(c);
            if (i++ != size - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    private class EvaluateCandidates implements Callable<Candidate> {

        private final Candidate<E> candidate;

        public EvaluateCandidates(Candidate<E> candidate) {
            this.candidate = candidate;
        }

        @Override
        public Candidate call() throws Exception {
            candidate.setCost(evaluator.evaluate(candidate.getVariables()));
            return candidate;
        }
    }

    private class CreateCandidates implements Callable<Candidate> {

        private final E elements;

        public CreateCandidates(E elements) {
            this.elements = elements;
        }

        @Override
        public Candidate call() throws Exception {
            if (elements == null) {
                return createCandidate(randomEncoding(candidateLength));
            } else {
                return createCandidate(elements);
            }
        }
    }
}
