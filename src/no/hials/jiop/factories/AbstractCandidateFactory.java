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
import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.base.Evaluator;
import no.hials.jiop.base.candidates.encoding.BasicEncoding;

/**
 *
 * @author LarsIvar
 * @param <E>
 */
public abstract class AbstractCandidateFactory<E> {

    private final Evaluator<E> evaluator;

    private ExecutorService pool;
    private ExecutorCompletionService<Candidate> completionService;

    private boolean multiThreaded = false;

    public AbstractCandidateFactory(Evaluator<E> evaluator) {
        this.evaluator = evaluator;
    }

    public void setNumThreads(int numthreads) {
        if (numthreads <= 0 | numthreads > Runtime.getRuntime().availableProcessors()) {
            throw new IllegalArgumentException("The number of threads must be a number between 1 and the number of available processors, which on your computer is: " + Runtime.getRuntime().availableProcessors());
        }
        if (numthreads > 1) {
            pool = Executors.newFixedThreadPool(numthreads);
            completionService = new ExecutorCompletionService<>(pool);
            multiThreaded = true;
        } else {
            multiThreaded = false;
        }
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
    
    public Candidate<E> createCandidate(E elements) {
        BasicEncoding<E> encoding = wrap(elements);
        return new Candidate<>(encoding, evaluate(encoding.getElements()));
    }

    public double evaluate(E encoding) {
        return evaluator.evaluate(encoding);
    }

    public abstract BasicEncoding<E> random(int length);
    
    public abstract BasicEncoding<E> wrap(E original);

    public abstract BasicEncoding<E> neighbor(E original);

    public List<Candidate<E>> createCandidates(int howMany, int candidateLength) {
        List<Candidate<E>> candidates = new ArrayList<>(howMany);
        if (multiThreaded) {
            for (int i = 0; i < howMany; i++) {
                completionService.submit(new CreateCandidate(random(candidateLength)));
            }
            for (int i = 0; i < howMany; i++) {
                try {
                    Future<Candidate> take = completionService.take();
                    candidates.add(take.get());
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(AbstractCandidateFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            for (int i = 0; i < howMany; i++) {
                candidates.add(generateRandom(candidateLength));
            }
        }
        return candidates;
    }

    public void updateCost(List<Candidate<E>> candidates) {
        if (multiThreaded) {
            for (Candidate c : candidates) {
                completionService.submit(new EvaluateCandidates(c));
            }
             for (Candidate c : candidates) {
                try {
                    Future<Candidate> take = completionService.take();
                    take.get();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(AbstractCandidateFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            for (Candidate<E> c : candidates) {
                c.setCost(evaluate(c.getElements()));
            }
        }
    }

    private class CreateCandidate implements Callable<Candidate> {

        private final BasicEncoding<E> encoding;

        public CreateCandidate(BasicEncoding<E> encoding) {
            this.encoding = encoding;
        }

        @Override
        public Candidate call() throws Exception {
            return new Candidate<>(encoding, evaluate(encoding.getElements()));
        }
    }

    private class EvaluateCandidates implements Callable<Candidate> {

        private final Candidate<E> candidate;

        public EvaluateCandidates(Candidate<E> candidate) {
            this.candidate = candidate;
        }

        @Override
        public Candidate call() throws Exception {
            candidate.setCost(evaluate(candidate.getElements()));
            return candidate;
        }

    }
}
